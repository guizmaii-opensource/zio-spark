ThisBuild / organization := "com.guizmaii"

// Spark still uses 1.X.X version of scala-xml
ThisBuild / libraryDependencySchemes += "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always

// Aliases
addCommandAlias("fmt", "scalafmt")
addCommandAlias("fmtCheck", "scalafmtCheckAll")
addCommandAlias("check", "; fmtCheck;")

val sparkVersion = "3.5.7"
val zioVersion   = "2.0.4"

lazy val plugin =
  (project in file("."))
    .enablePlugins(SbtPlugin)
    .settings(
      name := "zio-spark-codegen",
      libraryDependencies ++= Seq(
        "dev.zio"          %% "zio"              % zioVersion,
        "dev.zio"          %% "zio-test"         % zioVersion % Test,
        "dev.zio"          %% "zio-test-sbt"     % zioVersion % Test,
        "org.scalameta"    %% "scalafmt-dynamic" % "3.4.3", // equals to sbt-scalafmt's scalfmt-dynamic version
        "org.scalameta"    %% "scalameta"        % "4.9.9",
        "org.apache.spark" %% "spark-core"       % sparkVersion withSources (), // For tests only
        "org.apache.spark" %% "spark-sql"        % sparkVersion withSources () // For tests only
      ),
      testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework"))
    )
