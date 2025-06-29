package zio.spark.codegen.generation.template.instance

import zio.spark.codegen.ScalaBinaryVersion
import zio.spark.codegen.generation.MethodType
import zio.spark.codegen.generation.MethodType.{GetWithAnalysis, Unpack, UnpackWithAnalysis}
import zio.spark.codegen.generation.template.{Helper, Template}
import zio.spark.codegen.generation.template.Helper.*
import zio.spark.codegen.structure.Method

case object RelationalGroupedDatasetTemplate extends Template.Default {
  override def name: String = "RelationalGroupedDataset"

  override def imports(scalaVersion: ScalaBinaryVersion): Option[String] =
    Some {
      """import org.apache.spark.sql.{
        |  Column,
        |  Encoder,
        |  Dataset => UnderlyingDataset,
        |  RelationalGroupedDataset => UnderlyingRelationalGroupedDataset,
        |  KeyValueGroupedDataset => UnderlyingKeyValueGroupedDataset,
        |}
        |""".stripMargin
    }

  override def implicits(scalaVersion: ScalaBinaryVersion): Option[String] =
    Some {
      s"""implicit private def liftKeyValueGroupedDataset[K, V](
         |  x: UnderlyingKeyValueGroupedDataset[K, V]
         |): KeyValueGroupedDataset[K, V] = KeyValueGroupedDataset(x)""".stripMargin
    }

  override def helpers: Helper = unpacks && transformations && gets

  override def getMethodType(method: Method): MethodType = {
    val baseMethodType = super.getMethodType(method)

    method.name match {
      case "as"                         => GetWithAnalysis
      case "count"                      => Unpack
      case "min" | "max" | "withColumn" => UnpackWithAnalysis
      case _                            => baseMethodType
    }
  }
}
