package example

import org.apache.spark.sql.Row

import zio._
import zio.spark.experimental
import zio.spark.experimental.{Pipeline, ZIOSparkAppDefault}
import zio.spark.parameter._
import zio.spark.sql._
import zio.spark.sql.implicits._

object UsingOlderSparkVersion extends ZIOSparkAppDefault {

  import zio.spark.sql.TryAnalysis.syntax.throwAnalysisException

  final case class Person(name: String, age: Int)

  def resourcePath(fileName: String): Path = Paths.get(this.getClass.getClassLoader.getResource(fileName).toURI)

  private val readfilePath: Task[String] =
    ZIO.attempt {
      resourcePath("data.csv").toFile.getAbsolutePath
    }

  def read(filePath: String): SIO[DataFrame] = SparkSession.read.inferSchema.withHeader.withDelimiter(";").csv(filePath)

  def transform(inputDs: DataFrame): Dataset[Person] = inputDs.as[Person]

  def output(transformedDs: Dataset[Person]): Task[Option[Person]] = transformedDs.headOption

  def pipeline(filePath: String): Pipeline[Row, Person, Option[Person]] = experimental.Pipeline(read(filePath), transform, output)

  val job: ZIO[SparkSession, Throwable, Unit] =
    for {
      filePath <- readfilePath
      _ <- ZIO.debug(s"File path: $filePath")
      maybePeople <- pipeline(filePath).run
      _ <-
        maybePeople match {
          case None => Console.printLine("There is nobody :(.")
          case Some(p) => Console.printLine(s"The first person's name is ${p.name}.")
        }
    } yield ()

  private val session = SparkSession.builder.master(localAllNodes).appName("app").asLayer

  override def run: ZIO[ZIOAppArgs, Any, Any] = job.provide(session)
}
