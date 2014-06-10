package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.db.slick._
import play.api.Play.current

import views._
import models._
import models.auto_generated._
import FormMappings._
import views.html.helper._
import scala.util.Try

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
  def edit(id: Int, modelName: String) = DBAction { implicit rs =>
    Model.byName(modelName).typed{ model =>
      model.findById(id).map{e =>
        val modelForm = model.form(model.playForm.fill(e))
        Ok(html.editForm(id,modelForm))
      }.getOrElse(NotFound)
    }
  }
  
  /**
   * Handle the 'edit form' submission 
   *
   * @param id Id of the computer to edit
   */
  def write_edit(id: Int, modelName: String) = DBAction { implicit rs =>
    Model.byName(modelName).typed{ model =>
      model.playForm.bindFromRequest.fold(
        formWithErrors => Left(model.form(formWithErrors)),
        entity => {
          model.update(id, entity)
          Right(model.tinyDescription(entity))
        }
      ).fold(
        form => BadRequest(html.editForm(id, form)),
        tinyDescription => Home.flashing("success" -> "%s %s has been updated".format(model.labels.singular.capitalize, tinyDescription))
      )
    }
  }
  
  /**
   * Display the 'new computer form'.
   */
  def create = DBAction { implicit rs =>
    Ok(html.createForm(Computers.playForm, Companies.options))
  }
  
  /**
   * Handle the 'new computer form' submission.
   */
  def save = DBAction { implicit rs =>
    Computers.playForm.bindFromRequest.fold(
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
  def delete(id: Int, modelName: String) = DBAction { implicit rs =>
    val model = Model.byName(modelName)
    Try{
      model.typed{_.delete(id)}
    }.toOption
     .map(_ => Home.flashing("success" -> "%s has been deleted".format(model.labels.singular.capitalize)))
     .getOrElse(
       Home.flashing("error" -> "%s could not be deleted. Maybe due to a constraint.".format(model.labels.singular.capitalize))
     )
  }

}
            
