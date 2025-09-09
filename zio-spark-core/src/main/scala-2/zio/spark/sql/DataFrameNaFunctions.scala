/**
 * /!\ Warning /!\
 *
 * This file is generated using zio-spark-codegen, you should not edit
 * this file directly.
 */

package zio.spark.sql

import org.apache.spark.sql.{DataFrameNaFunctions => UnderlyingDataFrameNaFunctions, Dataset => UnderlyingDataset}

final case class DataFrameNaFunctions(underlying: UnderlyingDataFrameNaFunctions) { self =>

  /** Unpack the underlying DataFrameNaFunctions into a DataFrame. */
  def unpack[U](f: UnderlyingDataFrameNaFunctions => UnderlyingDataset[U]): Dataset[U] = Dataset(f(underlying))

  /**
   * Unpack the underlying DataFrameNaFunctions into a DataFrame, it is
   * used for transformations that can fail due to an AnalysisException.
   */
  def unpackWithAnalysis[U](f: UnderlyingDataFrameNaFunctions => UnderlyingDataset[U]): TryAnalysis[Dataset[U]] =
    TryAnalysis(unpack(f))

  // Generated functions coming from spark
  /** @inheritdoc */
  def drop: DataFrame = unpack(_.drop())

  /** @inheritdoc */
  def drop(how: String): DataFrame = unpack(_.drop(how))

  /** @inheritdoc */
  def drop(minNonNulls: Int): DataFrame = unpack(_.drop(minNonNulls))

  /** @inheritdoc */
  def fill(value: Long): DataFrame = unpack(_.fill(value))

  /** @inheritdoc */
  def fill(value: Double): DataFrame = unpack(_.fill(value))

  /** @inheritdoc */
  def fill(value: String): DataFrame = unpack(_.fill(value))

  /** @inheritdoc */
  def fill(value: Boolean): DataFrame = unpack(_.fill(value))

  /** @inheritdoc */
  def fill(valueMap: Map[String, Any]): DataFrame = unpack(_.fill(valueMap))

  // ===============

  /** @inheritdoc */
  def drop(cols: Seq[String]): TryAnalysis[DataFrame] = unpackWithAnalysis(_.drop(cols))

  /** @inheritdoc */
  def drop(how: String, cols: Seq[String]): TryAnalysis[DataFrame] = unpackWithAnalysis(_.drop(how, cols))

  /** @inheritdoc */
  def drop(minNonNulls: Int, cols: Seq[String]): TryAnalysis[DataFrame] = unpackWithAnalysis(_.drop(minNonNulls, cols))

  /** @inheritdoc */
  def fill(value: Long, cols: Seq[String]): TryAnalysis[DataFrame] = unpackWithAnalysis(_.fill(value, cols))

  /** @inheritdoc */
  def fill(value: Double, cols: Seq[String]): TryAnalysis[DataFrame] = unpackWithAnalysis(_.fill(value, cols))

  /** @inheritdoc */
  def fill(value: String, cols: Seq[String]): TryAnalysis[DataFrame] = unpackWithAnalysis(_.fill(value, cols))

  /** @inheritdoc */
  def fill(value: Boolean, cols: Seq[String]): TryAnalysis[DataFrame] = unpackWithAnalysis(_.fill(value, cols))

  /** @inheritdoc */
  def replace[T](col: String, replacement: Map[T, T]): TryAnalysis[DataFrame] =
    unpackWithAnalysis(_.replace[T](col, replacement))

  /** @inheritdoc */
  def replace[T](cols: Seq[String], replacement: Map[T, T]): TryAnalysis[DataFrame] =
    unpackWithAnalysis(_.replace[T](cols, replacement))
}
