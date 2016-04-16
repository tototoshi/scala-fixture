package com.github.tototoshi.fixture

import java.sql.{ Connection, DriverManager }

private[fixture] class Database(driver: String, url: String, username: String, password: String) {

  def getConnection(): Connection = {
    Class.forName(driver)
    DriverManager.getConnection(url, username, password)
  }

}

