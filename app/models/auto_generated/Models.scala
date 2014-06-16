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
  def byName: Map[String,SafeModel[_ <: Entity,_ <: TableBase[_ <: Entity]]] = Map(
    "company" -> Companies,
    "computer" -> Computers
  )
}

/** Entity class storing rows of table companies
 *  @param name Database column NAME 
 *  @param id Database column ID AutoInc, PrimaryKey */
case class Company(name: String, id: Option[Int] = None) extends Entity
/** Table description of table COMPANY. Objects of this class serve as prototypes for rows in queries. */
abstract class CompaniesTable(tag: Tag) extends Table[Company](tag, "COMPANY") with TableBase[Company] {
  def * = (name, id.?) <> (Company.tupled, Company.unapply)
  /** Maps whole row to an option. Useful for outer joins. */
  def ? = (name.?, id.?).shaped.<>({r=>import r._; _1.map(_=> Company.tupled((_1.get, _2)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
  
  /** Database column NAME  */
  val name: Column[String] = column[String]("NAME")
  /** Database column ID AutoInc, PrimaryKey */
  val id: Column[Int] = column[Int]("ID", O.AutoInc, O.PrimaryKey)
  
  
  def tinyDescription = name
            
}
class Companies(tag: Tag) extends CompaniesTable(tag)

class CompanyModel extends SafeModel[Company,Companies]{
  val playForm = Form(
    mapping(
      "name" -> nonEmptyText,
      "id" -> optional(number)
    )(Company.apply)(Company.unapply)
  )
  def form(playForm: Form[Company]) = CompanyForm(playForm=playForm)
  def findById(id: Int)(implicit s: Session): Option[Company] =
    query.filter(_.id === id).firstOption
  def update(id: Int, entity: Company)(implicit s: Session) {
    query.filter(_.id === id).update(entity.copy(id=Some(id)))
  }
  def delete(id: Int)(implicit s: Session) {
    query.filter(_.id === id).delete
  }

  val labels = new super.Labels{
    def singular = "Company".toLowerCase
    def plural   = "Companies".toLowerCase
    object columns{
      def name: String = "Name"
      def id: String = "Id"
    }
  }

  val referencedModels: Map[String,Model[_ <: Entity]] = Map(
    
  )


  def referencedModelsAndIds(entities: Seq[Company])(implicit session: Session): Map[Model[_ <: Entity],Map[Int,Option[(Int,String)]]] = {
    Map(
      
    )
  }


  override def tinyDescription(e: Company) = e.name

  val schema = Map(
    "name" -> ("String", false)
  )

  final val query = TableQuery[Companies]
  override val html = new Html
  class Html extends super.Html{
    def headings = Seq(labels.columns.name)
    def cells(e: Company) = {
      def render(v: Any) = v match {
        case None => <em> - </em>
        case d:java.sql.Date => new java.text.SimpleDateFormat("dd MMM yyyy").format(d)
        case v => v.toString
      }
      Seq[Any](e.name).map{
        case Some(v) => render(v)
        case v => render(v)
      }
    }
  }
}
object Companies extends CompanyModel
case class CompanyForm(playForm: Form[Company]) extends ModelForm[Company]{
  val model = Companies
  override val html = new Html
  class Html extends super.Html{
    // ArrayBuffer()
    def allInputs(implicit handler: FieldConstructor, lang: Lang) = Seq(
      inputs.name
    )
    object inputs{
      def name(implicit handler: FieldConstructor, lang: Lang) = inputText(playForm("name"), '_label -> model.labels.columns.name)
      def id(implicit handler: FieldConstructor, lang: Lang) = inputText(playForm("id"), '_label -> model.labels.columns.id)
    }
  }  
}

/** Entity class storing rows of table computers
 *  @param name Database column NAME 
 *  @param introduced Database column INTRODUCED 
 *  @param discontinued Database column DISCONTINUED 
 *  @param companyId Database column COMPANY_ID 
 *  @param id Database column ID AutoInc, PrimaryKey */
case class Computer(name: String, introduced: Option[java.sql.Date], discontinued: Option[java.sql.Date], companyId: Option[Int], id: Option[Int] = None) extends Entity
/** Table description of table COMPUTER. Objects of this class serve as prototypes for rows in queries. */
abstract class ComputersTable(tag: Tag) extends Table[Computer](tag, "COMPUTER") with TableBase[Computer] {
  def * = (name, introduced, discontinued, companyId, id.?) <> (Computer.tupled, Computer.unapply)
  /** Maps whole row to an option. Useful for outer joins. */
  def ? = (name.?, introduced, discontinued, companyId, id.?).shaped.<>({r=>import r._; _1.map(_=> Computer.tupled((_1.get, _2, _3, _4, _5)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
  
  /** Database column NAME  */
  val name: Column[String] = column[String]("NAME")
  /** Database column INTRODUCED  */
  val introduced: Column[Option[java.sql.Date]] = column[Option[java.sql.Date]]("INTRODUCED")
  /** Database column DISCONTINUED  */
  val discontinued: Column[Option[java.sql.Date]] = column[Option[java.sql.Date]]("DISCONTINUED")
  /** Database column COMPANY_ID  */
  val companyId: Column[Option[Int]] = column[Option[Int]]("COMPANY_ID")
  /** Database column ID AutoInc, PrimaryKey */
  val id: Column[Int] = column[Int]("ID", O.AutoInc, O.PrimaryKey)
  
  /** Foreign key referencing companies (database name CONSTRAINT_AE) */
  lazy val companiesTableFk = foreignKey("CONSTRAINT_AE", companyId, TableQuery[Companies])(r => r.id, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Restrict)
  
  
  def tinyDescription = name
            
}
class Computers(tag: Tag) extends ComputersTable(tag)

class ComputerModel extends SafeModel[Computer,Computers]{
  val playForm = Form(
    mapping(
      "name" -> nonEmptyText,
      "introduced" -> optional(sqlDate("yyyy-MM-dd")),
      "discontinued" -> optional(sqlDate("yyyy-MM-dd")),
      "companyId" -> optional(number),
      "id" -> optional(number)
    )(Computer.apply)(Computer.unapply)
  )
  def form(playForm: Form[Computer]) = ComputerForm(playForm=playForm)
  def findById(id: Int)(implicit s: Session): Option[Computer] =
    query.filter(_.id === id).firstOption
  def update(id: Int, entity: Computer)(implicit s: Session) {
    query.filter(_.id === id).update(entity.copy(id=Some(id)))
  }
  def delete(id: Int)(implicit s: Session) {
    query.filter(_.id === id).delete
  }

  val labels = new super.Labels{
    def singular = "Computer".toLowerCase
    def plural   = "Computers".toLowerCase
    object columns{
      def name: String = "Name"
      def introduced: String = "Introduced"
      def discontinued: String = "Discontinued"
      def companyId: String = "Company id"
      def id: String = "Id"
    }
  }

  val referencedModels: Map[String,Model[_ <: Entity]] = Map(
    "companyId" -> Companies
  )


  def referencedModelsAndIds(entities: Seq[Computer])(implicit session: Session): Map[Model[_ <: Entity],Map[Int,Option[(Int,String)]]] = {
    Map(
      {
        val rEntities = Companies.query.filter(
          _.id inSet entities.flatMap(_.companyId).distinct
        ).map(r => r.id -> (r.id -> r.tinyDescription)).run.toMap
        Companies -> entities.map( e =>
          e.id.get -> e.companyId.flatMap(rEntities.get)
        ).toMap
      }
    )
  }


  override def tinyDescription(e: Computer) = e.name

  val schema = Map(
    "name" -> ("String", false),
    "introduced" -> ("java.sql.Date", true),
    "discontinued" -> ("java.sql.Date", true)
  )

  final val query = TableQuery[Computers]
  override val html = new Html
  class Html extends super.Html{
    def headings = Seq(labels.columns.name, labels.columns.introduced, labels.columns.discontinued)
    def cells(e: Computer) = {
      def render(v: Any) = v match {
        case None => <em> - </em>
        case d:java.sql.Date => new java.text.SimpleDateFormat("dd MMM yyyy").format(d)
        case v => v.toString
      }
      Seq[Any](e.name, e.introduced, e.discontinued).map{
        case Some(v) => render(v)
        case v => render(v)
      }
    }
  }
}
object Computers extends ComputerModel
case class ComputerForm(playForm: Form[Computer]) extends ModelForm[Computer]{
  val model = Computers
  override val html = new Html
  class Html extends super.Html{
    // ArrayBuffer(Column(COMPANY_ID,QualifiedName(COMPUTER,None,Some(SLICK_CODEGEN)),Int,true,Set()))
    def allInputs(implicit handler: FieldConstructor, lang: Lang) = Seq(
      inputs.name,
      inputs.introduced,
      inputs.discontinued
    )
    object inputs{
      def name(implicit handler: FieldConstructor, lang: Lang) = inputText(playForm("name"), '_label -> model.labels.columns.name)
      def introduced(implicit handler: FieldConstructor, lang: Lang) = inputText(playForm("introduced"), '_label -> model.labels.columns.introduced)
      def discontinued(implicit handler: FieldConstructor, lang: Lang) = inputText(playForm("discontinued"), '_label -> model.labels.columns.discontinued)
      def companyId(implicit handler: FieldConstructor, lang: Lang) = inputText(playForm("companyId"), '_label -> model.labels.columns.companyId)
      def id(implicit handler: FieldConstructor, lang: Lang) = inputText(playForm("id"), '_label -> model.labels.columns.id)
    }
  }  
}