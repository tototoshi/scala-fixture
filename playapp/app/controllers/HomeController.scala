package controllers

import javax.inject.Inject

import models.UserRepository
import play.api.mvc.{ Action, Controller }

class HomeController @Inject() (webJarAssets: WebJarAssets, userRepository: UserRepository) extends Controller {

  def index = Action { implicit request =>
    val users = userRepository.findAll()
    Ok(views.html.index(webJarAssets, users))
  }
}
