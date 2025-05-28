name         := "zparkio-comparaison"
scalaVersion := "2.13.16"

libraryDependencies ++= Seq(
  // "io.univalence"    %% "zio-spark"  % "0.10.0", //https://index.scala-lang.org/univalence/zio-spark/zio-spark
  "org.apache.spark" %% "spark-core" % "3.5.6" % Provided,
  "org.apache.spark" %% "spark-sql"  % "3.5.5" % Provided
)

libraryDependencies += "dev.zio" %% "zio-logging" % "2.5.0"
