name := "webfx-finch"

organization := "io.digitalstream"

version := "1.0.0"

lazy val finchV = "0.12.0"
lazy val circeV = "0.7.1"

lazy val compilerOptions = Seq(
  "-encoding",
  "UTF-8",
  "-feature",
  "-language:implicitConversions",
  "-deprecation"
)

lazy val baseSettings = Seq(
  libraryDependencies ++= Seq(
    "com.github.finagle" %% "finch-core" % finchV,
    "com.github.finagle" %% "finch-circe" % finchV,
    "io.circe" %% "circe-generic" % circeV
  ),
  scalaVersion := "2.12.14",
  scalacOptions ++= compilerOptions
)

lazy val assemblySettings = Seq(
  assembly / assemblyMergeStrategy := {
    case PathList("BUILD", xs@_*) => MergeStrategy.last
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

lazy val `webfx-finch` = project.in(file("."))
  .settings(allSettings)
