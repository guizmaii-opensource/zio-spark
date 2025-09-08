package example

import org.apache.spark.sql.Row
import zio._
import zio.spark.experimental
import zio.spark.experimental.{Pipeline, ZIOSparkAppDefault}
import zio.spark.parameter._
import zio.spark.sql._
import zio.spark.sql.implicits._

import java.nio.file.Path

object SimpleApp extends ZIOSparkAppDefault {

  import zio.spark.sql.TryAnalysis.syntax.throwAnalysisException

  final case class Person(name: String, age: Int)

  val filePath: String = {
    val url = this.getClass.getClassLoader.getResource("data.csv")
    Path.of(url.toURI).toFile.getAbsolutePath
  }

  def read: SIO[DataFrame] = SparkSession.read.schema[Person].withHeader.withDelimiter(";").csv(filePath)

  def transform(inputDs: DataFrame): Dataset[Person] = inputDs.as[Person]

  def output(transformedDs: Dataset[Person]): Task[Option[Person]] = transformedDs.headOption

  val pipeline: Pipeline[Row, Person, Option[Person]] = experimental.Pipeline(read, transform, output)

  val job: ZIO[SparkSession, Throwable, Unit] =
    for {
      maybePeople <- pipeline.run
      _ <-
        maybePeople match {
          case None => Console.printLine("There is nobody :(.")
          case Some(p) => Console.printLine(s"The first person's name is ${p.name}.")
        }
    } yield ()

  private val session = SparkSession.builder.master(localAllNodes).appName("app").asLayer

  override def run: ZIO[ZIOAppArgs, Any, Any] = job.provide(session)
}
