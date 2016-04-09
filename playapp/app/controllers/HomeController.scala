package controllers

import javax.inject.Inject

import models.User
import play.api.mvc.{ Action, Controller }

class HomeController @Inject() (webJarAssets: WebJarAssets) extends Controller {

  def index = Action { implicit request =>
    val users = User.findAll()
    Ok(views.html.index(webJarAssets, users))
  }
}
