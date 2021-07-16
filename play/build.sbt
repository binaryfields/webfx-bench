name := "webfx-play"

organization := "io.digitalstream"

version := "1.0.0"

lazy val compilerOptions = Seq(
  "-encoding",
  "UTF-8",
  "-feature",
  "-language:implicitConversions",
  "-deprecation"
)

lazy val baseSettings = Seq(
  libraryDependencies ++= Seq(
    guice,
  ),
  routesGenerator := InjectedRoutesGenerator,
  scalaVersion := "2.13.6",
  scalacOptions ++= compilerOptions
)

lazy val assemblySettings = Seq(
  assembly / assemblyMergeStrategy := {
    case PathList("META-INF", "io.netty.versions.properties", xs@_*) => MergeStrategy.last
    case x =>
      val oldStrategy = (assembly / assemblyMergeStrategy).value
      oldStrategy(x)
  },
  assembly / test := {}
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
