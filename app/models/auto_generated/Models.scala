package models.auto_generated
import play.api.db.slick.Config.driver.simple._
import views.html.helper._
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.Lang
import models._
import scala.slick.model.ForeignKeyAction

object Model{
  def all = byName.values
  def byName/*: Map[String,Model[_,_]]*/ = Map(
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
  
  
  def tinyDescription = LiteralColumn("Company(") ++ id.asColumnOf[String] ++ ")"
            
}
class Companies(tag: Tag) extends CompaniesTable(tag) with CompaniesTableCustomized

class CompanyModel extends SafeModel[Company,Companies]{
  val playForm = Form(
    mapping(
      "name" -> nonEmptyText,
      "id" -> optional(number)
    )(Company.apply)(Company.unapply)
  )
  def form(playForm: Form[Company]) = CompanyForm(playForm=playForm)
  def findById(id: Int)(implicit s: Session): Option[Company] =
    companies.filter(_.id === id).firstOption
  def update(id: Int, entity: Company)(implicit s: Session) {
    companies.filter(_.id === id).update(entity.copy(id=Some(id)))
  }
  def delete(id: Int)(implicit s: Session) {
    companies.filter(_.id === id).delete
  }

  val labels = new super.Labels{
    def singular = "Company".toLowerCase
    def plural   = "Companies".toLowerCase
    object columns{
      def name: String = "Name"
  def id: String = "Id"
    }
  }
  final val query = TableQuery[Companies]
}
object Companies extends CompanyModelCustomized
case class CompanyForm(playForm: Form[Company]) extends ModelForm[Company,Companies]{
  val model = Companies
  val html = new Html
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
  lazy val companiesTableFk = foreignKey("CONSTRAINT_AE", companyId, companies)(r => r.id, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Restrict)
  
  
  def tinyDescription = LiteralColumn("Computer(") ++ id.asColumnOf[String] ++ ")"
            
}
class Computers(tag: Tag) extends ComputersTable(tag) with ComputersTableCustomized

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
    computers.filter(_.id === id).firstOption
  def update(id: Int, entity: Computer)(implicit s: Session) {
    computers.filter(_.id === id).update(entity.copy(id=Some(id)))
  }
  def delete(id: Int)(implicit s: Session) {
    computers.filter(_.id === id).delete
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
  final val query = TableQuery[Computers]
}
object Computers extends ComputerModelCustomized
case class ComputerForm(playForm: Form[Computer]) extends ModelForm[Computer,Computers]{
  val model = Computers
  val html = new Html
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