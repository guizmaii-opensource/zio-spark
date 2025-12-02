name         := "zparkio-comparaison"
scalaVersion := "2.13.17"

val sparkVersion = "3.5.7"

libraryDependencies ++= Seq(
  // "io.univalence"    %% "zio-spark"  % "0.10.0", //https://index.scala-lang.org/univalence/zio-spark/zio-spark
  "org.apache.spark" %% "spark-core" % sparkVersion % Provided,
  "org.apache.spark" %% "spark-sql"  % sparkVersion % Provided
)

libraryDependencies += "dev.zio" %% "zio-logging" % "2.5.2"
