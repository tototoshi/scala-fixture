package com.github.tototoshi.fixture

import org.scalatest.FunSuite

class JavaFixtureScriptScannerTest extends FunSuite {

  test("search java-based fixture script") {
    val scanner = new JavaFixtureScriptScanner(getClass.getClassLoader, "")
    val script = scanner.scan("com.github.tototoshi.fixture.TestFixtureScript").getOrElse(sys.error("failed to load script"))
    assert(script.isInstanceOf[FixtureScript])
  }

  test("search java-based fixture script with package") {
    val scanner = new JavaFixtureScriptScanner(getClass.getClassLoader, "com.github.tototoshi.fixture")
    val script = scanner.scan("TestFixtureScript").getOrElse(sys.error("failed to load script"))
    assert(script.isInstanceOf[FixtureScript])
  }

}
