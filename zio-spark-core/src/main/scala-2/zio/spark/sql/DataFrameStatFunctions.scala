/**
 * /!\ Warning /!\
 *
 * This file is generated using zio-spark-codegen, you should not edit
 * this file directly.
 */

package zio.spark.sql

import org.apache.spark.sql.{
  Column,
  DataFrameStatFunctions => UnderlyingDataFrameStatFunctions,
  Dataset => UnderlyingDataset
}
import org.apache.spark.util.sketch.{BloomFilter, CountMinSketch}

final case class DataFrameStatFunctions(underlying: UnderlyingDataFrameStatFunctions) { self =>

  /** Unpack the underlying DataFrameStatFunctions into a DataFrame. */
  def unpack[U](f: UnderlyingDataFrameStatFunctions => UnderlyingDataset[U]): Dataset[U] = Dataset(f(underlying))

  /**
   * Unpack the underlying DataFrameStatFunctions into a DataFrame, it
   * is used for transformations that can fail due to an
   * AnalysisException.
   */
  def unpackWithAnalysis[U](f: UnderlyingDataFrameStatFunctions => UnderlyingDataset[U]): TryAnalysis[Dataset[U]] =
    TryAnalysis(unpack(f))

  /** Applies a transformation to the underlying DataFrameStatFunctions. */
  def transformation(f: UnderlyingDataFrameStatFunctions => UnderlyingDataFrameStatFunctions): DataFrameStatFunctions =
    DataFrameStatFunctions(f(underlying))

  /**
   * Applies a transformation to the underlying DataFrameStatFunctions,
   * it is used for transformations that can fail due to an
   * AnalysisException.
   */
  def transformationWithAnalysis(
      f: UnderlyingDataFrameStatFunctions => UnderlyingDataFrameStatFunctions
  ): TryAnalysis[DataFrameStatFunctions] = TryAnalysis(transformation(f))

  /** Applies an action to the underlying DataFrameStatFunctions. */
  def get[U](f: UnderlyingDataFrameStatFunctions => U): U = f(underlying)

  /**
   * Applies an action to the underlying DataFrameStatFunctions, it is
   * used for transformations that can fail due to an AnalysisException.
   */
  def getWithAnalysis[U](f: UnderlyingDataFrameStatFunctions => U): TryAnalysis[U] = TryAnalysis(f(underlying))

  // Generated functions coming from spark
  /** @inheritdoc */
  def corr(col1: String, col2: String, method: String): TryAnalysis[Double] =
    getWithAnalysis(_.corr(col1, col2, method))

  /** @inheritdoc */
  def cov(col1: String, col2: String): TryAnalysis[Double] = getWithAnalysis(_.cov(col1, col2))

  // ===============

  /** @inheritdoc */
  def crosstab(col1: String, col2: String): TryAnalysis[DataFrame] = unpackWithAnalysis(_.crosstab(col1, col2))

  /** @inheritdoc */
  def freqItems(cols: Seq[String], support: Double): TryAnalysis[DataFrame] =
    unpackWithAnalysis(_.freqItems(cols, support))

  /** @inheritdoc */
  def freqItems(cols: Seq[String]): TryAnalysis[DataFrame] = unpackWithAnalysis(_.freqItems(cols))

  /** @inheritdoc */
  def sampleBy[T](col: String, fractions: Map[T, Double], seed: Long): TryAnalysis[DataFrame] =
    unpackWithAnalysis(_.sampleBy[T](col, fractions, seed))

  /** @inheritdoc */
  def sampleBy[T](col: Column, fractions: Map[T, Double], seed: Long): TryAnalysis[DataFrame] =
    unpackWithAnalysis(_.sampleBy[T](col, fractions, seed))
}
