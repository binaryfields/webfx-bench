import sbt.Keys._
import play.sbt.PlaySettings

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    organization := "io.binaryfields",
    name := "webfx-play",
    version := "1.0.0",
    scalaVersion := "2.13.10",
    PlayKeys.playDefaultPort := 8080,
    libraryDependencies ++= Seq(
      guice,
      "com.google.inject"            % "guice"                % "5.1.0",
      "com.google.inject.extensions" % "guice-assistedinject" % "5.1.0",
      "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test
    ),
    scalacOptions ++= Seq(
      "-feature",
      "-deprecation",
      "-Xfatal-warnings"
    )
  )
