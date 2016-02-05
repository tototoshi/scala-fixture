package controllers

import models.User
import play.api.mvc.{ Action, Controller }

class Application extends Controller {

  def index = Action { implicit request =>
    val users = User.findAll()
    Ok(views.html.index(users))
  }
}
