val akkaV = "2.5.26" // 2.5.31
val akkaHttpV = "10.1.12" // 10.2.0

lazy val root = (project in file("."))
  .settings(
    organization := "io.binaryfields",
    name := "webfx-akka",
    version := "1.0.0",
    scalaVersion := "2.13.10",
    run / fork := true,
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http" % akkaHttpV,
      "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpV,
      "com.typesafe.akka" %% "akka-stream" % akkaV
    )
  )
