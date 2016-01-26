package com.github.tototoshi.fixture

import java.nio.file.{ Files, Paths }

import org.scalatest.FunSuite

class SQLFixtureScriptTest extends FunSuite {

  test("Parse script file") {
    val path = Paths.get(getClass.getClassLoader.getResource("db/fixtures/default/script1.sql").toURI)
    val content = new String(Files.readAllBytes(path), "UTF-8")
    val script = SQLFixtureScript(path.toFile.getName, content)
    assert(script.setUpScript === "INSERT INTO users(id, name) VALUES (1, 'user1');")
    assert(script.tearDownScript === "DELETE FROM users WHERE id = 1;")
  }

}
