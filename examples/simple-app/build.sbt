name         := "simple-app"
scalaVersion := "2.13.17"

val sparkVersion = "4.1.1"

libraryDependencies ++= Seq(
  // "io.univalence"    %% "zio-spark"  % "X.X.X", //https://index.scala-lang.org/univalence/zio-spark/zio-spark
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-sql"  % sparkVersion
)
