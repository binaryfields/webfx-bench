name := "webfx-akka"

organization := "io.digitalstream"

version := "1.0.0"

val akkaV = "2.5.26" // 2.5.31
val akkaHttpV = "10.1.12" // 10.2.0

lazy val compilerOptions = Seq(
  "-encoding",
  "UTF-8",
  "-feature",
  "-language:implicitConversions",
  "-deprecation",
  "-Xfatal-warnings",
  "-Wunused:imports,privates,locals",
  "-Wvalue-discard"      
)

lazy val assemblySettings = Seq(
  assembly / test := {}
)

lazy val baseSettings = Seq(
  libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-http" % akkaHttpV,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpV,
    "com.typesafe.akka" %% "akka-stream" % akkaV
  ),
  scalaVersion := "2.13.6",
  scalacOptions ++= compilerOptions
)

lazy val noPublish = Seq(
  publish := {},
  publishLocal := {},
  publishArtifact := false
)

lazy val allSettings = assemblySettings ++ baseSettings ++ noPublish

lazy val `webfx-akka` = project.in(file("."))
  .settings(allSettings)
