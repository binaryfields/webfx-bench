val Http4sVersion = "1.0.0-M38"
val CircleVersion = "0.14.4"
val LogbackVersion = "1.4.5"
val MunitVersion = "0.7.29"
val MunitCatsEffectVersion = "1.0.6"

lazy val root = (project in file("."))
  .settings(
    organization := "io.binaryfields",
    name := "webfx-http4s",
    version := "1.0.0",
    scalaVersion := "3.2.2",
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-blaze-server" % Http4sVersion,
      //"org.http4s" %% "http4s-ember-server" % Http4sVersion,
      "org.http4s" %% "http4s-circe" % Http4sVersion,
      "org.http4s" %% "http4s-dsl" % Http4sVersion,
      "org.scalameta" %% "munit" % MunitVersion % Test,
      "io.circe" %% "circe-generic" % CircleVersion,
      "io.circe" %% "circe-literal" % CircleVersion,
      //"ch.qos.logback" % "logback-core" % LogbackVersion,
      //"ch.qos.logback" % "logback-classic" % LogbackVersion,
      "org.typelevel" %% "munit-cats-effect-3" % MunitCatsEffectVersion % Test,
    ),
    testFrameworks += new TestFramework("munit.Framework")
  )
