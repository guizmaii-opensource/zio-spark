package org.apache.spark.sql

/**
 * The Sniffer singleton provide a backdoor to access private spark
 * function.
 */
object Sniffer {

  /** Backdoor for showString private function. */
  def datasetShowString[T](dataset: Dataset[T], _numRows: Int, truncate: Int): String = {
    try {
      // Try the new Spark 4 signature first
      val method = dataset.getClass.getMethod("showString", classOf[Int], classOf[Int], classOf[Boolean])
      method.invoke(dataset, _numRows.asInstanceOf[AnyRef], truncate.asInstanceOf[AnyRef], false.asInstanceOf[AnyRef]).asInstanceOf[String]
    } catch {
      case _: NoSuchMethodException =>
        try {
          // Fallback to older signature
          val method = dataset.getClass.getMethod("showString", classOf[Int], classOf[Int])
          method.invoke(dataset, _numRows.asInstanceOf[AnyRef], truncate.asInstanceOf[AnyRef]).asInstanceOf[String]
        } catch {
          case _: Exception =>
            // Ultimate fallback - redirect show() output to capture it
            val baos = new java.io.ByteArrayOutputStream()
            val ps = new java.io.PrintStream(baos)
            val originalOut = System.out
            try {
              System.setOut(ps)
              dataset.show(_numRows, truncate)
              ps.flush()
              baos.toString("UTF-8")
            } finally {
              System.setOut(originalOut)
              ps.close()
            }
        }
    }
  }
}
