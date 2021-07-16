name := "webfx-spray"

organization := "io.digitalstream"

version := "1.0.0"

val akkaV = "2.4.16"
val sprayV = "1.3.4"
val sprayJsonV = "1.3.6"

lazy val compilerOptions = Seq(
  "-encoding",
  "UTF-8",
  "-feature",
  "-language:implicitConversions",
  "-deprecation",
  "-Xfatal-warnings",
)

lazy val baseSettings = Seq(
  libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaV,
    "io.spray" %% "spray-can" % sprayV,
    "io.spray" %% "spray-routing" % sprayV,
    "io.spray" %% "spray-httpx" % sprayV,
    "io.spray" %% "spray-json" % sprayJsonV
  ),
  scalaVersion := "2.11.12",
  scalacOptions ++= compilerOptions
)

lazy val assemblySettings = Seq(
  assembly / test := {}
)

lazy val noPublish = Seq(
  publish := {},
  publishLocal := {},
  publishArtifact := false
)

lazy val allSettings = assemblySettings ++ baseSettings ++ noPublish

lazy val `webfx-spray` = project.in(file("."))
  .settings(allSettings)
