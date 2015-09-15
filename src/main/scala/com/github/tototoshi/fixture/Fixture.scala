package com.github.tototoshi.fixture

case class Fixture(
    driver: String,
    url: String,
    username: String,
    password: String,
    classLoader: ClassLoader = Thread.currentThread().getContextClassLoader,
    scripts: Seq[String] = Seq(),
    scriptLocation: String = "",
    scriptPackage: String = "") {

  def classLoader(classLoader: ClassLoader): Fixture = this.copy(classLoader = classLoader)

  def scripts(scripts: Seq[String]): Fixture = this.copy(scripts = scripts)

  def scriptLocation(scriptPrefix: String): Fixture = this.copy(scriptLocation = scriptPrefix)

  def scriptPackage(scriptPackage: String): Fixture = this.copy(scriptPackage = scriptPackage)

  private[fixture] def scan(): Seq[FixtureScript] = {
    val sqlScanner = new SQLFixtureScriptScanner(classLoader, scriptLocation)
    val javaScanner = new JavaFixtureScriptScanner(classLoader, scriptPackage)
    scripts.map { script =>
      sqlScanner.scan(script)
        .orElse(javaScanner.scan(script))
        .getOrElse(throw new FixtureScriptNotFoundException(s"Fixture script is not found. [script=$script]"))
    }
  }

  def setUp(): Unit = {
    val db = new Database(driver, url, username, password)
    val conn = db.getConnection()
    scan.foreach { script => script.setUp(conn) }
  }

  def tearDown(): Unit = {
    val db = new Database(driver, url, username, password)
    val conn = db.getConnection()
    scan.reverse.foreach { script => script.tearDown(conn) }
  }

}

