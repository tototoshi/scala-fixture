package controllers

import play.api.mvc.{Action, Controller}
import scalikejdbc._

class Application extends Controller {

  def index = Action { implicit request =>
    val users = DB.readOnly { implicit session =>
      sql"SELECT * FROM users".map(rs => (rs.int("id"), rs.string("name"))).list.apply()
    }
    Ok(users.toString)
  }
}
