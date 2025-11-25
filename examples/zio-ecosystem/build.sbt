name         := "zio-ecosystem"
scalaVersion := "2.13.18"

val sparkVersion = "3.5.7"

libraryDependencies ++= Seq(
  // "io.univalence"    %% "zio-spark"  % "X.X.X", //https://index.scala-lang.org/univalence/zio-spark/zio-spark
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-sql"  % sparkVersion,
  "dev.zio"          %% "zio-cli"    % "0.7.3"
)
