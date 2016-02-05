package models

import scalikejdbc._

case class User(id: Int, name: String)

object User {

  def *(rs: WrappedResultSet): User = User(rs.int("id"), rs.string("name"))

  def findAll(): List[User] = {
    DB.readOnly { implicit session =>
      sql"SELECT * FROM users".map(*).list.apply()
    }
  }

}