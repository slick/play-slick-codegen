import sbt._
import Keys._
import play.Play.autoImport._
import PlayKeys._
import play.PlayScala
import play.twirl.sbt.Import._

object mdeBuild extends Build {
  /** main project containing main source code depending on slick and codegen project */
  lazy val mainProject = Project(
    id="computer-database-mde",
    base=file("."),
    settings = sharedSettings ++ Seq(
      version := "1.0-M1",
      name := "computer-database-mde",
      slick <<= slickCodeGenTask, // register manual sbt command
      sourceGenerators in Compile <+= slickCodeGenTask, // register automatic code generation on every compile, remove for only manual use
      TwirlKeys.templateImports ++= Seq(
        "auto_generated._",
        "play.api.db.slick.Config.driver.simple._"
      )
    )
  ).dependsOn( codegenProject ).enablePlugins(PlayScala)

  /** codegen project containing the customized code generator */
  lazy val codegenProject = Project(
    id="codegen",
    base=file("slick-codegen"),
    settings = sharedSettings
  ).settings(resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/")
  
  // shared sbt config between main project and codegen project
  val sharedSettings = Seq(
    scalaVersion := "2.10.4",
    libraryDependencies ++= List(
      "com.typesafe.play" %% "play-slick" % "0.7.0-M1",
      "org.slf4j" % "slf4j-nop" % "1.6.4",
      "com.h2database" % "h2" % "1.3.170"
    )
  )

  // slick code generation task, call in sbt using "slick-codegen"
  lazy val slick = TaskKey[Seq[File]]("slick-codegen")
  lazy val slickCodeGenTask = (scalaSource in Compile, dependencyClasspath in Compile, runner in Compile, streams) map { (dir, cp, r, s) =>
    toError(r.run("SlickCodeGenerator", cp.files, Array[String](), s.log))
    val f = new File("app/models/auto_generated/Models.scala")
    Seq(file(f.getAbsolutePath))
  }
}