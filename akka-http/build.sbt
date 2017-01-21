name := "webfx-akka"

organization := "io.digitalstream"

version := "1.0.0"

val akkaV = "10.0.1"

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

lazy val baseSettings = Seq(
  libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-http" % akkaV,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaV
  ),
  scalaVersion := "2.12.1",
  scalacOptions ++= compilerOptions
)

lazy val noPublish = Seq(
  publish := {},
  publishLocal := {},
  publishArtifact := false
)

lazy val allSettings = baseSettings ++ noPublish

lazy val `webfx-akka` = project.in(file("."))
  .settings(allSettings)
