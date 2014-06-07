package models

import java.util.Date
import java.sql.{ Date => SqlDate }
import play.api.Play.current
import play.api.db.slick.Config.driver.simple._
import scala.slick.lifted.Tag
import java.sql.Timestamp

case class Page[A](items: Seq[A], page: Int, offset: Long, total: Long) {
  lazy val prev = Option(page - 1).filter(_ >= 0)
  lazy val next = Option(page + 1).filter(_ => (offset + items.size) < total)
}

import auto_generated.Models._

object Companies {  
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

object Computers {
  /**
   * Retrieve a computer from the id
   * @param id
   */
  def findById(id: Int)(implicit s: Session): Option[Computer] =
    computers.filter(_.id === id).firstOption

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

  /**
   * Update a computer
   * @param id
   * @param computer
   */
  def update(id: Int, computer: Computer)(implicit s: Session) {
    val computerToUpdate: Computer = computer.copy(id=Some(id))
    computers.filter(_.id === id).update(computerToUpdate)
  }

  /**
   * Delete a computer
   * @param id
   */
  def delete(id: Int)(implicit s: Session) {
    //computers.filter(_.id === id).delete // FIXME
  }
}
