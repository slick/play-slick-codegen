package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.db.slick._
import play.api.Play.current

import views._
import models._
import models.auto_generated.Models._
import FormMappings._
import views.html.helper._

/**
 * Manage a database of computers
 */
object Application extends Controller { 
  /**
   * This result directly redirect to the application home.
   */
  val Home = Redirect(routes.Application.list(0, 2, ""))
  
  // -- Actions

  /**
   * Handle default path requests, redirect to computers list
   */  
  def index = Action { Home }
  
  /**
   * Display the paginated list of computers.
   *
   * @param page Current page number (starts from 0)
   * @param orderBy Column to be sorted
   * @param filter Filter applied on computer names
   */
  def list(page: Int, orderBy: Int, filter: String) = DBAction { implicit rs =>
    Ok(html.list(
      Computers.list(page = page, orderBy = orderBy, filter = ("%"+filter+"%")),
      orderBy, filter
    ))
  }
  
  /**
   * Display the 'edit form' of a existing Computer.
   *
   * @param id Id of the computer to edit
   */
  def edit(id: Int, modelName: String = "Computer") = DBAction { implicit rs =>
    Model.byName(modelName).fillFormById(id).map(m => Ok(html.editForm(id,m))).getOrElse(NotFound)
  }
  
  /**
   * Handle the 'edit form' submission 
   *
   * @param id Id of the computer to edit
   */
  def update(id: Int) = DBAction { implicit rs =>
    Computers.form.bindFromRequest.fold(
      formWithErrors => BadRequest(html.editForm(id, Computers.copy(form=formWithErrors))),
      computer => {
        Computers.update(id, computer)
        Home.flashing("success" -> "Computer %s has been updated".format(computer.name))
      }
    )
  }
  
  /**
   * Display the 'new computer form'.
   */
  def create = DBAction { implicit rs =>
    Ok(html.createForm(Computers.form, Companies.options))
  }
  
  /**
   * Handle the 'new computer form' submission.
   */
  def save = DBAction { implicit rs =>
    Computers.form.bindFromRequest.fold(
      formWithErrors => BadRequest(html.createForm(formWithErrors, Companies.options)),
      computer => {
        Computers.insert(computer)
        Home.flashing("success" -> "Computer %s has been created".format(computer.name))
      }
    )
  }
  
  /**
   * Handle computer deletion.
   */
  def delete(id: Int) = DBAction { implicit rs =>
    Computers.delete(id)
    Home.flashing("success" -> "Computer has been deleted")
  }

}
            
