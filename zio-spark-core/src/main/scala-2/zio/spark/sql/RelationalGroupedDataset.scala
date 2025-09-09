/**
 * /!\ Warning /!\
 *
 * This file is generated using zio-spark-codegen, you should not edit
 * this file directly.
 */

package zio.spark.sql

import org.apache.spark.sql.{
  Column,
  Dataset => UnderlyingDataset,
  Encoder,
  KeyValueGroupedDataset => UnderlyingKeyValueGroupedDataset,
  RelationalGroupedDataset => UnderlyingRelationalGroupedDataset
}

final case class RelationalGroupedDataset(underlying: UnderlyingRelationalGroupedDataset) { self =>
  // scalafix:off
  implicit private def liftKeyValueGroupedDataset[K, V](
      x: UnderlyingKeyValueGroupedDataset[K, V]
  ): KeyValueGroupedDataset[K, V] = KeyValueGroupedDataset(x)
  // scalafix:on

  /** Unpack the underlying RelationalGroupedDataset into a DataFrame. */
  def unpack[U](f: UnderlyingRelationalGroupedDataset => UnderlyingDataset[U]): Dataset[U] = Dataset(f(underlying))

  /**
   * Unpack the underlying RelationalGroupedDataset into a DataFrame, it
   * is used for transformations that can fail due to an
   * AnalysisException.
   */
  def unpackWithAnalysis[U](f: UnderlyingRelationalGroupedDataset => UnderlyingDataset[U]): TryAnalysis[Dataset[U]] =
    TryAnalysis(unpack(f))

  /**
   * Applies a transformation to the underlying
   * RelationalGroupedDataset.
   */
  def transformation(
      f: UnderlyingRelationalGroupedDataset => UnderlyingRelationalGroupedDataset
  ): RelationalGroupedDataset = RelationalGroupedDataset(f(underlying))

  /**
   * Applies a transformation to the underlying
   * RelationalGroupedDataset, it is used for transformations that can
   * fail due to an AnalysisException.
   */
  def transformationWithAnalysis(
      f: UnderlyingRelationalGroupedDataset => UnderlyingRelationalGroupedDataset
  ): TryAnalysis[RelationalGroupedDataset] = TryAnalysis(transformation(f))

  /** Applies an action to the underlying RelationalGroupedDataset. */
  def get[U](f: UnderlyingRelationalGroupedDataset => U): U = f(underlying)

  /**
   * Applies an action to the underlying RelationalGroupedDataset, it is
   * used for transformations that can fail due to an AnalysisException.
   */
  def getWithAnalysis[U](f: UnderlyingRelationalGroupedDataset => U): TryAnalysis[U] = TryAnalysis(f(underlying))

  // Generated functions coming from spark
  /** @inheritdoc */
  def as[K: Encoder, T: Encoder]: TryAnalysis[KeyValueGroupedDataset[K, T]] = getWithAnalysis(_.as[K, T])

  // ===============

  /** @inheritdoc */
  def pivot(pivotColumn: String): TryAnalysis[RelationalGroupedDataset] =
    transformationWithAnalysis(_.pivot(pivotColumn))

  /** @inheritdoc */
  def pivot(pivotColumn: String, values: Seq[Any]): TryAnalysis[RelationalGroupedDataset] =
    transformationWithAnalysis(_.pivot(pivotColumn, values))

  /** @inheritdoc */
  def pivot(pivotColumn: Column): TryAnalysis[RelationalGroupedDataset] =
    transformationWithAnalysis(_.pivot(pivotColumn))

  /** @inheritdoc */
  def pivot(pivotColumn: Column, values: Seq[Any]): TryAnalysis[RelationalGroupedDataset] =
    transformationWithAnalysis(_.pivot(pivotColumn, values))

  // ===============

  /** @inheritdoc */
  def count: DataFrame = unpack(_.count())

  // ===============

  /** @inheritdoc */
  def agg(aggExpr: (String, String), aggExprs: (String, String)*): TryAnalysis[DataFrame] =
    unpackWithAnalysis(_.agg(aggExpr, aggExprs: _*))

  /** @inheritdoc */
  def agg(exprs: Map[String, String]): TryAnalysis[DataFrame] = unpackWithAnalysis(_.agg(exprs))

  /** @inheritdoc */
  def agg(expr: Column, exprs: Column*): TryAnalysis[DataFrame] = unpackWithAnalysis(_.agg(expr, exprs: _*))

  /** @inheritdoc */
  def avg(colNames: String*): TryAnalysis[DataFrame] = unpackWithAnalysis(_.avg(colNames: _*))

  /** @inheritdoc */
  def max(colNames: String*): TryAnalysis[DataFrame] = unpackWithAnalysis(_.max(colNames: _*))

  /** @inheritdoc */
  def mean(colNames: String*): TryAnalysis[DataFrame] = unpackWithAnalysis(_.mean(colNames: _*))

  /** @inheritdoc */
  def min(colNames: String*): TryAnalysis[DataFrame] = unpackWithAnalysis(_.min(colNames: _*))

  /** @inheritdoc */
  def sum(colNames: String*): TryAnalysis[DataFrame] = unpackWithAnalysis(_.sum(colNames: _*))

  // ===============

  // Ignored methods
  //
  // [[org.apache.spark.sql.RelationalGroupedDataset.toString]]
}
