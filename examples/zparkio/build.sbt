name         := "zparkio-comparaison"
scalaVersion := "3.3.7"

val sparkVersion = "3.5.8"

libraryDependencies ++= Seq(
  // "io.univalence"    %% "zio-spark"  % "0.10.0", //https://index.scala-lang.org/univalence/zio-spark/zio-spark
  ("org.apache.spark" %% "spark-core" % sparkVersion % Provided).cross(CrossVersion.for3Use2_13),
  ("org.apache.spark" %% "spark-sql"  % sparkVersion % Provided).cross(CrossVersion.for3Use2_13),
)

libraryDependencies += "dev.zio" %% "zio-logging" % "2.5.1"
