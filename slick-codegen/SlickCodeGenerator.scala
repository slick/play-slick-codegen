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
    override def code = {
      def modelLabels(t:Table) = {
        def fieldLabel(c: t.Column) = {
          s"""
def ${c.name}: String = "${c.model.name.replace("_"," ").toLowerCase.capitalize}"
          """.trim
        }

        s"""
object ${t.EntityType.name}{
  def singular = "${t.EntityType.name}"
  def plural   = "${t.TableClass.name}"
  ${indent(t.columns.map(fieldLabel).mkString("\n"))}
}
        """.trim
      }
      s"""
class ModelLabels{
  ${indent(tables.map(modelLabels).mkString("\n"))}  
}
      """.trim + "\n\n" + super.code
    }
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
      override def code = {
        def input(c: Column) = s"""
def ${c.name}(form: Form[${EntityType.name}])(implicit handler: FieldConstructor, lang: Lang) = inputText(form("${c.name}"), '_label -> ModelLabels.${EntityType.name}.${c.name})
          """.trim
        def formField(c: Column) = {
          val rawFieldType = c.rawType match {
            case "Int" => "number"
            case "String" => "nonEmptyText"
            case "java.sql.Date" => """sqlDate("yyyy-MM-dd")"""
          }
          val fieldType = if (c.fakeNullable || c.model.nullable) s"optional($rawFieldType)" else rawFieldType
          s"""
"${c.name}" -> $fieldType
          """.trim
        }

        super.code ++ Seq(s"""
import views.html.helper._
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.Lang
import models._
object ${TableClass.name}Form{
  val form = Form(
    mapping(
      ${indent(indent(indent(columns.map(formField).mkString(",\n"))))}
    )(${EntityType.name}.apply)(${EntityType.name}.unapply)
  )
  // ${model.foreignKeys.map(_.referencingColumns.head).toString}
  def allInputs(form: Form[${EntityType.name}])(implicit handler: FieldConstructor, lang: Lang) = Seq(
    ${indent(indent(
        columns
          // not include auto inc columns
          .filterNot(_.autoInc)
          // not include foreign keys
          .filterNot(c => model.foreignKeys.map(_.referencingColumns.head.name) contains c.model.name)
          .map(_.name)
          .map("Inputs."+_+"(form)")
          .mkString(",\n")
    ))}    
  )
  object Inputs{
    ${indent(indent(columns.map(input).mkString("\n")))}
  }
}
        """.trim)
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