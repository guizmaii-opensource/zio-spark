name         := "spark-code-migration"
scalaVersion := "3.3.7"

val sparkVersion = "3.5.8"

libraryDependencies ++= Seq(
  // "io.univalence"    %% "zio-spark"  % "X.X.X", //https://index.scala-lang.org/univalence/zio-spark/zio-spark
  ("org.apache.spark" %% "spark-core" % sparkVersion).cross(CrossVersion.for3Use2_13),
  ("org.apache.spark" %% "spark-sql"  % sparkVersion).cross(CrossVersion.for3Use2_13),
)
