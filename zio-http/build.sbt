name := "webfx-zio"
organization := "org.binaryfields"
version := "1.0.0"

scalaVersion := "3.1.3"
scalacOptions ++= Seq(
  "-language:implicitConversions"
)
fork := true

libraryDependencies ++= Seq(
  "dev.zio" %% "zio" % "2.0.0-RC6",
  "dev.zio" %% "zio-json" % "0.3.0-RC8",
  "io.d11" %% "zhttp" % "2.0.0-RC9",
)
