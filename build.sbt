name := "computer-database-mde"

version := "1.0-M1"

lazy val root = Project("computer-database-slick", file("."))
  .enablePlugins(PlayScala)

scalaVersion := "2.10.4"

libraryDependencies ++= Seq(
  jdbc,
  "com.typesafe.play" %% "play-slick" % "0.7.0-M1"
)
