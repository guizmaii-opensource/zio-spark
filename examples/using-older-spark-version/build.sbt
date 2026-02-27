name         := "using-older-spark-version"
scalaVersion := "3.3.7"

libraryDependencies ++= Seq(
  // "io.univalence"    %% "zio-spark"  % "X.X.X", //https://index.scala-lang.org/univalence/zio-spark/zio-spark
  ("org.apache.spark" %% "spark-core" % "3.2.4").cross(CrossVersion.for3Use2_13),
  ("org.apache.spark" %% "spark-sql"  % "3.2.4").cross(CrossVersion.for3Use2_13),
)
