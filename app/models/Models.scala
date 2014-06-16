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
trait Model[E <: Entity]{
  def playForm: Form[E]
  trait Labels{
    def singular: String
    def plural: String
  }
  def labels: Labels
  def schema: Map[String,(String,Boolean)]
  def form(playForm: Form[E]): ModelForm[E]

  def findById(id: Int)(implicit s: Session): Option[E]
  def insert(entity: E)(implicit s: Session): Unit
  def update(id: Int, entity: E)(implicit s: Session): Unit
  def delete(id: Int)(implicit s: Session): Unit
  def list(page: Int = 0, pageSize: Int = 10, orderBy: Int = 1, filter: String = "%")(implicit s: Session): Page[E]

  def tinyDescription(e: E): String = labels.singular.capitalize + s"(${e.id})"

  def referencedModels: Map[String,Model[_ <: Entity]]
  def referencedModelsAndIds(entities: Seq[E])(implicit session: Session): Map[Model[_ <: Entity],Map[Int,Option[(Int,String)]]]
  def options(implicit s: Session): Seq[(String, String)]

  /**
    * This makes up for a limitation of Scala's type inferencer.
    * It allows to typecheck and evaluate a block of code with
    * multiple occurences of the same model with unknown _ types
    * E and T. Going through this function allows the type inferencer
    * to at least know about the identity of E and T.
    */
  def typed[R](body: Model[E] => R) = body(this)
  trait Html{
    def headings: Seq[String]
    def cells(e: E): Seq[java.io.Serializable]
  }
  def html: Html
}

trait ModelForm[E <: Entity]{
  def playForm: Form[E]
  def model: Model[E]
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

trait TableBase[E] extends Table[E]{
  def id: Column[Int]
  def tinyDescription: Column[String]
}

trait SafeModel[E <: Entity,T <: TableBase[E]] extends Model[E]{
  def query: TableQuery[T]

  /**
   * Construct the Map[String,String] needed to fill a select options set
   */
  def options(implicit s: Session): Seq[(String, String)] =
    query.map(r => r.id.asColumnOf[String] -> r.tinyDescription).sortBy(_._2).run

  def count(implicit s: Session): Int = query.length.run
  def count(filter: String)(implicit s: Session): Int =
    query.filter(_.tinyDescription.toLowerCase like filter.toLowerCase).length.run

  /**
   * Return a page of entities
   * @param page
   * @param pageSize
   * @param orderBy
   * @param filter
   */
  def list(page: Int = 0, pageSize: Int = 10, orderBy: Int = 1, filter: String = "%")(implicit s: Session): Page[E] = {

    val offset = pageSize * page
    val q = query.filter(_.tinyDescription.toLowerCase like filter.toLowerCase()).drop(offset).take(pageSize)

    val totalRows = count(filter)
    val result = q.list

    Page(result, page, offset, totalRows)
  }

  def insert(entity: E)(implicit s: Session) {
    query.insert(entity)
  }
}
