import scala.slick.model.codegen.SourceCodeGenerator
import scala.slick.model.Model
import scala.slick.driver.H2Driver

object SlickCodeGenerator extends App{
  val jdbcDriver = "org.h2.Driver"
  val slickProfile = scala.slick.driver.H2Driver
  val url = s"jdbc:h2:mem:slick_codegen;init=runscript from 'conf/schema.sql'"

  val db = slickProfile.simple.Database.forURL(url,driver=jdbcDriver)

  import java.io.File
  val path = new File("app/models/auto_generated")
  scala.util.Try(path.listFiles().filter(_.getName.endsWith(".scala")) foreach { _.delete() })
  
  class SlickCodeGenerator(val model: Model) extends SourceCodeGenerator(model: Model){ gen =>
    override def tableName = _ match {
      case "COMPANY" => "Companies"
      case n => n.toCamelCase+"s"
    }
    override def entityName = _.toCamelCase
    override def Table = new Table(_){
      override def autoIncLastAsOption = true
      override def TableValue = new TableValue{
        override def rawName = super.rawName.head.toString.toLowerCase + super.rawName.tail
      }
    }
  }
  val codegen = new SlickCodeGenerator(db.withSession(s => H2Driver.createModel(s)))
  codegen.writeToFile(
    "scala.slick.driver.H2Driver",
    "app",
    "models.auto_generated",
    "Models",
    "Models.scala"
  )
}