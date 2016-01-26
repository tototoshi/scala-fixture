package com.github.tototoshi.fixture

import java.sql.Connection

// Almost taken from scalikejdbc-play-fixture
private[fixture] case class SQLFixtureScript(override val name: String, content: String) extends FixtureScript {

  private def isSetUpMarker(s: String): Boolean = s.matches("""^#.*!(SetUp|Ups).*$""")

  private def isTearDownMarker(s: String): Boolean = s.matches("""^#.*!(TearDown|Downs).*$""")

  def setUpScript: String =
    content
      .lines
      .dropWhile { line => !isSetUpMarker(line) }
      .dropWhile { line => isSetUpMarker(line) }
      .takeWhile { line => !isTearDownMarker(line) }
      .mkString("\n")

  def tearDownScript: String =
    content
      .lines
      .dropWhile { line => !isTearDownMarker(line) }
      .dropWhile { line => isTearDownMarker(line) }
      .mkString("\n")

  override def setUp(connection: Connection): Unit = {
    connection.prepareStatement(setUpScript).execute()
  }

  override def tearDown(connection: Connection): Unit = {
    connection.prepareStatement(tearDownScript).execute()
  }

}

