name := "webfx-finch"

organization := "io.digitalstream"

version := "1.0.0"

lazy val finchV = "0.12.0"
lazy val circeV = "0.7.0"

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
    "com.github.finagle" %% "finch-core" % finchV,
    "com.github.finagle" %% "finch-circe" % finchV,
    "io.circe" %% "circe-generic" % circeV
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

lazy val `webfx-finch` = project.in(file("."))
  .settings(allSettings)
