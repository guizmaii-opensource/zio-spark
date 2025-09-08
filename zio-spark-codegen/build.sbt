ThisBuild / organization := "com.guizmaii"

Global / conflictWarning := ConflictWarning.disable

// Aliases
addCommandAlias("fmt", "scalafmt")
addCommandAlias("fmtCheck", "scalafmtCheckAll")
addCommandAlias("check", "; fmtCheck;")

val sparkVersion = "4.0.1"
val zioVersion   = "2.0.21"

lazy val plugin =
  (project in file("."))
    .enablePlugins(SbtPlugin)
    .settings(
      name := "zio-spark-codegen",
      libraryDependencies ++= Seq(
        "dev.zio" %% "zio"          % zioVersion,
        "dev.zio" %% "zio-test"     % zioVersion % Test,
        "dev.zio" %% "zio-test-sbt" % zioVersion % Test,
        // equals to sbt-scalafmt's scalfmt-dynamic version
        ("org.scalameta" %% "scalafmt-dynamic" % "3.9.9").cross(CrossVersion.for3Use2_13),
        "org.scalameta"  %% "scalameta"        % "4.13.9",
        // For tests only
        ("org.apache.spark" %% "spark-core" % sparkVersion).withSources().cross(CrossVersion.for3Use2_13)
          .exclude("org.scala-lang.modules", "scala-xml_2.13")
          .exclude("org.scala-lang.modules", "scala-parser-combinators_2.13")
          .exclude("org.scala-lang.modules", "scala-parallel-collections_2.13"),
        // For tests only
        ("org.apache.spark" %% "spark-sql" % sparkVersion)
          .withSources()
          .cross(CrossVersion.for3Use2_13)
          .exclude("org.scala-lang.modules", "scala-xml_2.13")
          .exclude("org.scala-lang.modules", "scala-parser-combinators_2.13")
          .exclude("org.scala-lang.modules", "scala-parallel-collections_2.13"),
        // Explicitly use Scala 3 versions to avoid conflicts
        "org.scala-lang.modules" %% "scala-xml" % "2.3.0",
        "org.scala-lang.modules" %% "scala-parser-combinators" % "2.4.0",
        "org.scala-lang.modules" %% "scala-parallel-collections" % "1.1.0"
      ),
      testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework"))
    )
