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
abstract class Model[E <: Entity,T <: TableBase[E]]{
  def query: TableQuery[T]

  def referencedModels: Map[String,Model[_ <: Entity,_]]

  // GUI related stuff
  trait Labels{
    def singular: String
    def plural: String
  }
  def labels: Labels

  def tinyDescription(e: E): String = labels.singular.capitalize + s"(${e.id})"

  /** model -> map(entity id -> option(related entity id -> entity tiny description))*/
  def referencedModelsAndIds(entities: Seq[E])(implicit session: Session): Map[Model[_ <: Entity,_],Map[Int,Option[(Int,String)]]]

  trait Html{
    def headings: Seq[String]
    def cells(e: E): Seq[java.io.Serializable]
  }
  def html: Html

  // FORM stuff
  def form(playForm: Form[E]): ModelForm[E]
  def playForm: Form[E]
  /** column name -> (type, required) */
  def schema: Map[String,(String,Boolean)]

  // CRUD

  /** caches compiled sql */
  private val byIdCompiled = Compiled{ (id: Column[Int]) => query.filter(_.id === id) }
  def findById(id: Int)(implicit s: Session): Option[E] = byIdCompiled(id).firstOption
  def update(entity: E)(implicit s: Session): Unit      = entity.id.map{ id =>
    byIdCompiled(id).update(entity)
  }.getOrElse{
    throw new Exception("cannot update entity without id")
  }
  def delete(id: Int)(implicit s: Session): Unit        = byIdCompiled(id).delete

  /** caches compiled sql */
  private lazy val insertInvoker = query.insertInvoker
  /** pre-compiled insert */
  def insert(entity: E)(implicit s: Session): Unit = insertInvoker.insert(entity)

  // OTHER
  /**
    * This makes up for a limitation of Scala's type inferencer.
    * It allows to typecheck and evaluate a block of code with
    * multiple occurences of the same model with unknown _ types
    * E and T. Going through this function allows the type inferencer
    * to at least know about the identity of E and T.
    */
  def typed[R](body: Model[E,_ <: TableBase[E]] => R) = body(this)

  // USE CASES

  /** caches compiled sql */
  private lazy val optionsCompiled = Compiled{
    query.map(r => r.id.asColumnOf[String] -> r.tinyDescription).sortBy(_._2)
  }
  /**
   * Construct the Map[String,String] needed to fill a select options set
   */
  def options(implicit s: Session): Seq[(String, String)] = optionsCompiled.run

  
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
}

trait ModelForm[E <: Entity]{
  def playForm: Form[E]
  def model: Model[E,_]
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
