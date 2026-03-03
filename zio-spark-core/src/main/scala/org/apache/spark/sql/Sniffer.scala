package org.apache.spark.sql

import org.apache.spark.sql.classic.{Dataset => ClassicDataset}

/**
 * The Sniffer singleton provide a backdoor to access private spark
 * function.
 */
object Sniffer {

  /** Backdoor for showString private function. */
  def datasetShowString[T](dataset: Dataset[T], _numRows: Int, truncate: Int): String =
    dataset.asInstanceOf[ClassicDataset[T]].showString(_numRows, truncate)
}
