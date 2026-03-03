ThisBuild / organization := "com.guizmaii"

// Spark still uses 1.X.X version of scala-xml
ThisBuild / libraryDependencySchemes += "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always
ThisBuild / libraryDependencySchemes += "com.github.luben"        % "zstd-jni"  % VersionScheme.Always

// Aliases
addCommandAlias("fmt", "scalafmt")
addCommandAlias("fmtCheck", "scalafmtCheckAll")
addCommandAlias("check", "; fmtCheck;")

val sparkVersion = "4.1.1"
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
        // Spark 4 dropped Scala 2.12; use 2.13 artifacts explicitly (only needed for source JAR reading).
        // Intransitive to avoid cross-version conflicts with Scala 2.12 plugin dependencies.
        ("org.apache.spark" % "spark-core_2.13"    % sparkVersion withSources ()).intransitive(),
        ("org.apache.spark" % "spark-sql_2.13"     % sparkVersion withSources ()).intransitive(),
        ("org.apache.spark" % "spark-sql-api_2.13" % sparkVersion withSources ()).intransitive()
      ),
      testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework"))
    )
