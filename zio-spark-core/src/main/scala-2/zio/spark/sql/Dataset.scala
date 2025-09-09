/**
 * /!\ Warning /!\
 *
 * This file is generated using zio-spark-codegen, you should not edit
 * this file directly.
 */

package zio.spark.sql

import org.apache.spark.sql
import org.apache.spark.sql.{
  Column,
  DataFrameNaFunctions => UnderlyingDataFrameNaFunctions,
  DataFrameStatFunctions => UnderlyingDataFrameStatFunctions,
  Dataset => UnderlyingDataset,
  Encoder,
  KeyValueGroupedDataset => UnderlyingKeyValueGroupedDataset,
  MergeIntoWriter,
  RelationalGroupedDataset => UnderlyingRelationalGroupedDataset,
  Row,
  Sniffer,
  TypedColumn
}
import org.apache.spark.sql.Observation
import org.apache.spark.sql.execution.ExplainMode
import org.apache.spark.sql.types.Metadata
import org.apache.spark.sql.types.StructType
import org.apache.spark.storage.StorageLevel

import zio._
import zio.spark.rdd._
import zio.spark.sql.streaming.DataStreamWriter

import scala.jdk.CollectionConverters._
import scala.reflect.runtime.universe.TypeTag

import java.io.IOException
import java.util

final case class Dataset[T](underlying: UnderlyingDataset[T]) { self =>
  // scalafix:off
  implicit private def lift[U](x: UnderlyingDataset[U]): Dataset[U]                        = Dataset(x)
  implicit private def iteratorConversion[U](iterator: java.util.Iterator[U]): Iterator[U] = iterator.asScala
  implicit private def liftDataFrameNaFunctions(x: UnderlyingDataFrameNaFunctions): DataFrameNaFunctions =
    DataFrameNaFunctions(x)
  implicit private def liftDataFrameStatFunctions(x: UnderlyingDataFrameStatFunctions): DataFrameStatFunctions =
    DataFrameStatFunctions(x)
  implicit private def liftRelationalGroupedDataset[U](
      x: UnderlyingRelationalGroupedDataset
  ): RelationalGroupedDataset = RelationalGroupedDataset(x)
  implicit private def liftKeyValueGroupedDataset[K, V](
      x: UnderlyingKeyValueGroupedDataset[K, V]
  ): KeyValueGroupedDataset[K, V] = KeyValueGroupedDataset(x)
  // scalafix:on

  /** Applies an action to the underlying Dataset. */
  def action[U](f: UnderlyingDataset[T] => U)(implicit trace: Trace): Task[U] = ZIO.attempt(get(f))

  /** Applies a transformation to the underlying Dataset. */
  def transformation[TNew](f: UnderlyingDataset[T] => UnderlyingDataset[TNew]): Dataset[TNew] = Dataset(f(underlying))

  /**
   * Applies a transformation to the underlying Dataset, it is used for
   * transformations that can fail due to an AnalysisException.
   */
  def transformationWithAnalysis[TNew](f: UnderlyingDataset[T] => UnderlyingDataset[TNew]): TryAnalysis[Dataset[TNew]] =
    TryAnalysis(transformation(f))

  /** Applies an action to the underlying Dataset. */
  def get[U](f: UnderlyingDataset[T] => U): U = f(underlying)

  /**
   * Applies an action to the underlying Dataset, it is used for
   * transformations that can fail due to an AnalysisException.
   */
  def getWithAnalysis[U](f: UnderlyingDataset[T] => U): TryAnalysis[U] = TryAnalysis(f(underlying))

  // Handmade functions specific to zio-spark
  /**
   * Prints the plans (logical and physical) with a format specified by
   * a given explain mode.
   *
   * @param mode
   *   specifies the expected output format of plans. <ul> <li>`simple`
   *   Print only a physical plan.</li> <li>`extended`: Print both
   *   logical and physical plans.</li> <li>`codegen`: Print a physical
   *   plan and generated codes if they are available.</li> <li>`cost`:
   *   Print a logical plan and statistics if they are available.</li>
   *   <li>`formatted`: Split explain output into two sections: a
   *   physical plan outline and node details.</li> </ul>
   * @group basic
   * @since 3.0.0
   */
  def explain(mode: String)(implicit trace: Trace): SIO[Unit] = explain(ExplainMode.fromString(mode))

  /**
   * Prints the plans (logical and physical) with a format specified by
   * a given explain mode.
   *
   * @group basic
   * @since 3.0.0
   */
  def explain(mode: ExplainMode)(implicit trace: Trace): SIO[Unit] =
    for {
      ss   <- ZIO.service[SparkSession]
      plan <- ss.withActive(underlying.queryExecution.explainString(mode))
      _    <- Console.printLine(plan)
    } yield ()

  /** Alias for [[headOption]]. */
  def firstOption(implicit trace: Trace): Task[Option[T]] = headOption

  // template:on
  /** Transforms the Dataset into a RelationalGroupedDataset. */
  def group(f: UnderlyingDataset[T] => UnderlyingRelationalGroupedDataset): RelationalGroupedDataset =
    RelationalGroupedDataset(f(underlying))

  /**
   * Groups the Dataset using the specified columns, so we ca run
   * aggregations on them.
   *
   * See [[UnderlyingDataset.groupBy]] for more information.
   */
  def groupBy(cols: Column*): RelationalGroupedDataset = group(_.groupBy(cols: _*))

  /** Takes the first element of a dataset or None. */
  def headOption(implicit trace: Trace): Task[Option[T]] = head(1).map(_.headOption)

  // template:on
  /** Alias for [[tail]]. */
  def last(implicit trace: Trace): Task[T] = tail

  /** Alias for [[tailOption]]. */
  def lastOption(implicit trace: Trace): Task[Option[T]] = tailOption

  /**
   * Prints the schema to the console in a nice tree format.
   *
   * @group basic
   * @since 1.6.0
   */
  def printSchema(implicit trace: Trace): IO[IOException, Unit] = printSchema(Int.MaxValue)

  /**
   * Prints the schema up to the given level to the console in a nice
   * tree format.
   *
   * @group basic
   * @since 3.0.0
   */
  def printSchema(level: Int)(implicit trace: Trace): IO[IOException, Unit] =
    Console.printLine(schema.treeString(level))

  /**
   * Transform the dataset into a [[RDD]].
   *
   * See [[UnderlyingDataset.rdd]] for more information.
   */
  def rdd: RDD[T] = RDD(get(_.rdd))

  /**
   * Displays the top rows of Dataset in a tabular form. Strings with
   * more than 20 characters will be truncated.
   *
   * See [[UnderlyingDataset.show]] for more information.
   */
  def show(numRows: Int)(implicit trace: Trace): IO[IOException, Unit] = show(numRows, truncate = true)

  /**
   * Displays the top 20 rows of Dataset in a tabular form. Strings with
   * more than 20 characters will be truncated.
   *
   * See [[UnderlyingDataset.show]] for more information.
   */
  def show(implicit trace: Trace): IO[IOException, Unit] = show(20)

  /**
   * Displays the top 20 rows of Dataset in a tabular form.
   *
   * See [[UnderlyingDataset.show]] for more information.
   */
  def show(truncate: Boolean)(implicit trace: Trace): IO[IOException, Unit] = show(20, truncate)

  /**
   * Displays the top rows of Dataset in a tabular form.
   *
   * See [[UnderlyingDataset.show]] for more information.
   */
  def show(numRows: Int, truncate: Boolean)(implicit trace: Trace): IO[IOException, Unit] = {
    val trunc         = if (truncate) 20 else 0
    val stringifiedDf = Sniffer.datasetShowString(underlying, numRows, truncate = trunc)
    Console.printLine(stringifiedDf)
  }

  /**
   * Computes specified statistics for numeric and string columns.
   *
   * See [[org.apache.spark.sql.Dataset.summary]] for more information.
   */
  def summary(statistics: Statistics*)(implicit d: DummyImplicit): DataFrame =
    self.summary(statistics.map(_.toString): _*)

  /**
   * Takes the last element of a dataset or throws an exception.
   *
   * See [[Dataset.tail]] for more information.
   */
  def tail(implicit trace: Trace): Task[T] = self.tail(1).map(_.head)

  /** Takes the last element of a dataset or None. */
  def tailOption(implicit trace: Trace): Task[Option[T]] = self.tail(1).map(_.headOption)

  /** Alias for [[tail]]. */
  def takeRight(n: Int)(implicit trace: Trace): Task[Seq[T]] = self.tail(n)

  /**
   * Chains custom transformations.
   *
   * See [[UnderlyingDataset.transform]] for more information.
   */
  def transform[U](t: Dataset[T] => Dataset[U]): Dataset[U] = t(self)

  /**
   * Mark the Dataset as non-persistent, and remove all blocks for it
   * from memory and disk in a blocking way.
   *
   * See [[UnderlyingDataset.unpersist]] for more information.
   */
  def unpersistBlocking(implicit trace: Trace): UIO[Dataset[T]] =
    ZIO.succeed(transformation(_.unpersist(blocking = true)))

  /** Alias for [[filter]]. */
  def where(f: T => Boolean): Dataset[T] = filter(f)

  /** Create a DataFrameWriter from this dataset. */
  def write: DataFrameWriter[T] = DataFrameWriter(self)

  /** Create a DataStreamWriter from this dataset. */
  def writeStream: DataStreamWriter[T] = DataStreamWriter(self)

  // Generated functions coming from spark
  /** @inheritdoc */
  def groupByKey[K: Encoder](func: T => K): KeyValueGroupedDataset[K, T] = get(_.groupByKey[K](func))

  // scalastyle:on println
  /** @inheritdoc */
  def na: DataFrameNaFunctions = get(_.na)

  /** @inheritdoc */
  def schema: StructType = get(_.schema)

  /** @inheritdoc */
  def stat: DataFrameStatFunctions = get(_.stat)

  // ===============

  /** @inheritdoc */
  def col(colName: String): TryAnalysis[Column] = getWithAnalysis(_.col(colName))

  /** @inheritdoc */
  def colRegex(colName: String): TryAnalysis[Column] = getWithAnalysis(_.colRegex(colName))

  /** @inheritdoc */
  def cube(cols: Column*): TryAnalysis[RelationalGroupedDataset] = getWithAnalysis(_.cube(cols: _*))

  /** @inheritdoc */
  def cube(col1: String, cols: String*): TryAnalysis[RelationalGroupedDataset] = getWithAnalysis(_.cube(col1, cols: _*))

  /** @inheritdoc */
  def rollup(cols: Column*): TryAnalysis[RelationalGroupedDataset] = getWithAnalysis(_.rollup(cols: _*))

  /** @inheritdoc */
  def rollup(col1: String, cols: String*): TryAnalysis[RelationalGroupedDataset] =
    getWithAnalysis(_.rollup(col1, cols: _*))

  // ===============

  /** @inheritdoc */
  def collect(implicit trace: Trace): Task[Seq[T]] = action(_.collect().toSeq)

  /** @inheritdoc */
  def count(implicit trace: Trace): Task[Long] = action(_.count())

  /** @inheritdoc */
  def foreachPartition(f: Iterator[T] => Unit)(implicit trace: Trace): Task[Unit] = action(_.foreachPartition(f))

  /** @inheritdoc */
  def head(n: => Int)(implicit trace: Trace): Task[Seq[T]] = action(_.head(n).toSeq)

  /** @inheritdoc */
  def isEmpty(implicit trace: Trace): Task[Boolean] = action(_.isEmpty)

  /** @inheritdoc */
  def reduce(func: (T, T) => T)(implicit trace: Trace): Task[T] = action(_.reduce(func))

  /** @inheritdoc */
  def tail(n: => Int)(implicit trace: Trace): Task[Seq[T]] = action(_.tail(n).toSeq)

  /** @inheritdoc */
  def toLocalIterator(implicit trace: Trace): Task[Iterator[T]] = action(_.toLocalIterator())

  // ===============

  /** @inheritdoc */
  def cache(implicit trace: Trace): Task[Dataset[T]] = action(_.cache())

  /** @inheritdoc */
  def checkpoint(implicit trace: Trace): Task[Dataset[T]] = action(_.checkpoint())

  /** @inheritdoc */
  def checkpoint(eager: => Boolean)(implicit trace: Trace): Task[Dataset[T]] = action(_.checkpoint(eager))

  /** @inheritdoc */
  def inputFiles(implicit trace: Trace): Task[Seq[String]] = action(_.inputFiles.toSeq)

  /** @inheritdoc */
  def isLocal(implicit trace: Trace): Task[Boolean] = action(_.isLocal)

  /** @inheritdoc */
  def isStreaming(implicit trace: Trace): Task[Boolean] = action(_.isStreaming)

  /** @inheritdoc */
  def localCheckpoint(implicit trace: Trace): Task[Dataset[T]] = action(_.localCheckpoint())

  /** @inheritdoc */
  def localCheckpoint(eager: => Boolean)(implicit trace: Trace): Task[Dataset[T]] = action(_.localCheckpoint(eager))

  /** @inheritdoc */
  def localCheckpoint(eager: => Boolean, storageLevel: => StorageLevel)(implicit trace: Trace): Task[Dataset[T]] =
    action(_.localCheckpoint(eager, storageLevel))

  /** @inheritdoc */
  def persist(implicit trace: Trace): Task[Dataset[T]] = action(_.persist())

  /** @inheritdoc */
  def persist(newLevel: => StorageLevel)(implicit trace: Trace): Task[Dataset[T]] = action(_.persist(newLevel))

  /** @inheritdoc */
  def storageLevel(implicit trace: Trace): Task[StorageLevel] = action(_.storageLevel)

  /** @inheritdoc */
  def unpersist(blocking: => Boolean)(implicit trace: Trace): Task[Dataset[T]] = action(_.unpersist(blocking))

  /** @inheritdoc */
  def unpersist(implicit trace: Trace): Task[Dataset[T]] = action(_.unpersist())

  // ===============

  /** @inheritdoc */
  def alias(alias: String): Dataset[T] = transformation(_.alias(alias))

  /** @inheritdoc */
  def alias(alias: Symbol): Dataset[T] = transformation(_.alias(alias))

  /** @inheritdoc */
  def as(alias: String): Dataset[T] = transformation(_.as(alias))

  /** @inheritdoc */
  def as(alias: Symbol): Dataset[T] = transformation(_.as(alias))

  /** @inheritdoc */
  def coalesce(numPartitions: Int): Dataset[T] = transformation(_.coalesce(numPartitions))

  /** @inheritdoc */
  def crossJoin(right: sql.Dataset[_]): DataFrame = transformation(_.crossJoin(right))

  /** @inheritdoc */
  def distinct: Dataset[T] = transformation(_.distinct())

  /** @inheritdoc */
  def drop(colNames: String*): DataFrame = transformation(_.drop(colNames: _*))

  /** @inheritdoc */
  def drop(col: Column, cols: Column*): DataFrame = transformation(_.drop(col, cols: _*))

  //   suffered from this.
  //   scattered across the interface and implementation. `drop` and `select`
  // - Retain the old signatures for binary compatibility;
  ////////////////////////////////////////////////////////////////////////////
  /** @inheritdoc */
  // - Scala method resolution runs into problems when the ambiguous methods are
  // of the interface. This is done for a couple of reasons:
  //   This causes issues when the java code tries to materialize results, or
  // Return type overrides to make sure we return the implementation instead
  //   tries to use functionality that is implementation specfic.
  // - Java compatibility . The java compiler uses the byte code signatures,
  //   and those would point to api.Dataset being returned instead of Dataset.
  ////////////////////////////////////////////////////////////////////////////
  def drop(colName: String): DataFrame = transformation(_.drop(colName))

  /** @inheritdoc */
  def drop(col: Column): DataFrame = transformation(_.drop(col))

  /** @inheritdoc */
  def dropDuplicates: Dataset[T] = transformation(_.dropDuplicates())

  /** @inheritdoc */
  def dropDuplicatesWithinWatermark: Dataset[T] = transformation(_.dropDuplicatesWithinWatermark())

  /** @inheritdoc */
  def except(other: sql.Dataset[T]): Dataset[T] = transformation(_.except(other))

  /** @inheritdoc */
  def exceptAll(other: sql.Dataset[T]): Dataset[T] = transformation(_.exceptAll(other))

  /** @inheritdoc */
  @deprecated("use flatMap() or select() with functions.explode() instead", "2.0.0")
  def explode[A <: Product: TypeTag](input: Column*)(f: Row => IterableOnce[A]): DataFrame =
    transformation(_.explode[A](input: _*)(f))

  /** @inheritdoc */
  def filter(func: T => Boolean): Dataset[T] = transformation(_.filter(func))

  /** @inheritdoc */
  def flatMap[U: Encoder](func: T => IterableOnce[U]): Dataset[U] = transformation(_.flatMap[U](func))

  /** @inheritdoc */
  def hint(name: String, parameters: Any*): Dataset[T] = transformation(_.hint(name, parameters: _*))

  /** @inheritdoc */
  def intersect(other: sql.Dataset[T]): Dataset[T] = transformation(_.intersect(other))

  /** @inheritdoc */
  def intersectAll(other: sql.Dataset[T]): Dataset[T] = transformation(_.intersectAll(other))

  /** @inheritdoc */
  def join(right: sql.Dataset[_]): DataFrame = transformation(_.join(right))

  /** @inheritdoc */
  def lateralJoin(right: sql.Dataset[_]): DataFrame = transformation(_.lateralJoin(right))

  /** @inheritdoc */
  def lateralJoin(right: sql.Dataset[_], joinType: String): DataFrame = transformation(_.lateralJoin(right, joinType))

  /** @inheritdoc */
  def limit(n: Int): Dataset[T] = transformation(_.limit(n))

  /** @inheritdoc */
  def map[U: Encoder](func: T => U): Dataset[U] = transformation(_.map[U](func))

  /** @inheritdoc */
  def mapPartitions[U: Encoder](func: Iterator[T] => Iterator[U]): Dataset[U] = transformation(_.mapPartitions[U](func))

  /** @inheritdoc */
  def offset(n: Int): Dataset[T] = transformation(_.offset(n))

  /** @inheritdoc */
  def repartition(numPartitions: Int): Dataset[T] = transformation(_.repartition(numPartitions))

  /** @inheritdoc */
  def sample(withReplacement: Boolean, fraction: Double, seed: Long): Dataset[T] =
    transformation(_.sample(withReplacement, fraction, seed))

  /** @inheritdoc */
  def sample(fraction: Double, seed: Long): Dataset[T] = transformation(_.sample(fraction, seed))

  /** @inheritdoc */
  def sample(fraction: Double): Dataset[T] = transformation(_.sample(fraction))

  /** @inheritdoc */
  def sample(withReplacement: Boolean, fraction: Double): Dataset[T] =
    transformation(_.sample(withReplacement, fraction))

  /** @inheritdoc */
  def select[U1](c1: TypedColumn[T, U1]): Dataset[U1] = transformation(_.select[U1](c1))

  /** @inheritdoc */
  def select[U1, U2](c1: TypedColumn[T, U1], c2: TypedColumn[T, U2]): Dataset[(U1, U2)] =
    transformation(_.select[U1, U2](c1, c2))

  /** @inheritdoc */
  def select[U1, U2, U3](
      c1: TypedColumn[T, U1],
      c2: TypedColumn[T, U2],
      c3: TypedColumn[T, U3]
  ): Dataset[(U1, U2, U3)] = transformation(_.select[U1, U2, U3](c1, c2, c3))

  /** @inheritdoc */
  def select[U1, U2, U3, U4](
      c1: TypedColumn[T, U1],
      c2: TypedColumn[T, U2],
      c3: TypedColumn[T, U3],
      c4: TypedColumn[T, U4]
  ): Dataset[(U1, U2, U3, U4)] = transformation(_.select[U1, U2, U3, U4](c1, c2, c3, c4))

  /** @inheritdoc */
  def select[U1, U2, U3, U4, U5](
      c1: TypedColumn[T, U1],
      c2: TypedColumn[T, U2],
      c3: TypedColumn[T, U3],
      c4: TypedColumn[T, U4],
      c5: TypedColumn[T, U5]
  ): Dataset[(U1, U2, U3, U4, U5)] = transformation(_.select[U1, U2, U3, U4, U5](c1, c2, c3, c4, c5))

  /** @inheritdoc */
  def summary(statistics: String*): DataFrame = transformation(_.summary(statistics: _*))

  /** @inheritdoc */
  def to(schema: StructType): DataFrame = transformation(_.to(schema))

  /** @inheritdoc */
  // This is declared with parentheses to prevent the Scala compiler from treating
  // `ds.toDF("1")` as invoking this toDF and then apply on the returned DataFrame.
  def toDF: DataFrame = transformation(_.toDF())

  /** @inheritdoc */
  def toJSON: Dataset[String] = transformation(_.toJSON)

  /** @inheritdoc */
  def transpose: DataFrame = transformation(_.transpose())

  /** @inheritdoc */
  def union(other: sql.Dataset[T]): Dataset[T] = transformation(_.union(other))

  /** @inheritdoc */
  def unionAll(other: sql.Dataset[T]): Dataset[T] = transformation(_.unionAll(other))

  /** @inheritdoc */
  def unionByName(other: sql.Dataset[T]): Dataset[T] = transformation(_.unionByName(other))

  /** @inheritdoc */
  def withColumnRenamed(existingName: String, newName: String): DataFrame =
    transformation(_.withColumnRenamed(existingName, newName))

  /** @inheritdoc */
  // We only accept an existing column name, not a derived column here as a watermark that is
  // defined on a derived column cannot referenced elsewhere in the plan.
  def withWatermark(eventTime: String, delayThreshold: String): Dataset[T] =
    transformation(_.withWatermark(eventTime, delayThreshold))

  // ===============

  /** @inheritdoc */
  def agg(aggExpr: (String, String), aggExprs: (String, String)*): TryAnalysis[DataFrame] =
    transformationWithAnalysis(_.agg(aggExpr, aggExprs: _*))

  /** @inheritdoc */
  def agg(exprs: Map[String, String]): TryAnalysis[DataFrame] = transformationWithAnalysis(_.agg(exprs))

  /** @inheritdoc */
  def agg(expr: Column, exprs: Column*): TryAnalysis[DataFrame] = transformationWithAnalysis(_.agg(expr, exprs: _*))

  /** @inheritdoc */
  def as[U: Encoder]: TryAnalysis[Dataset[U]] = transformationWithAnalysis(_.as[U])

  /** @inheritdoc */
  def describe(cols: String*): TryAnalysis[DataFrame] = transformationWithAnalysis(_.describe(cols: _*))

  /** @inheritdoc */
  def dropDuplicates(colNames: Seq[String]): TryAnalysis[Dataset[T]] =
    transformationWithAnalysis(_.dropDuplicates(colNames))

  /** @inheritdoc */
  def dropDuplicates(col1: String, cols: String*): TryAnalysis[Dataset[T]] =
    transformationWithAnalysis(_.dropDuplicates(col1, cols: _*))

  /** @inheritdoc */
  def dropDuplicatesWithinWatermark(colNames: Seq[String]): TryAnalysis[Dataset[T]] =
    transformationWithAnalysis(_.dropDuplicatesWithinWatermark(colNames))

  /** @inheritdoc */
  def dropDuplicatesWithinWatermark(col1: String, cols: String*): TryAnalysis[Dataset[T]] =
    transformationWithAnalysis(_.dropDuplicatesWithinWatermark(col1, cols: _*))

  /** @inheritdoc */
  @deprecated("use flatMap() or select() with functions.explode() instead", "2.0.0")
  def explode[A, B: TypeTag](inputColumn: String, outputColumn: String)(
      f: A => IterableOnce[B]
  ): TryAnalysis[DataFrame] = transformationWithAnalysis(_.explode[A, B](inputColumn, outputColumn)(f))

  /** @inheritdoc */
  def filter(condition: Column): TryAnalysis[Dataset[T]] = transformationWithAnalysis(_.filter(condition))

  /** @inheritdoc */
  def filter(conditionExpr: String): TryAnalysis[Dataset[T]] = transformationWithAnalysis(_.filter(conditionExpr))

  /** @inheritdoc */
  def join(right: sql.Dataset[_], usingColumns: Seq[String], joinType: String): TryAnalysis[DataFrame] =
    transformationWithAnalysis(_.join(right, usingColumns, joinType))

  /** @inheritdoc */
  def join(right: sql.Dataset[_], joinExprs: Column, joinType: String): TryAnalysis[DataFrame] =
    transformationWithAnalysis(_.join(right, joinExprs, joinType))

  /** @inheritdoc */
  def join(right: sql.Dataset[_], usingColumn: String): TryAnalysis[DataFrame] =
    transformationWithAnalysis(_.join(right, usingColumn))

  /** @inheritdoc */
  def join(right: sql.Dataset[_], usingColumns: Seq[String]): TryAnalysis[DataFrame] =
    transformationWithAnalysis(_.join(right, usingColumns))

  /** @inheritdoc */
  def join(right: sql.Dataset[_], usingColumn: String, joinType: String): TryAnalysis[DataFrame] =
    transformationWithAnalysis(_.join(right, usingColumn, joinType))

  /** @inheritdoc */
  def join(right: sql.Dataset[_], joinExprs: Column): TryAnalysis[DataFrame] =
    transformationWithAnalysis(_.join(right, joinExprs))

  /** @inheritdoc */
  def joinWith[U](other: sql.Dataset[U], condition: Column, joinType: String): TryAnalysis[Dataset[(T, U)]] =
    transformationWithAnalysis(_.joinWith[U](other, condition, joinType))

  /** @inheritdoc */
  def joinWith[U](other: sql.Dataset[U], condition: Column): TryAnalysis[Dataset[(T, U)]] =
    transformationWithAnalysis(_.joinWith[U](other, condition))

  /** @inheritdoc */
  def lateralJoin(right: sql.Dataset[_], joinExprs: Column): TryAnalysis[DataFrame] =
    transformationWithAnalysis(_.lateralJoin(right, joinExprs))

  /** @inheritdoc */
  def lateralJoin(right: sql.Dataset[_], joinExprs: Column, joinType: String): TryAnalysis[DataFrame] =
    transformationWithAnalysis(_.lateralJoin(right, joinExprs, joinType))

  /** @inheritdoc */
  def observe(name: String, expr: Column, exprs: Column*): TryAnalysis[Dataset[T]] =
    transformationWithAnalysis(_.observe(name, expr, exprs: _*))

  /** @inheritdoc */
  def observe(observation: Observation, expr: Column, exprs: Column*): TryAnalysis[Dataset[T]] =
    transformationWithAnalysis(_.observe(observation, expr, exprs: _*))

  /** @inheritdoc */
  def orderBy(sortCol: String, sortCols: String*): TryAnalysis[Dataset[T]] =
    transformationWithAnalysis(_.orderBy(sortCol, sortCols: _*))

  /** @inheritdoc */
  def orderBy(sortExprs: Column*): TryAnalysis[Dataset[T]] = transformationWithAnalysis(_.orderBy(sortExprs: _*))

  /** @inheritdoc */
  def repartition(numPartitions: Int, partitionExprs: Column*): TryAnalysis[Dataset[T]] =
    transformationWithAnalysis(_.repartition(numPartitions, partitionExprs: _*))

  /** @inheritdoc */
  def repartition(partitionExprs: Column*): TryAnalysis[Dataset[T]] =
    transformationWithAnalysis(_.repartition(partitionExprs: _*))

  /** @inheritdoc */
  def repartitionByRange(numPartitions: Int, partitionExprs: Column*): TryAnalysis[Dataset[T]] =
    transformationWithAnalysis(_.repartitionByRange(numPartitions, partitionExprs: _*))

  /** @inheritdoc */
  def repartitionByRange(partitionExprs: Column*): TryAnalysis[Dataset[T]] =
    transformationWithAnalysis(_.repartitionByRange(partitionExprs: _*))

  /** @inheritdoc */
  def select(cols: Column*): TryAnalysis[DataFrame] = transformationWithAnalysis(_.select(cols: _*))

  /** @inheritdoc */
  def select(col: String, cols: String*): TryAnalysis[DataFrame] = transformationWithAnalysis(_.select(col, cols: _*))

  /** @inheritdoc */
  def selectExpr(exprs: String*): TryAnalysis[DataFrame] = transformationWithAnalysis(_.selectExpr(exprs: _*))

  /** @inheritdoc */
  def sort(sortCol: String, sortCols: String*): TryAnalysis[Dataset[T]] =
    transformationWithAnalysis(_.sort(sortCol, sortCols: _*))

  /** @inheritdoc */
  def sort(sortExprs: Column*): TryAnalysis[Dataset[T]] = transformationWithAnalysis(_.sort(sortExprs: _*))

  /** @inheritdoc */
  def sortWithinPartitions(sortCol: String, sortCols: String*): TryAnalysis[Dataset[T]] =
    transformationWithAnalysis(_.sortWithinPartitions(sortCol, sortCols: _*))

  /** @inheritdoc */
  def sortWithinPartitions(sortExprs: Column*): TryAnalysis[Dataset[T]] =
    transformationWithAnalysis(_.sortWithinPartitions(sortExprs: _*))

  /** @inheritdoc */
  def toDF(colNames: String*): TryAnalysis[DataFrame] = transformationWithAnalysis(_.toDF(colNames: _*))

  /** @inheritdoc */
  def transpose(indexColumn: Column): TryAnalysis[DataFrame] = transformationWithAnalysis(_.transpose(indexColumn))

  /** @inheritdoc */
  def unionByName(other: sql.Dataset[T], allowMissingColumns: Boolean): TryAnalysis[Dataset[T]] =
    transformationWithAnalysis(_.unionByName(other, allowMissingColumns))

  /** @inheritdoc */
  def where(condition: Column): TryAnalysis[Dataset[T]] = transformationWithAnalysis(_.where(condition))

  /** @inheritdoc */
  def where(conditionExpr: String): TryAnalysis[Dataset[T]] = transformationWithAnalysis(_.where(conditionExpr))

  /** @inheritdoc */
  def withColumn(colName: String, col: Column): TryAnalysis[DataFrame] =
    transformationWithAnalysis(_.withColumn(colName, col))

  /** @inheritdoc */
  def withColumns(colsMap: Map[String, Column]): TryAnalysis[DataFrame] =
    transformationWithAnalysis(_.withColumns(colsMap))

  /** @inheritdoc */
  def withColumns(colsMap: util.Map[String, Column]): TryAnalysis[DataFrame] =
    transformationWithAnalysis(_.withColumns(colsMap))

  /** @inheritdoc */
  def withColumnsRenamed(colsMap: Map[String, String]): TryAnalysis[DataFrame] =
    transformationWithAnalysis(_.withColumnsRenamed(colsMap))

  /** @inheritdoc */
  def withColumnsRenamed(colsMap: util.Map[String, String]): TryAnalysis[DataFrame] =
    transformationWithAnalysis(_.withColumnsRenamed(colsMap))

  /** @inheritdoc */
  def withMetadata(columnName: String, metadata: Metadata): TryAnalysis[DataFrame] =
    transformationWithAnalysis(_.withMetadata(columnName, metadata))

  // ===============

  // Methods that need to be implemented
  //
  // [[org.apache.spark.sql.Dataset.writeTo]]

  // ===============

  // Methods with handmade implementations
  //
  // [[org.apache.spark.sql.Dataset.explain]]
  // [[org.apache.spark.sql.Dataset.groupBy]]
  // [[org.apache.spark.sql.Dataset.show]]
  // [[org.apache.spark.sql.Dataset.write]]
  // [[org.apache.spark.sql.Dataset.writeStream]]

  // ===============

  // Ignored methods
  //
  // [[org.apache.spark.sql.Dataset.asTable]]
  // [[org.apache.spark.sql.Dataset.collectAsList]]
  // [[org.apache.spark.sql.Dataset.filter]]
  // [[org.apache.spark.sql.Dataset.flatMap]]
  // [[org.apache.spark.sql.Dataset.foreachPartition]]
  // [[org.apache.spark.sql.Dataset.groupByKey]]
  // [[org.apache.spark.sql.Dataset.map]]
  // [[org.apache.spark.sql.Dataset.mapPartitions]]
  // [[org.apache.spark.sql.Dataset.toJavaRDD]]
  // [[org.apache.spark.sql.Dataset.toString]]

  // ===============

  /** @inheritdoc */
  def exists: Column = get(_.exists())

  /** @inheritdoc */
  def groupingSets(groupingSets: Seq[Seq[Column]], cols: Column*): RelationalGroupedDataset =
    get(_.groupingSets(groupingSets, cols: _*))

  /** @inheritdoc */
  def mergeInto(table: String, condition: Column): MergeIntoWriter[T] = get(_.mergeInto(table, condition))

  /** @inheritdoc */
  def metadataColumn(colName: String): Column = get(_.metadataColumn(colName))

  /** @inheritdoc */
  def scalar: Column = get(_.scalar())
}
