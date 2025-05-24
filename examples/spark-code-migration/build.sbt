name         := "spark-code-migration"
scalaVersion := "2.13.16"

libraryDependencies ++= Seq(
  // "io.univalence"    %% "zio-spark"  % "X.X.X", //https://index.scala-lang.org/univalence/zio-spark/zio-spark
  "org.apache.spark" %% "spark-core" % "4.0.0",
  "org.apache.spark" %% "spark-sql"  % "4.0.0"
)
