name := "webfx-zio"
organization := "org.binaryfields"
version := "1.0.0"

scalaVersion := "2.13.8"
fork := true

libraryDependencies ++= Seq(
  "dev.zio" %% "zio" % "2.0.0-RC6",
  "dev.zio" %% "zio-json" % "0.3.0-RC8",
  "io.d11" %% "zhttp" % "2.0.0-RC9",
  "io.getquill" %% "quill-zio" % "3.17.0-RC2",
  "io.getquill" %% "quill-jdbc-zio" % "3.17.0-RC2",
  "com.h2database" % "h2" % "2.1.212",
  "ch.qos.logback" % "logback-core" % "1.2.11",
  "ch.qos.logback" % "logback-classic" % "1.2.11",
  "org.slf4j" % "slf4j-api" % "1.7.36",
  "org.slf4j" % "jcl-over-slf4j" % "1.7.36",
)
