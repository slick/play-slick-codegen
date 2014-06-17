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
    override def packageCode(profile: String, pkg: String, container:String) = code
    override def code = {
      s"""
package models.auto_generated
import play.api.db.slick.Config.driver.simple._
import views.html.helper._
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.Lang
import models._
import scala.slick.model.ForeignKeyAction
import play.api.data.format.Formats

object Model{
  def all = byName.values
  def byName: Map[String,Model[_ <: Entity,_ <: TableBase[_ <: Entity]]] = Map(
    ${indent(indent(tables.map(t => "\"" + t.EntityType.name.toLowerCase + "\" -> " + tableName(t.model.name.table) ).mkString(",\n")))}
  )
}
      """.trim + "\n\n" + tables.map(_.code.mkString("\n")).mkString("\n\n")
    }
    override def Table = new Table(_){
      val E = entityName(model.name.table)
      val T = tableName(model.name.table)
      val dataColumns = columns
            // not include auto inc columns
            .filterNot(_.autoInc)
            // not include foreign keys
            .filterNot(c => model.foreignKeys.map(_.referencingColumns.head.name) contains c.model.name)

      override def PlainSqlMapper = new PlainSqlMapper{
        override def enabled = false
      }
      override def autoIncLastAsOption = true
      override def EntityType = new EntityType{
        override def parents = Seq("Entity")
      }
      override def TableClass = new TableClass{
        override def rawName = T+"Table"
        override def parents = super.parents ++ Seq(s"TableBase[$E]")
        override def body = super.body ++ Seq(Seq(s"""
def tinyDescription = ${dataColumns.head.name}
          """))
        override def code = "abstract "+super.code
      }
      override def TableValue = new TableValue{
        override def enabled = false
        override def rawName = super.rawName.head.toString.toLowerCase + super.rawName.tail
        override def code = s"object $name extends TableQuery(tag => new $T(tag))"
      }
      override def ForeignKey = new ForeignKey(_){
        override def code = {
          val fkColumns = compound(referencingColumns.map(_.name))
          val pkTable = tableName(referencedTable.model.name.table)
          val pkColumns = compound(referencedColumns.map(c => s"r.${c.name}"))
          s"""lazy val $name = foreignKey("$dbName", $fkColumns, TableQuery[$pkTable])(r => $pkColumns, onUpdate=${onUpdate}, onDelete=${onDelete})"""
        }
      }
      override def code = {
        def input(c: Column) = s"""
def ${c.name}(implicit handler: FieldConstructor, lang: Lang) = inputText(playForm("${c.name}"), '_label -> model.labels.columns.${c.name})
          """.trim
        def formField(c: Column) = {
          val rawFieldType = c.rawType match {
            case "Int" => "number"
            case "String" if (c.fakeNullable || c.model.nullable) => "text"
            case "String" => "nonEmptyText"
            case "java.sql.Date" => """sqlDate("yyyy-MM-dd")"""
          }
          val fieldType = if (c.fakeNullable || c.model.nullable) s"optional($rawFieldType)" else rawFieldType
          s"""
"${c.name}" -> $fieldType
          """.trim
        }

        def fieldLabel(c: Column) = s"""
def ${c.name}: String = "${c.model.name.replace("_"," ").toLowerCase.capitalize}"
          """.trim

        def schemaColumn(c: Column) = s"""
"${c.name}" -> ("${c.rawType}", ${c.model.nullable})
          """.trim

        def referencedModel(fk: ForeignKey) = s"""
"${fk.referencingColumns.head.name}" -> ${tableName(fk.referencedTable.model.name.table)}
          """.trim
        def modelAndEntities(fk: ForeignKey) = s"""
{
  val rEntities = ${tableName(fk.referencedTable.model.name.table)}.query.filter(
    _.${fk.referencedColumns.head.name} inSet entities.flatMap(_.${fk.referencingColumns.head.name}).distinct
  ).map(r => r.${fk.referencedColumns.head.name} -> (r.id -> r.tinyDescription)).run.toMap
  ${tableName(fk.referencedTable.model.name.table)} -> entities.map( e =>
    e.id.get -> e.${fk.referencingColumns.head.name}.flatMap(rEntities.get)
  ).toMap
}
          """.trim

        super.code ++ Seq(s"""
class $T(tag: Tag) extends ${TableClass.name}(tag)

class ${E}Model extends Model[$E,$T]{
  val playForm = Form(
    mapping(
      ${indent(indent(indent(columns.map(formField).mkString(",\n"))))}
    )($E.apply)($E.unapply)
  )
  def form(playForm: Form[$E]) = ${E}Form(playForm=playForm)

  val labels = new super.Labels{
    def singular = "$E".toLowerCase
    def plural   = "$T".toLowerCase
    object columns{
      ${indent(indent(indent(columns.map(fieldLabel).mkString("\n"))))}
    }
  }

  val referencedModels: Map[String,Model[_ <: Entity,_]] = Map(
    ${indent(indent(foreignKeys.map(referencedModel).mkString(",\n")))}
  )


  def referencedModelsAndIds(entities: Seq[$E])(implicit session: Session): Map[Model[_ <: Entity,_],Map[Int,Option[(Int,String)]]] = {
    Map(
      ${indent(indent(indent(foreignKeys.map(modelAndEntities).mkString(",\n"))))}
    )
  }


  override def tinyDescription(e: $E) = e.${dataColumns.head.name}

  val schema = Map(
    ${indent(indent(dataColumns.map(schemaColumn).mkString(",\n")))}
  )

  final val query = TableQuery[$T]
  override val html = new Html
  class Html extends super.Html{
    def headings = Seq(${dataColumns.map(_.name).map("labels.columns."+_).mkString(", ")})
    def cells(e: $E) = {
      def render(v: Any) = v match {
        case None => <em> - </em>
        case d:java.sql.Date => new java.text.SimpleDateFormat("dd MMM yyyy").format(d)
        case v => v.toString
      }
      Seq[Any](${dataColumns.map(_.name).map("e."+_+"").mkString(", ")}).map{
        case Some(v) => render(v)
        case v => render(v)
      }
    }
  }
}
object $T extends ${E}Model
case class ${E}Form(playForm: Form[$E]) extends ModelForm[$E]{
  val model = $T
  override val html = new Html
  class Html extends super.Html{
    // ${model.foreignKeys.map(_.referencingColumns.head).toString}
    def allInputs(implicit handler: FieldConstructor, lang: Lang) = Seq(
      ${indent(indent(indent(
          dataColumns
            .map(_.name)
            .map("inputs."+_)
            .mkString(",\n")
      )))}
    )
    object inputs{
      ${indent(indent(indent(columns.map(input).mkString("\n"))))}
    }
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