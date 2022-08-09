name := "webfx-spray"
organization := "org.binaryfields"
version := "1.0.0"

scalaVersion := "2.11.12"
fork := true

val akkaV = "2.4.16"
val sprayV = "1.3.4"
val sprayJsonV = "1.3.6"

libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaV,
    "io.spray" %% "spray-can" % sprayV,
    "io.spray" %% "spray-routing" % sprayV,
    "io.spray" %% "spray-httpx" % sprayV,
    "io.spray" %% "spray-json" % sprayJsonV
)
