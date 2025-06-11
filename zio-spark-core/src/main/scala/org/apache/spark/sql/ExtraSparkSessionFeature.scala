package org.apache.spark.sql

import org.apache.spark.sql.{SparkSession => UnderlyingSparkSession}

import zio.{Task, Trace, ZIO}

abstract class ExtraSparkSessionFeature(underlyingSparkSession: UnderlyingSparkSession) {
  def withActive[T](block: => T)(implicit trace: Trace): Task[T] = ZIO.attempt(underlyingSparkSession.withActive(block))

}
