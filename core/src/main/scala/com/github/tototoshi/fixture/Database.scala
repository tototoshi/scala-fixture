package com.github.tototoshi.fixture

import java.sql.{ Connection, DriverManager }

private[fixture] class Database(driver: String, url: String, username: Option[String], password: Option[String]) {

  def getConnection(): Connection = {
    Class.forName(driver)
    DriverManager.getConnection(url, username.orNull, password.orNull)
  }

}

