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
  }
  /** Collection-like TableQuery object for table computers */
  lazy val computers = new TableQuery(tag => new Computers(tag))
}