package models.auto_generated
// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object Models extends {
  val profile = scala.slick.driver.H2Driver
} with Models

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait Models {
  val profile: scala.slick.driver.JdbcProfile
  import profile.simple._
  import views.html.helper._
  import play.api.data.Form
  import play.api.data.Forms._
  import play.api.i18n.Lang
  import models._
  
  trait Model[T]{
    def playForm: Form[T]
    trait Html{
      def allInputs(implicit handler: FieldConstructor, lang: Lang): Seq[play.twirl.api.HtmlFormat.Appendable]
    }
    trait Labels{
      def singular: String
      def plural: String
    }
    def html: Html
    def labels: Labels
  }
  
  import scala.slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import scala.slick.jdbc.{GetResult => GR}
  
  /** DDL for all tables. Call .create to execute. */
  lazy val ddl = companies.ddl ++ computers.ddl
  
  /** Entity class storing rows of table companies
   *  @param name Database column NAME 
   *  @param id Database column ID AutoInc, PrimaryKey */
  case class Company(name: String, id: Option[Int] = None)
  /** GetResult implicit for fetching Company objects using plain SQL queries */
  implicit def GetResultCompany(implicit e0: GR[String], e1: GR[Option[Int]]): GR[Company] = GR{
    prs => import prs._
    val r = (<<?[Int], <<[String])
    import r._
    Company.tupled((_2, _1)) // putting AutoInc last
  }
  /** Table description of table COMPANY. Objects of this class serve as prototypes for rows in queries. */
  class Companies(tag: Tag) extends Table[Company](tag, "COMPANY") {
    def * = (name, id.?) <> (Company.tupled, Company.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (name.?, id.?).shaped.<>({r=>import r._; _1.map(_=> Company.tupled((_1.get, _2)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column NAME  */
    val name: Column[String] = column[String]("NAME")
    /** Database column ID AutoInc, PrimaryKey */
    val id: Column[Int] = column[Int]("ID", O.AutoInc, O.PrimaryKey)
  }
  /** Collection-like TableQuery object for table companies */
  lazy val companies = new TableQuery(tag => new Companies(tag))
  case class CompanyModel(playForm: Form[Company]) extends Model[Company]{
    val html = new Html
    class Html extends super.Html{
      // ArrayBuffer()
      def allInputs(implicit handler: FieldConstructor, lang: Lang) = Seq(
        inputs.name
      )
      object inputs{
        def name(implicit handler: FieldConstructor, lang: Lang) = inputText(playForm("name"), '_label -> labels.columns.name)
        def id(implicit handler: FieldConstructor, lang: Lang) = inputText(playForm("id"), '_label -> labels.columns.id)
      }
    }
    val labels = new super.Labels{
      def singular = "Company".toLowerCase
      def plural   = "Companies".toLowerCase
      object columns{
        def name: String = "Name"
    def id: String = "Id"
      }
    }
  
  }
  object CompanyModel extends CompanyModel(
    Form(
      mapping(
        "name" -> nonEmptyText,
        "id" -> optional(number)
      )(Company.apply)(Company.unapply)
    )
  )
  
  /** Entity class storing rows of table computers
   *  @param name Database column NAME 
   *  @param introduced Database column INTRODUCED 
   *  @param discontinued Database column DISCONTINUED 
   *  @param companyId Database column COMPANY_ID 
   *  @param id Database column ID AutoInc, PrimaryKey */
  case class Computer(name: String, introduced: Option[java.sql.Date], discontinued: Option[java.sql.Date], companyId: Option[Int], id: Option[Int] = None)
  /** GetResult implicit for fetching Computer objects using plain SQL queries */
  implicit def GetResultComputer(implicit e0: GR[String], e1: GR[Option[java.sql.Date]], e2: GR[Option[Int]]): GR[Computer] = GR{
    prs => import prs._
    val r = (<<?[Int], <<[String], <<?[java.sql.Date], <<?[java.sql.Date], <<?[Int])
    import r._
    Computer.tupled((_2, _3, _4, _5, _1)) // putting AutoInc last
  }
  /** Table description of table COMPUTER. Objects of this class serve as prototypes for rows in queries. */
  class Computers(tag: Tag) extends Table[Computer](tag, "COMPUTER") {
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
    lazy val companiesFk = foreignKey("CONSTRAINT_AE", companyId, companies)(r => r.id, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Restrict)
  }
  /** Collection-like TableQuery object for table computers */
  lazy val computers = new TableQuery(tag => new Computers(tag))
  case class ComputerModel(playForm: Form[Computer]) extends Model[Computer]{
    val html = new Html
    class Html extends super.Html{
      // ArrayBuffer(Column(COMPANY_ID,QualifiedName(COMPUTER,None,Some(SLICK_CODEGEN)),Int,true,Set()))
      def allInputs(implicit handler: FieldConstructor, lang: Lang) = Seq(
        inputs.name,
      inputs.introduced,
      inputs.discontinued
      )
      object inputs{
        def name(implicit handler: FieldConstructor, lang: Lang) = inputText(playForm("name"), '_label -> labels.columns.name)
        def introduced(implicit handler: FieldConstructor, lang: Lang) = inputText(playForm("introduced"), '_label -> labels.columns.introduced)
        def discontinued(implicit handler: FieldConstructor, lang: Lang) = inputText(playForm("discontinued"), '_label -> labels.columns.discontinued)
        def companyId(implicit handler: FieldConstructor, lang: Lang) = inputText(playForm("companyId"), '_label -> labels.columns.companyId)
        def id(implicit handler: FieldConstructor, lang: Lang) = inputText(playForm("id"), '_label -> labels.columns.id)
      }
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
  
  }
  object ComputerModel extends ComputerModel(
    Form(
      mapping(
        "name" -> nonEmptyText,
        "introduced" -> optional(sqlDate("yyyy-MM-dd")),
        "discontinued" -> optional(sqlDate("yyyy-MM-dd")),
        "companyId" -> optional(number),
        "id" -> optional(number)
      )(Computer.apply)(Computer.unapply)
    )
  )
}