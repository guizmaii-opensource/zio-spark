package zio.spark.codegen.generation

/**
 * A module describes a location of the classpath.
 *
 * @param name
 *   The name of the library
 * @param hierarchy
 *   The package hierarchy of the library
 * @param sourceSubPath
 *   Optional sub-path within the source JAR where implementation
 *   classes live (e.g. "classic" for Spark 4 SQL classes)
 * @param apiModule
 *   Optional secondary module containing abstract API definitions (e.g.
 *   spark-sql-api in Spark 4)
 */
final case class Module(
    name:          String,
    hierarchy:     String,
    sourceSubPath: Option[String] = None,
    apiModule:     Option[(String, String)] = None
) {

  /** Convert the hierarchy into a stringify path. */
  def path: String = hierarchy.replace(".", "/")

  /** Path used to look up source files in the source JAR. */
  def sourcePath: String = sourceSubPath.map(sub => s"$path/$sub").getOrElse(path)

  /** Convert the stringify spark path into a zio spark path. */
  def zioPath: String = path.replace("org/apache/spark", "zio/spark")

  def zioHierarchy: String = hierarchy.replace("org.apache.spark", "zio.spark")
}

object Module {
  val baseModule: Module = Module("spark-core", "org.apache.spark")
  val coreModule: Module = Module("spark-core", "org.apache.spark.rdd")
  val sqlModule: Module  =
    Module("spark-sql", "org.apache.spark.sql", Some("classic"), Some(("spark-sql-api", "org/apache/spark/sql")))
}
