// Common configuration
inThisBuild(
  List(
    scalaVersion  := scala213,
    organization  := "com.guizmaii",
    homepage      := Some(url("https://github.com/univalence/zio-spark")),
    licenses      := List("Apache-2.0" -> url("https://github.com/univalence/zio-spark/blob/master/LICENSE")),
    versionScheme := Some("early-semver"),
    developers := List(
      Developer(
        id    = "jwinandy",
        name  = "Jonathan Winandy",
        email = "jonathan@univalence.io",
        url   = url("https://github.com/ahoy-jon")
      ),
      Developer(
        id    = "phong",
        name  = "Philippe Hong",
        email = "philippe@univalence.io",
        url   = url("https://github.com/hwki77")
      ),
      Developer(
        id    = "fsarradin",
        name  = "François Sarradin",
        email = "francois@univalence.io",
        url   = url("https://github.com/fsarradin")
      ),
      Developer(
        id    = "bernit77",
        name  = "Bernarith Men",
        email = "bernarith@univalence.io",
        url   = url("https://github.com/bernit77")
      ),
      Developer(
        id    = "HarrisonCheng",
        name  = "Harrison Cheng",
        email = "harrison@univalence.io",
        url   = url("https://github.com/HarrisonCheng")
      ),
      Developer(
        id    = "dylandoamaral",
        name  = "Dylan Do Amaral",
        email = "dylan@univalence.io",
        url   = url("https://github.com/dylandoamaral")
      )
    ),
    excludeDependencies := Seq(
      "org.scala-lang.modules" %% "scala-collection-compat"
    )
  )
)

Global / onChangedBuildSource := ReloadOnSourceChanges

// Scalafix configuration
ThisBuild / semanticdbEnabled := true
ThisBuild / semanticdbVersion := scalafixSemanticdb.revision
ThisBuild / scalafixDependencies ++= Seq("com.github.vovapolu" %% "scaluzzi" % "0.1.23")

// Java 17+ stuff
ThisBuild / Test / javaOptions ++= Seq("--add-exports", "java.base/sun.nio.ch=ALL-UNNAMED")
ThisBuild / Test / javaOptions ++= Seq("--add-opens", "java.base/java.nio=ALL-UNNAMED")
ThisBuild / Test / javaOptions ++= Seq("--add-opens", "java.base/java.util=ALL-UNNAMED")
ThisBuild / Test / javaOptions ++= Seq("--add-opens", "java.base/java.lang=ALL-UNNAMED")
ThisBuild / Test / javaOptions ++= Seq("--add-opens", "java.base/java.lang.invoke=ALL-UNNAMED")
ThisBuild / Test / fork := true // Needed otherwise the javaOptions are not taken into account

// SCoverage configuration
val excludedPackages: Seq[String] =
  Seq(
    "zio\\.spark\\.SparkContext.*",                 // Generated source
    "zio\\.spark\\.rdd\\.RDD.*",                    // Generated source
    "zio\\.spark\\.sql\\.Dataset.*",                // Generated source
    "zio\\.spark\\.sql\\.DataFrameNaFunctions.*",   // Generated source
    "zio\\.spark\\.sql\\.DataFrameStatFunctions.*", // Generated source
    "zio\\.spark\\.sql\\.KeyValueGroupedDataset.*", // Generated source
    "zio\\.spark\\.sql\\.implicits.*",              // Spark implicits
    "zio\\.spark\\.sql\\.LowPrioritySQLImplicits.*" // Spark implicits
  )

ThisBuild / coverageFailOnMinimum           := false
ThisBuild / coverageMinimumStmtTotal        := 80
ThisBuild / coverageMinimumBranchTotal      := 80
ThisBuild / coverageMinimumStmtPerPackage   := 50
ThisBuild / coverageMinimumBranchPerPackage := 50
ThisBuild / coverageMinimumStmtPerFile      := 0
ThisBuild / coverageMinimumBranchPerFile    := 0
ThisBuild / coverageExcludedPackages        := excludedPackages.mkString(";")

// Aliases
addCommandAlias("fmt", "scalafmt")
addCommandAlias("fmtCheck", "scalafmtCheckAll")
addCommandAlias("lint", "scalafix")
addCommandAlias("lintCheck", "scalafixAll --check")
addCommandAlias("check", "; fmtCheck; lintCheck;")
addCommandAlias("fixStyle", "; scalafmtAll; scalafixAll;")
addCommandAlias("prepare", "fixStyle")
addCommandAlias("testAll", "; clean;+ test;")
addCommandAlias("testSpecific", "; clean; test;")
addCommandAlias("testSpecificWithCoverage", "; clean; coverage; test; coverageReport;")

// -- Lib versions
lazy val zio        = "2.1.20"
lazy val zioPrelude = "1.0.0-RC41"

lazy val scala213 = "2.13.16"
lazy val scala3   = "3.3.6"

lazy val supportedScalaVersions = List(scala213, scala3)

lazy val scalaMajorVersion: SettingKey[Long] = SettingKey("scala major version")
lazy val scalaMinorVersion: SettingKey[Long] = SettingKey("scala minor version")

lazy val core =
  (project in file("zio-spark-core"))
    .configs(IntegrationTest)
    .settings(crossScalaVersionSettings)
    .settings(commonSettings)
    .settings(
      name              := "zio-spark",
      scalaMajorVersion := CrossVersion.partialVersion(scalaVersion.value).get._1,
      scalaMinorVersion := CrossVersion.partialVersion(scalaVersion.value).get._2,
      libraryDependencies ++= Seq(
        "dev.zio" %% "zio"         % zio,
        "dev.zio" %% "zio-streams" % zio,
        "dev.zio" %% "zio-prelude" % zioPrelude
      ) ++ generateSparkLibraryDependencies(scalaMajorVersion.value, scalaMinorVersion.value)
        ++ generateMagnoliaDependency(scalaMajorVersion.value, scalaMinorVersion.value),
      Defaults.itSettings
    )
    .enablePlugins(ZioSparkCodegenPlugin)

lazy val coreTests =
  (project in file("zio-spark-core-tests"))
    .settings(crossScalaVersionSettings)
    .settings(commonSettings)
    .settings(noPublishingSettings)
    .settings(
      name              := "zio-spark-tests",
      scalaMajorVersion := CrossVersion.partialVersion(scalaVersion.value).get._1,
      scalaMinorVersion := CrossVersion.partialVersion(scalaVersion.value).get._2,
      libraryDependencies ++= Seq(
        "dev.zio" %% "zio"          % zio,
        "dev.zio" %% "zio-test"     % zio % Test,
        "dev.zio" %% "zio-test-sbt" % zio % Test
      ) ++ generateSparkLibraryDependencies(scalaMajorVersion.value, scalaMinorVersion.value)
    )
    .dependsOn(core, test)

lazy val test =
  (project in file("zio-spark-test"))
    .settings(crossScalaVersionSettings)
    .settings(commonSettings)
    .settings(
      name              := "zio-spark-test",
      scalaMajorVersion := CrossVersion.partialVersion(scalaVersion.value).get._1,
      scalaMinorVersion := CrossVersion.partialVersion(scalaVersion.value).get._2,
      libraryDependencies ++= Seq(
        "dev.zio" %% "zio"          % zio,
        "dev.zio" %% "zio-test"     % zio,
        "dev.zio" %% "zio-test-sbt" % zio % Test
      ) ++ generateSparkLibraryDependencies(scalaMajorVersion.value, scalaMinorVersion.value)
    )
    .dependsOn(core)

def example(project: Project): Project =
  project
    .dependsOn(core)
    .settings(noPublishingSettings)

lazy val exampleSimpleApp              = (project in file("examples/simple-app")).configure(example)
lazy val exampleSparkCodeMigration     = (project in file("examples/spark-code-migration")).configure(example)
lazy val exampleUsingOlderSparkVersion = (project in file("examples/using-older-spark-version")).configure(example)
lazy val exampleWordCount              = (project in file("examples/word-count")).configure(example)
lazy val exampleZparkio                = (project in file("examples/zparkio")).configure(example)
lazy val exampleZIOEcosystem =
  (project in file("examples/zio-ecosystem"))
    .configure(example)
    .dependsOn(
      exampleSimpleApp,
      exampleSparkCodeMigration,
      exampleWordCount
    )

lazy val examples =
  (project in file("examples"))
    .settings(noPublishingSettings)
    .settings(crossScalaVersions := Nil)
    .aggregate(
      exampleSimpleApp,
      exampleSparkCodeMigration,
      exampleUsingOlderSparkVersion,
      exampleWordCount,
      exampleZparkio,
      exampleZIOEcosystem
    )

/** Generates required libraries for magnolia. */
def generateMagnoliaDependency(scalaMajor: Long, scalaMinor: Long): Seq[ModuleID] =
  scalaMinor match {
    case _ if scalaMajor == 3 => Seq("com.softwaremill.magnolia1_3" %% "magnolia" % "1.3.18")
    case 12 | 13              => Seq("com.softwaremill.magnolia1_2" %% "magnolia" % "1.1.10")
    case _                    => throw new Exception("It should be unreachable.")
  }

/** Generates required libraries for spark. */
def generateSparkLibraryDependencies(scalaMajor: Long, scalaMinor: Long): Seq[ModuleID] = {
  val mappingVersion       = if (scalaMajor == 2) scalaMinor else 13
  val sparkVersion: String = sparkScalaVersionMapping(mappingVersion)
  val sparkCore            = "org.apache.spark" %% "spark-core" % sparkVersion % Provided withSources ()
  val sparkSql             = "org.apache.spark" %% "spark-sql"  % sparkVersion % Provided withSources ()

  scalaMajor match {
    case 2 => Seq(sparkCore, sparkSql)
    case 3 =>
      Seq(
        sparkCore.cross(CrossVersion.for3Use2_13),
        sparkSql.cross(CrossVersion.for3Use2_13),
        "io.github.vincenzobaz" %% "spark-scala3-encoders" % "0.3.2"
      )
    case _ => throw new Exception("It should be unreachable.")
  }
}

/**
 * Returns the correct spark version depending of the current scala
 * minor.
 */
def sparkScalaVersionMapping(scalaMinor: Long): String =
  scalaMinor match {
    case 12 => "3.5.4"
    case 13 => "3.5.4"
    case _  => throw new Exception("It should be unreachable.")
  }

/**
 * Don't fail the compilation for warnings by default, you can still
 * activate it using system properties (It should always be activated in
 * the CI).
 */
def fatalWarningsAsProperties(options: Seq[String]): Seq[String] =
  if (sys.props.getOrElse("fatal-warnings", "false") == "true") options
  else options.filterNot(Set("-Xfatal-warnings"))

def scalaVersionSpecificSources(environment: String, baseDirectory: File)(versions: String*) =
  for {
    version <- "scala" :: versions.toList.map("scala-" + _)
    result = baseDirectory / "src" / environment / version
    if result.exists
  } yield result

def crossScalaVersionSources(scalaVersion: String, environment: String, baseDir: File) = {
  val versions =
    CrossVersion.partialVersion(scalaVersion) match {
      case Some((2, 13)) => List("2")
      case Some((3, _))  => List("3")
      case _             => List.empty
    }
  scalaVersionSpecificSources(environment, baseDir)(versions: _*)
}

lazy val crossScalaVersionSettings =
  Seq(
    Compile / unmanagedSourceDirectories ++=
      crossScalaVersionSources(
        scalaVersion.value,
        "main",
        baseDirectory.value
      ),
    Test / unmanagedSourceDirectories ++=
      crossScalaVersionSources(
        scalaVersion.value,
        "test",
        baseDirectory.value
      )
  )

lazy val commonSettings =
  Seq(
    resolvers += Resolver.sonatypeCentralSnapshots,
    crossScalaVersions := supportedScalaVersions,
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework"),
    scalacOptions ~= fatalWarningsAsProperties
  )

lazy val noPublishingSettings =
  Seq(
    publish / skip                         := true,
    Compile / doc / sources                := Seq.empty,
    Compile / packageDoc / publishArtifact := false
  )
