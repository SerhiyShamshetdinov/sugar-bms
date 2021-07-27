/*
 * `sugar-bms` tests has 3 options that may be added as environment variables or system properties (system property has higher priority):
 * - bmsTestDebug: to enable output of generated toolbox test code (default is `false`)
 * - bmsTestTrace: to enable output of each toolbox testcase result and typechecked toolbox test code (defaults to bmsTestDebug value)
 * - bmsTestHeavy: to enable time expensive tests with 4-operands logical expressions (default is `false`)
 *
 * Ways to pass options:
 * - in sbt cmd: call sbt with '-DbmsTestDebug=true' to enable debug & trace outputs or '-DbmsTestDebug=true -DbmsTestTrace=false' - debug without trace output
 * - in sbt shell or add to sbt cmd command: 'eval System.setProperty("bmsTestHeavy=true")' to enable heavy tests
 * - IDEA tests: in the IDEA test configuration add VM Options -DbmsTestDebug=true/false, etc.
 * - at the project level in build.sbt: add to `sugar-bms` project settings: javaOptions += "-DbmsTestDebug=true" // javaOptions: Options passed to a new JVM when forking.
 * - to pass option to test run from sbt cmd add ';set `sugar-bms`/javaOptions += "-DbmsTestDebug=true"' or ';eval System.setProperty("bmsTestDebug", "true")'
 * - add options as environment variables
 *
 * To run `sugar-bms` sbt tests for all Scala versions with non default JDK: sbt --java-home "%JAVA8_HOME%" +sugar-bms/test
 *
 * Root features tests accepts 'rootBmsArtifactDependencyVersion' environment variable or system property (system property has higher priority)
 * to test artifact of the passed version (when omitted then tests run on current sugar-bms project code):
 * sbt -DrootBmsArtifactDependencyVersion=0.1.0 "+test" // to run all Scala versions root features tests with sugar-bms ver. 0.1.0 artifact
 *
 * Usage of sbt-header plugin for the code license for both root & sugar-bms sources:
 *  sbt> headerCheckAll  // to check all configurations (Compile & Test) sources headers (including non active Scala version sources)
 *  sbt> headerCreateAll // to update or create all configurations (Compile & Test) sources headers (including non active Scala version sources)
 *
 */

import com.jsuereth.sbtpgp.PgpKeys.publishSigned
import de.heikoseeberger.sbtheader.HeaderPlugin.autoImport.headerLicense
import sbt.Def
import sbt.Keys.{aggregate, autoScalaLibrary, crossScalaVersions, homepage, mappings, packageBin, packageDoc, publishMavenStyle, publishTo, testGrouping, testOptions}
import sbt.io.Path.basic
import xerial.sbt.Sonatype.autoImport.{sonatypeProjectHosting, sonatypePublishToBundle, sonatypeRepository}

import scala.util.Try
import scala.util.control.NoStackTrace

val scala211 = "2.11.12"
val scala212 = "2.12.14" //"2.12.14" fails to compile REPL in worksheet of IDEA
val scala213 = "2.13.6"
lazy val supportedScalaVersions = List(scala211, scala212, scala213)

ThisBuild / scalaVersion := supportedScalaVersions.last
ThisBuild / version := "0.1.0"
ThisBuild / description := "A library that implements statements and logical operators on Boolean Monads and Collections in Scala"
ThisBuild / homepage := Some(url("https://github.com/SerhiyShamshetdinov/sugar-bms"))
ThisBuild / licenses += "Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")
ThisBuild / startYear := Some(2021)
ThisBuild / organization := "ua.org.sands"
ThisBuild / organizationName := "PE Shamshetdinov Serhiy (Kyiv, Ukraine)"
ThisBuild / organizationHomepage := Some(url("http://www.sands.org.ua/"))
ThisBuild / developers := List(Developer("SerhiyShamshetdinov", "Serhiy Shamshetdinov", "serhiy@sands.org.ua", url("https://github.com/SerhiyShamshetdinov")))
ThisBuild / resolvers += "Sonatype S01 OSS Snapshots" at "https://s01.oss.sonatype.org/content/repositories/snapshots"

// Release locally (this require Sonatype credentials for ua.org.sands):
// - push latest changes of 'main' or: fetch the repo, checkout the 'main' branch, ensure it is up to date
// - verify the version in build.sbt is actual release non-SNAPSHOT one (commit if you change it)
// - if committed: push changes to GitHub and wait for the tests to finish (when failed: correct the code & start from the beginning)
// - if not committed: ensure tests were passed for the last commit in the main otherwise do any change or fix tests errors, commit & wait tests results
// - to publish all cross versions do (till staged on Sonatype):
//   >;project sugar-bms ;sonatypeBundleClean ;+publishSigned ;sonatypePrepare ;sonatypeBundleUpload
//   and do the rest at Sonatype, or see https://github.com/xerial/sbt-sonatype for other commands.
//   !Do not publish from the root: it is blocked now! Only local publishing of sugar-bms will work from the root: >publishLocal or >publishLocalSigned
// - On GitHub! (not locally) add the release with _tag_ & _release title_ = 'v0.x.x' of a done release version
// - fetch repo
// - increment patch number and add '-SNAPSHOT' suffix to version in build.sbt
// - push the commit to GitHub & skip run tests!
lazy val sonatypePublishSettings = Seq[Setting[_]]( // some of settings do not work when done in ThisBuild / ...
  sonatypeCredentialHost := "s01.oss.sonatype.org", // For all Sonatype accounts created on or after February 2021
  sonatypeRepository := "https://s01.oss.sonatype.org/service/local", // For all Sonatype accounts created on or after February 2021
  sonatypeProjectHosting := Some(xerial.sbt.Sonatype.GitHubHosting("SerhiyShamshetdinov", "sugar-bms", "serhiy@sands.org.ua")),
  publishTo := sonatypePublishToBundle.value,
  publishMavenStyle := true, // To sync with Maven central, you need to supply the following information
  versionScheme := Some("early-semver"),
)

lazy val commonSettings = Seq[Setting[_]](
  crossScalaVersions := supportedScalaVersions,
  autoScalaLibrary := false,
//  Compile / unmanagedSourceDirectories ++= versionedSourceDirs((Compile / sourceDirectory).value, scalaVersion.value),
//  Test / unmanagedSourceDirectories ++= versionedSourceDirs((Test / sourceDirectory).value, scalaVersion.value),
  scalacOptions := allVersionsScalacOptions,
  Test / fork := true, // default is false
  Test / testForkedParallel := true, // required to run tests in the 1 TestGroup in parallel in 1 forked JVM
  Test / testOptions += Tests.Argument(TestFrameworks.ScalaTest, "-oDF"), // -Pn does not work with SBT
  // for sbt-header plugin to add all Scala version source files
  Compile / headerSources := ((Compile / sourceDirectory).value ** "*.scala").get,
  Test / headerSources := ((Test / sourceDirectory).value ** "*.scala").get,
  headerLicense := Some(HeaderLicense.Custom( // don't move to ThisBuild! it'll not work
    """Statements and Logical Operators on Boolean Monads and Collections
      |
      |Copyright (c) 2021 Serhiy Shamshetdinov (Kyiv, Ukraine)
      |
      |Licensed under the Apache License, Version 2.0 (the "License");
      |you may not use this file except in compliance with the License.
      |You may obtain a copy of the License at
      |
      |    http://www.apache.org/licenses/LICENSE-2.0
      |
      |Unless required by applicable law or agreed to in writing, software
      |distributed under the License is distributed on an "AS IS" BASIS,
      |WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
      |See the License for the specific language governing permissions and
      |limitations under the License.
      |
      |See the NOTICE file distributed with this work for
      |additional information regarding copyright ownership and used works.
      |""".stripMargin
  ))
)

// source dirs sample is here: https://docs.scala-lang.org/overviews/core/collections-migration-213.html#how-do-i-cross-build-my-project-against-scala-212-and-scala-213
//def versionedSourceDirs(rootDir: File, scalaVersionValue: String): Seq[File] =
//  scalaVersionValue match {
//    case ver if ver >= "3.0" =>
//      Seq()
//    case ver if ver >= "2.13" =>
//      Seq(rootDir / "scala-2@13+") // @ here means: only Scala 2 starting 2.13 inclusive and higher till Scala 3 (excluding). Contraryly 2.13+ means all version above 2.13 including 3.0, etc.
//    case ver if ifScala212at13p(ver, whenScala212at13p = true, otherwise = false) =>
//      Seq(rootDir / "scala-2.12@13+", rootDir / "scala-2.12-") // "2.12-" means: any 2.12.x and less: 2.11.x, etc.
//    case _ =>
//      Seq(rootDir / "scala-2.12.12-", rootDir / "scala-2.12-")
//  }

def getSysPropOrEnvVal(key: String): Option[String] = scala.sys.props.get(key).orElse(scala.sys.env.get(key))

def scalaCompiler(version: String) = "org.scala-lang" % "scala-compiler" % version

def scalaCompilerDependencies(configurations: String*) = libraryDependencies ++= Seq(
  scalaCompiler(scalaVersion.value)
).map(configurations.foldLeft(_)(_ % _))

def scalaTestDependencies(configurations: String*) = libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.2.3", // 3.2.9 fails with strange error for 2.12.12 in sbt +sugar-bms/test: framework does not start due to an "illegal override" error
  scalaCompiler(scalaVersion.value) // for the scala.tools.reflect.{ToolBox, ...}
).map(configurations.foldLeft(_)(_ % _))

// root defines the project to test published sugar-bms jars or sources for accessibility of full syntax for all monads
// if rootBmsArtifactDependencyVersion is None then root will depend on sugar-bms project sources directly
lazy val root = {
  val rootWithoutBms = Project("root", file("."))
    .settings(commonSettings)
    .settings(
      scalaTestDependencies(),
      publish / skip := true,
      Test / aggregate := false,
      publish / aggregate := false,
      publishSigned / aggregate := false
    )

  getSysPropOrEnvVal("rootBmsArtifactDependencyVersion").fold({
    println("[info] * sugar-bms: root test project depends directly on sugar-bms subproject")
    rootWithoutBms
      .dependsOn(`sugar-bms`)
      .aggregate(`sugar-bms`)
  }) { bmsArtifactVersion =>
    println(s"[info] * sugar-bms: root test project depends on sugar-bms '$bmsArtifactVersion' artifact")
    rootWithoutBms
      .settings(
        libraryDependencies += "ua.org.sands" %% "sugar-bms" % bmsArtifactVersion withSources() withJavadoc()
      )
  }
}

/** retrieves the Seq of bmsTest* system properties passed to sbt in command line to be passed to ScalaTest in javaOptions */
def bmsTestSystemPropertiesPassedToSbtAsJavaOptions(): Seq[String] =
  scala.sys.props.names.filter(_.startsWith("bmsTest")).flatMap { key =>
    scala.sys.props.get(key).map(value => s"-D$key=$value")
  }.toSeq

// primary project
lazy val `sugar-bms` = project.in(file("sugar-bms"))
  .settings(commonSettings)
  .settings(
    javaOptions ++= bmsTestSystemPropertiesPassedToSbtAsJavaOptions(), // sbt Description: Options passed to a new JVM when forking.
    scalaTestDependencies("compile-internal,test-internal") ,
    // include files in the `sugar-bms` jar
    Compile / packageBin / mappings ++= packageFileMappings,
    // include files in the `sugar-bms` source jar
    Compile / packageSrc / mappings ++= packageFileMappings,
    // include files in the `sugar-bms` java-doc jar
    Compile / packageDoc / mappings ++= packageFileMappings
  )
  .settings(sonatypePublishSettings)

lazy val packageFileMappings: Seq[(File, String)] = List(
  file("LICENSE"),
  file("NOTICE"),
  file("readme.md"),
  file("CONTRIBUTING.md")
).pair(basic, errorIfNone = true)

val allVersionsScalacOptions = Seq(
  "-encoding", "UTF-8",
  "-target:jvm-1.8", // jvm-1.8 is default for 2.11-2.13: for jar compatibility with all JDK starting 8. Keep in mind that util.Properties.javaVersion returns version of JVM run, not this target version. So used Java methods set depends on run JDK, not on this target version :)
  "-unchecked",
// "-Ymacro-debug-lite",
//  "-deprecation", // deprecated staff is used
  "-language:_"
)

//def versionedScalacOptions: Def.Setting[Task[Seq[String]]] = scalacOptions := {
//  CrossVersion.partialVersion(scalaVersion.value) match {
//    // if scala 2.13+ is used, quasiquotes are merged into scala-reflect, Macro annotations are available in Scala 2.13 with the -Ymacro-annotations
//    case Some((2, minor)) if minor >= 13 =>
//      scalacOptions.value ++ allVersionsScalacOptions ++ Seq("-Ymacro-annotations")
//    case _ =>
//      scalacOptions.value ++ allVersionsScalacOptions
//  }
//}
