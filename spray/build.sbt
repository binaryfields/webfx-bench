name := "webfx-spray"

organization := "io.digitalstream"

version := "1.0.0"

val akkaV = "2.4.16"
val sprayV = "1.3.3"
val sprayJsonV = "1.3.1"

lazy val compilerOptions = Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-unchecked",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-unused-import",
  "-Xfuture",
  "-Xlint"
)

lazy val assemblySettings = Seq(
  test in assembly := {}
)

lazy val baseSettings = Seq(
  libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaV,
    "io.spray" %% "spray-can" % sprayV,
    "io.spray" %% "spray-routing" % sprayV,
    "io.spray" %% "spray-httpx" % sprayV,
    "io.spray" %% "spray-json" % sprayJsonV
  ),
  scalaVersion := "2.11.8",
  scalacOptions ++= compilerOptions
)

lazy val noPublish = Seq(
  publish := {},
  publishLocal := {},
  publishArtifact := false
)

lazy val allSettings = assemblySettings ++ baseSettings ++ noPublish

lazy val `webfx-spray` = project.in(file("."))
  .settings(allSettings)
