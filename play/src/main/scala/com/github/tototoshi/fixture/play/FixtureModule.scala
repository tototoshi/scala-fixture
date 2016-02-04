package com.github.tototoshi.fixture.play

import java.io.{ByteArrayOutputStream, File, InputStream}
import java.util.regex.Pattern
import javax.inject.{Inject, Provider, Singleton}

import com.github.tototoshi.fixture.Fixture
import org.webjars.WebJarAssetLocator
import play.api.inject.{Binding, Module}
import play.api.mvc.Results._
import play.api.mvc.{RequestHeader, Result}
import play.api.{Configuration, Environment, Mode}
import play.core.{BuildLink, HandleWebCommandSupport, WebCommands}

class FixtureWebCommandHandler(configuration: Configuration, environment: Environment) extends HandleWebCommandSupport {

  private val configurationReader = new ConfigurationReader(configuration)

  private val webJarAssetLocator = new WebJarAssetLocator(WebJarAssetLocator.getFullPathIndex(Pattern.compile(".*"), environment.classLoader))

  private val fixtureConfigurations = configurationReader.getFixtureConfigurations

  private object Path {
    def unapplySeq(s: String): Option[Seq[String]] =
      if (s.trim.isEmpty) {
        None
      } else {
        Some(s.split("/").dropWhile(_.isEmpty).takeWhile(_.nonEmpty))
      }
  }

  def handleWebCommand(request: RequestHeader, buildLink: BuildLink, path: File): Option[Result] = {

    def readInputStreamToString(stream: InputStream): String = {
      val buffer = new Array[Byte](8192)
      var len = stream.read(buffer)
      using(new ByteArrayOutputStream()) { out =>
        while (len != -1) {
          out.write(buffer, 0, len)
          len = stream.read(buffer)
        }
        new String(out.toByteArray)
      }
    }

    if (!isDev(environment)) {
      None
    } else {
      request.path match {
        case Path("@fixture") => Some(Ok(views.html.index(configurationReader.getAllDatabaseNames)))
        case Path("@fixture", dbName) =>
          fixtureConfigurations.get(dbName).map { configuration =>
            Some(Ok(views.html.show(dbName, configuration.scripts)))
          }.getOrElse(Some(NotFound))
        case Path("@fixture", dbName, "setUp") =>
          createFixture(configuration, dbName).setUp()
          Some(Redirect(s"/@fixture/${dbName}"))
        case Path("@fixture", dbName, "tearDown") =>
          createFixture(configuration, dbName).tearDown()
          Some(Redirect(s"/@fixture/${dbName}"))
        case Path("@fixture", "assets", assets@_*) =>
          Option(environment.classLoader.getResource(webJarAssetLocator.getFullPath(assets.mkString("/")))).map {
            resource =>
              using(resource.openStream()) { stream =>
                Some(Ok(readInputStreamToString(stream)))
              }
          }.getOrElse(Some(NotFound))
        case _ => None
      }
    }
  }

  private def using[A <: {def close() : Unit}, B](resource: A)(f: A => B): B = {
    try {
      f(resource)
    } finally {
      resource.close()
    }
  }

  private def createFixture(configuration: Configuration, dbName: String): Fixture = {
    fixtureConfigurations.get(dbName) match {
      case Some(config) =>
        val dbConfig = config.database
        val fixture = Fixture(dbConfig.driver, dbConfig.url, dbConfig.username, dbConfig.password).scriptLocation(config.scriptLocation)

        config.scriptPackage match {
          case Some(p) => fixture.scriptPackage(p).scripts(config.scripts)
          case None => fixture.scripts(config.scripts)
        }
      case None => sys.error( s"""Configuration of Database "${dbName}" is missing""")
    }
  }

  private def isDev(environment: Environment): Boolean = environment.mode == Mode.Dev

}

case class DatabaseConfiguration(driver: String, url: String, username: String, password: String)

case class FixtureConfiguration(database: DatabaseConfiguration, scriptLocation: String, scriptPackage: Option[String], scripts: Seq[String])

class ConfigurationReader(configuration: Configuration) {

  def getFixtureConfigurations: Map[String, FixtureConfiguration] = {
    getAllDatabaseNames.map { databaseName => (databaseName, getFixtureConfiguration(databaseName)) }.toMap
  }

  private def getFixtureConfiguration(databaseName: String): FixtureConfiguration = {
    val driver = getStringConfiguration(configuration, s"db.${databaseName}.driver")
    val url = getStringConfiguration(configuration, s"db.${databaseName}.url")
    val username = getStringConfiguration(configuration, s"db.${databaseName}.username")
    val password = getStringConfiguration(configuration, s"db.${databaseName}.password")

    val databaseConfiguration = DatabaseConfiguration(driver, url, username, password)

    // scala-fixture specific configuration
    val scriptLocation = configuration.getString(s"db.${databaseName}.fixture.scriptLocation").getOrElse(s"db/fixtures/${databaseName}")
    val scriptPackage = configuration.getString(s"db.${databaseName}.fixture.scriptPackage")
    val scripts = getStringSeqConfiguration(configuration, s"db.${databaseName}.fixture.scripts")

    FixtureConfiguration(databaseConfiguration, scriptLocation, scriptPackage, scripts)
  }

  def getAllDatabaseNames: Seq[String] = (for {
    config <- configuration.getConfig("db").toList
    dbName <- config.subKeys
  } yield {
    dbName
  }).distinct

  private def getStringConfiguration(configuration: Configuration, key: String): String =
    configuration.getString(key).getOrElse(sys.error(s"Configuration of ${key} is missing"))

  private def getStringSeqConfiguration(configuration: Configuration, key: String): Seq[String] = {
    import scala.collection.JavaConverters._
    configuration.getStringList(key).getOrElse(sys.error(s"Configuration of ${key} is missing")).asScala
  }

}

class FixtureWebCommand @Inject()(configuration: Configuration, environment: Environment, webCommand: WebCommands) {
  webCommand.addHandler(new FixtureWebCommandHandler(configuration, environment))
}

@Singleton
class FixtureWebCommandProvider @Inject()(configuration: Configuration, environment: Environment, webCommands: WebCommands)
    extends Provider[FixtureWebCommand] {
  override def get(): FixtureWebCommand = new FixtureWebCommand(configuration, environment, webCommands)
}

class FixtureWebCommandModule extends Module {
  override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] = {
    Seq(bind[FixtureWebCommand].toProvider[FixtureWebCommandProvider].eagerly)
  }
}
