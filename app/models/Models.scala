package models

import java.util.Date
import java.sql.{ Date => SqlDate }
import play.api.Play.current
import play.api.db.slick.Config.driver.simple._
import scala.slick.lifted.Tag
import java.sql.Timestamp
import play.api.data.Form
import views.html.helper._
import play.api.i18n.Lang

import auto_generated._

trait Entity{
  def id: Option[Int]
}
trait Model[E,T]{//} <: Table[E]]{
  def playForm: Form[E]
  trait Labels{
    def singular: String
    def plural: String
  }
  def labels: Labels
  //def query: TableQuery[T]
  def form(playForm: Form[E]): ModelForm[E,T]

  def findById(id: Int)(implicit s: Session): Option[E]
  def update(id: Int, entity: E)(implicit s: Session): Unit
  def delete(id: Int)(implicit s: Session): Unit

  def tinyDescription(e: E): String

  /**
    * This makes up for a limitation of Scala's type inferencer.
    * It allows to typecheck and evaluate a block of code with
    * multiple occurences of the same model with unknown _ types
    * E and T. Going through this function allows the type inferencer
    * to at least know about the identity of E and T.
    */
  def typed[R](body: Model[E,T] => R) = body(this)
}

trait ModelForm[E,T]{//} <: Table[E]]{
  def playForm: Form[E]
  def model: Model[E,T]
  trait Html{
    def allInputs(implicit handler: FieldConstructor, lang: Lang): Seq[play.twirl.api.HtmlFormat.Appendable]
  }
  def html: Html
}

case class Page[A](items: Seq[A], page: Int, offset: Long, total: Long) {
  lazy val prev = Option(page - 1).filter(_ >= 0)
  lazy val next = Option(page + 1).filter(_ => (offset + items.size) < total)
}
object companies extends TableQuery(tag => new Companies(tag))
object computers extends TableQuery(tag => new Computers(tag))

trait CompanyModelCustomization{
  def tinyDescription(e: Company) = e.name

  /**
   * Construct the Map[String,String] needed to fill a select options set
   */
  def options(implicit s: Session): Seq[(String, String)] = {
    val query = (for {
      company <- companies
    } yield (company.id, company.name)).sortBy(_._2)
    query.list.map(row => (row._1.toString, row._2))
  }

  /**
   * Insert a new company
   * @param company
   */
  def insert(company: Company)(implicit s: Session) {
    companies.insert(company)
  }
}

trait ComputerModelCustomization{
  def tinyDescription(e: Computer) = e.name

  /**
   * Count all computers
   */
  def count(implicit s: Session): Int =
    Query(computers.length).first

  /**
   * Count computers with a filter
   * @param filter
   */
  def count(filter: String)(implicit s: Session): Int =
    Query(computers.filter(_.name.toLowerCase like filter.toLowerCase).length).first

  /**
   * Return a page of (Computer,Company)
   * @param page
   * @param pageSize
   * @param orderBy
   * @param filter
   */
  def list(page: Int = 0, pageSize: Int = 10, orderBy: Int = 1, filter: String = "%")(implicit s: Session): Page[(Computer, Option[Company])] = {

    val offset = pageSize * page
    val query =
      (for {
        (computer, company) <- computers leftJoin companies on (_.companyId === _.id)
        if computer.name.toLowerCase like filter.toLowerCase()
      } yield (computer, company.id.?, company.name.?))
        .drop(offset)
        .take(pageSize)

    val totalRows = count(filter)
    val result = query.list.map(row => (row._1, row._2.map(value => Company(row._3.get,Option(value)))))

    Page(result, page, offset, totalRows)
  }

  /**
   * Insert a new computer
   * @param computer
   */
  def insert(computer: Computer)(implicit s: Session) {
    computers.insert(computer)
  }

}
