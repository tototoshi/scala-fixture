package com.github.tototoshi.fixture

import java.sql.Connection

trait FixtureScript {

  def setUp(connection: Connection): Unit

  def tearDown(connection: Connection): Unit

}
