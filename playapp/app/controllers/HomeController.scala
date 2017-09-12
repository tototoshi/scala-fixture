package controllers

import javax.inject.Inject

import models.UserRepository
import play.api.mvc.{ AbstractController, ControllerComponents }

class HomeController @Inject() (
  cc: ControllerComponents,
  template: views.html.index,
  userRepository: UserRepository) extends AbstractController(cc) {

  def index = Action { implicit request =>
    val users = userRepository.findAll()
    Ok(template(users))
  }
}
