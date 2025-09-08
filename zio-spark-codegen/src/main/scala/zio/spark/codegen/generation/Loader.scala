package zio.spark.codegen.generation

import sbt.File
import sbt.Keys.Classpath

import zio.{IO, UIO, ZIO}
import zio.spark.codegen.generation.Error.*

import scala.io.{BufferedSource, Source}
import scala.meta.*

import java.io.InputStream
import java.util.jar.JarFile
import java.util.zip.ZipEntry

object Loader {

  /**
   * Find the source jar file from the actual classpath.
   *
   * We need, in SBT, to download spark with sources.
   */
  private def findSourceJar(moduleName: String, classpath: Classpath): ZIO[Logger, CodegenError, JarFile] = {
    val maybeFile: Option[File] =
      classpath
        .map(_.data)
        .collectFirst {
          case f if f.id.contains(moduleName) =>
            // Try to resolve the actual file path from the virtual file reference
            try
              // First try to get the file directly if it's a real file
              if (f.id.startsWith("/")) {
                // It's already an absolute path
                val sourcesPath = f.id.replaceFirst("\\.jar$", "-sources.jar")
                new File(sourcesPath)
              } else {
                // Handle virtual file references with placeholders
                val coursierCache =
                  scala.sys.props
                    .get("coursier.cache")
                    .orElse(Option(System.getenv("COURSIER_CACHE")))
                    .getOrElse(scala.util.Properties.userHome + "/Library/Caches/Coursier/v1")

                val resolvedPath = f.id.replace("${CSR_CACHE}", coursierCache)
                val sourcesPath  = resolvedPath.replaceFirst("\\.jar$", "-sources.jar")
                new File(sourcesPath)
              }
            catch {
              case _: Exception =>
                // Fallback: try common coursier locations
                val possiblePaths =
                  Seq(
                    scala.util.Properties.userHome + "/Library/Caches/Coursier/v1",
                    scala.util.Properties.userHome + "/.cache/coursier/v1",
                    scala.util.Properties.userHome + "/.coursier/cache/v1",
                    "/tmp/coursier-cache"
                  )

                val jarPath     = f.id.replace("${CSR_CACHE}", "")
                val sourcesPath = jarPath.replaceFirst("\\.jar$", "-sources.jar")

                possiblePaths
                  .map(cache => new File(cache + sourcesPath))
                  .find(_.exists())
                  .getOrElse(new File(f.id.replaceFirst("\\.jar$", "-sources.jar")))
            }
        }

    maybeFile match {
      case None => ZIO.fail(ModuleNotFoundError(moduleName))
      case Some(file) =>
        if (file.exists()) {
          ZIO
            .attempt(new JarFile(file))
            .mapError(e => SourceNotFoundError(file.getAbsolutePath, moduleName, e))
        } else {
          ZIO.fail(
            SourceNotFoundError(
              file.getAbsolutePath,
              moduleName,
              new java.io.FileNotFoundException(s"Source JAR not found: ${file.getAbsolutePath}")
            )
          )
        }
    }
  }

  /**
   * Read the source of particular file of a particular spark module
   * from sources and load the code in ScalaMeta.
   */
  def sourceFromClasspath(
      filePath: String,
      moduleName: String,
      classpath: Classpath
  ): ZIO[Logger, CodegenError, meta.Source] =
    ZIO.scoped {
      for {
        jar <- ZIO.acquireRelease(findSourceJar(moduleName, classpath))(x => ZIO.attempt(x.close()).ignore)
        _   <- Logger.info(s"Found $moduleName in ${jar.getName} for $filePath")
        source <-
          ZIO
            .attempt {
              val entry: ZipEntry = jar.getEntry(filePath)
              if (entry == null) {
                throw new java.io.FileNotFoundException(s"File '$filePath' not found in JAR ${jar.getName}")
              }
              val stream: InputStream     = jar.getInputStream(entry)
              val content: BufferedSource = Source.fromInputStream(stream)
              try
                content.getLines().mkString("\n").parse[meta.Source].get
              finally
                content.close()
            }
            .mapError(SourceNotFoundError(filePath, moduleName, _))
      } yield source
    }

  /**
   * Retrieves the content of a Scala file as Scala meta source.
   * @param file
   *   The file to retrieve content from
   */
  def sourceFromFile(file: File): IO[CodegenError, meta.Source] =
    for {
      content <- ZIO.attempt(sbt.IO.read(file)).mapError(FileReadingError(file.getPath, _))
      source  <- ZIO.attempt(content.parse[meta.Source].get).mapError(ContentIsNotSourceError(file.getPath, _))
    } yield source

  /**
   * Retrieves the content of a Scala file as Scala meta source, returns
   * None if the file doesn't not exist.
   */
  def optionalSourceFromFile(file: File): IO[CodegenError, Option[meta.Source]] =
    sourceFromFile(file).foldZIO(
      failure = {
        case FileReadingError(_, _) => ZIO.succeed(None)
        case e                      => ZIO.fail(e)
      },
      success = source => ZIO.succeed(Some(source))
    )

}
