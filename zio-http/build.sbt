lazy val root = (project in file("."))
  .settings(
    organization := "io.binaryfields",
    name := "webfx-zio",
    version := "1.0.0",
    scalaVersion := "3.2.2",
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio" % "2.0.9",
      "dev.zio" %% "zio-json" % "0.4.2",
      "io.d11" %% "zhttp" % "2.0.0-RC11",
    ),
    scalacOptions ++= Seq(
      "-language:implicitConversions"
    )
  )
