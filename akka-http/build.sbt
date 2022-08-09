name := "webfx-akka"
organization := "org.binaryfields"
version := "1.0.0"

scalaVersion := "2.13.8"
fork := true

val akkaV = "2.5.26" // 2.5.31
val akkaHttpV = "10.1.12" // 10.2.0

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % akkaHttpV,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpV,
  "com.typesafe.akka" %% "akka-stream" % akkaV
)
