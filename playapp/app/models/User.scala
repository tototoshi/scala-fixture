package models

import javax.inject.Inject

import scalikejdbc._

case class User(id: Int, name: String)

class UserRepository @Inject() (connectionPool: ConnectionPool) {

  def db = DB(connectionPool.borrow())

  def *(rs: WrappedResultSet): User = User(rs.int("id"), rs.string("name"))

  def findAll(): List[User] = {
    db.readOnly { implicit session =>
      sql"SELECT * FROM users".map(*).list.apply()
    }
  }

}