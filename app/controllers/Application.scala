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
  def listRedirect(modelName: String) = Redirect(routes.Application.list(modelName, 0, 2, ""))
  def index = Action { listRedirect(Computers.labels.singular) }
  
  /**
   * Display the paginated list of computers.
   *
   * @param page Current page number (starts from 0)
   * @param orderBy Column to be sorted
   * @param filter Filter applied on computer names
   */
  def list(modelName: String, page: Int, orderBy: Int, filter: String) = DBAction { implicit rs =>
    Ok(html.list(
      Model.byName(modelName),
      page,
      orderBy, filter
    ))
  }
  
  /**
   * Display the 'edit form' of a existing Computer.
   *
   * @param id Id of the computer to edit
   */
  def edit(modelName: String, id: Int) = DBAction { implicit rs =>
    Model.byName(modelName).typed{ model =>
      model.findById(id).map{e =>
        val modelForm = model.form(model.playForm.fill(e))
        Ok(html.editForm(modelForm,Some(id)))
      }.getOrElse(NotFound)
    }
  }
  
  /**
   * Handle the 'edit form' submission 
   *
   * @param id Id of the computer to edit
   */
  def write(modelName: String, id: Option[Int]) = DBAction { implicit rs =>
    Model.byName(modelName).typed{ model =>
      val form = model.playForm.bindFromRequest
      form.fold(
        formWithErrors => Left(model.form(formWithErrors)),
        entity => {
          entity.id.map{ _ =>
            id.map{i =>
              model.update(
                form.bind(form.data + ("id" -> i.toString)).get
              )
            }.getOrElse{
              throw new Exception("expected id to be passed")
            }
          }.getOrElse{
            model.insert(entity)          
          }
          Right(model.tinyDescription(entity))
        }
      ) match {
        case Left(form) => BadRequest(html.editForm(form, id))
        case Right(tinyDescription) =>
          listRedirect(model.labels.singular).flashing(
            "success" -> "%s %s has been %s".format(
              model.labels.singular.capitalize,
              tinyDescription,
              if(id.isDefined) "updated" else "created"
            )
          )
      }
    }
  }
  
  /**
   * Display the 'new computer form'.
   */
  def create(modelName: String) = DBAction { implicit rs =>
    Model.byName(modelName).typed{ model =>
      Ok(html.editForm(model.form(model.playForm),None))
    }
  }
  
  /**
   * Handle computer deletion.
   */
  def delete(modelName: String, id: Int) = DBAction { implicit rs =>
    Model.byName(modelName).typed{ model =>
      Try{
        model.delete(id)
      }.toOption
       .map(_ => listRedirect(model.labels.singular).flashing("success" -> "%s has been deleted".format(model.labels.singular.capitalize)))
       .getOrElse(
         listRedirect(model.labels.singular).flashing("error" -> "%s could not be deleted. Maybe due to a constraint.".format(model.labels.singular.capitalize))
       )
    }
  }

}
            
