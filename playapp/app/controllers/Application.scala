package controllers

import play.api.mvc.{Action, Controller}

class Application extends Controller {

  def index = Action { implicit request =>
    Ok("Hello")
  }
}
