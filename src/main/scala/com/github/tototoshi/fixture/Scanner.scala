package com.github.tototoshi.fixture

private[fixture] trait Scanner {

  def scan(scriptName: String): Option[FixtureScript]

}