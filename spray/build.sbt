val akkaV = "2.4.16"
val sprayV = "1.3.4"
val sprayJsonV = "1.3.6"

lazy val root = (project in file("."))
  .settings(
    organization := "io.binaryfields",
    name := "webfx-spray",
    version := "1.0.0",
    scalaVersion := "2.11.12",    
    run / fork := true,
    libraryDependencies ++= Seq(
        "com.typesafe.akka" %% "akka-actor" % akkaV,
        "io.spray" %% "spray-can" % sprayV,
        "io.spray" %% "spray-routing" % sprayV,
        "io.spray" %% "spray-httpx" % sprayV,
        "io.spray" %% "spray-json" % sprayJsonV
    )
  )
