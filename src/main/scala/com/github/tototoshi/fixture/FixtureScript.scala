package com.github.tototoshi.fixture

import java.sql.Connection

trait FixtureScript {

  val name: String = this.getClass.getName

  def setUp(connection: Connection): Unit

  def tearDown(connection: Connection): Unit

}
