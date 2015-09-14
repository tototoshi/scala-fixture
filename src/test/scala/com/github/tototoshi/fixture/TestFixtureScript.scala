package com.github.tototoshi.fixture

import java.sql.Connection

class TestFixtureScript extends FixtureScript {

  override def setUp(connection: Connection): Unit = {
    connection.prepareStatement("insert into users(id, name) values (3, 'user3')").execute()
  }

  override def tearDown(connection: Connection): Unit = {
    connection.prepareStatement("delete from users where id = 3").execute()
  }

}
