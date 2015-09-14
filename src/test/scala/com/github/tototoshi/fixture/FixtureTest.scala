package com.github.tototoshi.fixture

import org.flywaydb.core.Flyway
import org.scalatest.{ BeforeAndAfter, FunSuite }

class FixtureTest extends FunSuite with BeforeAndAfter {

  val driver = "org.h2.Driver"
  val url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1"
  val username = "sa"
  val password = ""

  val flyway = new Flyway
  flyway.setDataSource(url, username, password)
  flyway.setLocations("db/migration/default")

  val fixture = Fixture(driver, url, username, password)
    .scriptLocation("db/fixtures/default")
    .scriptPackage("com.github.tototoshi.fixture")
    .scripts(Seq("script1.sql", "script2.sql", "TestFixtureScript"))

  before {
    flyway.migrate()
  }

  after {
    flyway.clean()
  }

  test("load fixtures") {
    fixture.setUp()

    val db = new Database(driver, url, username, password)
    val conn = db.getConnection()

    {
      val rs = conn.prepareStatement("select * from users").executeQuery()
      rs.next()
      assert(rs.getInt("id") === 1)
      assert(rs.getString("name") === "user1")

      rs.next()
      assert(rs.getInt("id") === 2)
      assert(rs.getString("name") === "user2")

      rs.next()
      assert(rs.getInt("id") === 3)
      assert(rs.getString("name") === "user3")

      assert(rs.next() === false)
    }

    fixture.tearDown()

    {
      val rs = conn.prepareStatement("select * from users").executeQuery()
      assert(rs.next() === false)
    }

  }

}
