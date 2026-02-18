ThisBuild / organization := "net.andimiller"
ThisBuild / scalaVersion := "3.3.7"
ThisBuild / version      := "1.0.0"

val munitVersion = "1.0.4"
val circeVersion = "0.14.15"
val tapirVersion = "1.13.8"

lazy val sharedSettings = Seq(
  useGpg               := true,
  pomIncludeRepository := { _ => false },
  publishMavenStyle    := true,
  publishTo            := {
    val centralSnapshots = "https://central.sonatype.com/repository/maven-snapshots/"
    if (isSnapshot.value) Some("central-snapshots" at centralSnapshots)
    else localStaging.value
  },
  licenses             := Seq("MIT" -> url("https://opensource.org/licenses/MIT")),
  scmInfo              := Some(
    ScmInfo(url("https://github.com/andimiller/enumerive"), "scm:git@github.com:andimiller/enumerive.git")
  ),
  homepage             := Some(url("https://github.com/andimiller/enumerive")),
  developers           := List(
    Developer(
      id = "andimiller",
      name = "Andi Miller",
      email = "andi@andimiller.net",
      url = url("http://andimiller.net")
    )
  )
)

lazy val root = project
  .in(file("."))
  .settings(
    name            := "enumerive",
    publish         := {},
    publishLocal    := {},
    publishArtifact := false
  )
  .aggregate(
    core.jvm,
    core.js,
    core.native,
    circe.jvm,
    circe.js,
    circe.native,
    tapir.jvm,
    tapir.js,
    tapir.native
  )

lazy val core = crossProject(JVMPlatform, JSPlatform, NativePlatform)
  .crossType(CrossType.Pure)
  .in(file("modules/core"))
  .settings(sharedSettings *)
  .settings(
    name := "enumerive-core",
    libraryDependencies ++= Seq(
      "org.scalameta" %%% "munit" % munitVersion % Test
    )
  )

lazy val circe = crossProject(JVMPlatform, JSPlatform, NativePlatform)
  .crossType(CrossType.Pure)
  .in(file("modules/circe"))
  .dependsOn(core)
  .settings(sharedSettings *)
  .settings(
    name := "enumerive-circe",
    libraryDependencies ++= Seq(
      "io.circe"      %%% "circe-core" % circeVersion,
      "org.scalameta" %%% "munit"      % munitVersion % Test
    )
  )

lazy val tapir = crossProject(JVMPlatform, JSPlatform, NativePlatform)
  .crossType(CrossType.Pure)
  .in(file("modules/tapir"))
  .dependsOn(core)
  .settings(sharedSettings *)
  .settings(
    name := "enumerive-tapir",
    libraryDependencies ++= Seq(
      "com.softwaremill.sttp.tapir" %%% "tapir-core" % tapirVersion,
      "org.scalameta"               %%% "munit"      % munitVersion % Test
    )
  )
