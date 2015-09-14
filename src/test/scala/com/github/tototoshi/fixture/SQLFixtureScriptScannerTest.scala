package com.github.tototoshi.fixture

import org.scalatest.FunSuite

class SQLFixtureScriptScannerTest extends FunSuite {

  val scanner = new SQLFixtureScriptScanner(getClass.getClassLoader, "")

  test("create resource path from prefix and script name") {
    assert(scanner.createResourcePath("hoge", "test.txt") === "hoge/test.txt")
    assert(scanner.createResourcePath("hoge", "/test.txt") === "hoge/test.txt")
    assert(scanner.createResourcePath("", "test.txt") === "test.txt")
  }

  test("scan from files classpath") {
    assert(scanner.scan("test.txt") === Some(SQLFixtureScript("test")))
  }

}
