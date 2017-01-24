name := "webfx-play"

organization := "io.digitalstream"

version := "1.0.0"

val jacksonV = "2.8.6"

lazy val compilerOptions = Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-unchecked",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Xfuture",
  "-Xlint"
)

lazy val assemblySettings = Seq(
  assemblyMergeStrategy in assembly := {
    case PathList("META-INF", "io.netty.versions.properties", xs@_*) => MergeStrategy.last
    case x =>
      val oldStrategy = (assemblyMergeStrategy in assembly).value
      oldStrategy(x)
  },
  test in assembly := {}
)

lazy val baseSettings = Seq(
  libraryDependencies ++= Seq(
    "com.fasterxml.jackson.core" % "jackson-core" % jacksonV,
    "com.fasterxml.jackson.module" %% "jackson-module-scala" % jacksonV
  ),
  routesGenerator := InjectedRoutesGenerator,
  scalaVersion := "2.11.8",
  scalacOptions ++= compilerOptions
)

lazy val noPublish = Seq(
  publish := {},
  publishLocal := {},
  publishArtifact := false
)

lazy val allSettings = assemblySettings ++ baseSettings ++ noPublish

lazy val `webfx-play` = project.in(file("."))
  .enablePlugins(PlayScala)
  .settings(allSettings)
