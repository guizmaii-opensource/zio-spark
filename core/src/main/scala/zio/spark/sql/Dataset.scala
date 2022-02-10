package zio.spark.sql

import org.apache.spark.sql.{Dataset => UnderlyingDataset, Encoder, Row}

import zio.Task
import zio.spark.impure.Impure.ImpureBox
import zio.spark.rdd.RDD

final case class Dataset[T](underlyingDataset: ImpureBox[UnderlyingDataset[T]])
    extends ExtraDatasetFeature[T](underlyingDataset) {
  import underlyingDataset._

  /**
   * Maps each record to the specified type.
   *
   * See [[UnderlyingDataset.as]] for more information.
   */
  def as[U: Encoder]: TryAnalysis[Dataset[U]] = transformationWithAnalysis(_.as[U])

  /** Applies a transformation to the underlying dataset. */
  def transformation[U](f: UnderlyingDataset[T] => UnderlyingDataset[U]): Dataset[U] =
    succeedNow(f.andThen(x => Dataset(x)))

  /**
   * Applies a transformation to the underlying dataset, it is used for
   * transformations that can fail due to an AnalysisException.
   */
  def transformationWithAnalysis[U](f: UnderlyingDataset[T] => UnderlyingDataset[U]): TryAnalysis[Dataset[U]] =
    TryAnalysis(transformation(f))

  /** Applies an action to the underlying dataset. */
  def action[A](f: UnderlyingDataset[T] => A): Task[A] = attemptBlocking(f)

  /**
   * A variant of select that accepts SQL expressions.
   *
   * See [[UnderlyingDataset.selectExpr]] for more information.
   */
  def selectExpr(exprs: String*): TryAnalysis[Dataset[Row]] = transformationWithAnalysis(_.selectExpr(exprs: _*))

  /**
   * Limits the number of rows of a dataset.
   *
   * See [[UnderlyingDataset.limit]] for more information.
   */
  def limit(n: Int): Dataset[T] = transformation(_.limit(n))

  /**
   * Returns a new Dataset that only contains elements respecting the
   * predicate.
   *
   * See [[UnderlyingDataset.filter]] for more information.
   */
  def filter(f: T => Boolean): Dataset[T] = transformation(_.filter(f))

  def filter(expr: String): TryAnalysis[Dataset[T]] = transformationWithAnalysis(_.filter(expr))

  /**
   * Applies the function f to each record of the dataset.
   *
   * See [[UnderlyingDataset.map]] for more information.
   */
  def map[U: Encoder](f: T => U): Dataset[U] = transformation(_.map(f))

  /**
   * Applies the function f to each record of the dataset and then
   * flattening the result.
   *
   * See [[UnderlyingDataset.flatMap]] for more information.
   */
  def flatMap[U: Encoder](f: T => Iterable[U]): Dataset[U] = transformation(_.flatMap(f))

  /**
   * Counts the number of rows of a dataset.
   *
   * See [[UnderlyingDataset.count]] for more information.
   */
  def count: Task[Long] = action(_.count())

  /**
   * Retrieves the rows of a dataset as a list of elements.
   *
   * See [[UnderlyingDataset.collect]] for more information.
   */
  def collect: Task[Seq[T]] = action(_.collect().toSeq)

  /** Alias for [[head]]. */
  def first: Task[T] = head

  /**
   * Takes the first element of a dataset.
   *
   * See [[UnderlyingDataset.head]] for more information.
   */
  def head: Task[T] = head(1).map(_.head)

  /** Alias for [[head]]. */
  def take(n: Int): Task[List[T]] = head(n)

  /** Alias for [[headOption]]. */
  def firstOption: Task[Option[T]] = headOption

  /** Takes the first element of a dataset or None. */
  def headOption: Task[Option[T]] = head(1).map(_.headOption)

  /**
   * Takes the n elements of a dataset.
   *
   * See [[UnderlyingDataset.head]] for more information.
   */
  def head(n: Int): Task[List[T]] = action(_.head(n).toList)

  /**
   * Transform the dataset into a [[RDD]].
   *
   * See [[UnderlyingDataset.rdd]] for more information.
   */
  def rdd: RDD[T] = RDD(succeedNow(_.rdd))

  /**
   * Chains custom transformations.
   *
   * See [[UnderlyingDataset.transform]] for more information.
   */
  def transform[U](t: Dataset[T] => Dataset[U]): Dataset[U] = t(this)

  /**
   * Returns a new Dataset that contains only the unique rows from this
   * Dataset, considering only the subset of columns.
   *
   * See [[UnderlyingDataset.dropDuplicates]] for more information.
   */
  def dropDuplicates(colNames: Seq[String]): Dataset[T] = transformation(_.dropDuplicates(colNames))

  /**
   * Returns a new Dataset that contains only the unique rows from this
   * Dataset, considering all columns.
   *
   * See [[UnderlyingDataset.dropDuplicates]] for more information.
   */
  def dropDuplicates: Dataset[T] = transformation(_.dropDuplicates())

  /** Alias for [[dropDuplicates]]. */
  def distinct: Dataset[T] = dropDuplicates

  /**
   * Creates a local temporary view using the given name.
   *
   * See [[UnderlyingDataset.createOrReplaceTempView]] for more
   * information.
   */
  def createOrReplaceTempView(viewName: String): Task[Unit] = attemptBlocking(_.createOrReplaceTempView(viewName))
}
