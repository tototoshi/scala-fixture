package com.github.tototoshi.fixture.play

import java.io.File
import javax.inject.{Provider, Inject, Singleton}

import com.github.tototoshi.fixture.Fixture
import play.api.{Mode, Configuration, Environment}
import play.api.inject.{Binding, Module}
import play.api.mvc.{Result, RequestHeader}
import play.api.mvc.Results._
import play.core.{BuildLink, WebCommands, HandleWebCommandSupport}

class FixtureWebCommandHandler(configuration: Configuration, environment: Environment) extends HandleWebCommandSupport {

  def handleWebCommand(request: RequestHeader, buildLink: BuildLink, path: File): Option[Result] = {
    if (!isDev(environment)) {
      None
    } else {
      request.path match {
        case "/@fixture" => Some(Ok(views.html.index()))
        case "/@fixture/setUp" =>
          createFixture(configuration).setUp()
          Some(Redirect("/@fixture"))
        case "/@fixture/tearDown" =>
          createFixture(configuration).tearDown()
          Some(Redirect("/@fixture"))
        case _ => None
      }
    }
  }

  private def createFixture(configuration: Configuration): Fixture = {
    val configurationReader = new ConfigurationReader(configuration)
    val fixtureConfigurations = configurationReader.getFixtureConfigurations
    fixtureConfigurations.get("default") match {
      case Some(config) =>
        val dbConfig = config.database
        val fixture = Fixture(dbConfig.driver, dbConfig.url, dbConfig.username, dbConfig.password).scriptLocation(config.scriptLocation)

        config.scriptPackage match {
          case Some(p) => fixture.scriptPackage(p).scripts(config.scripts)
          case None => fixture.scripts(config.scripts)
        }
      case None => sys.error("configuration error")
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

  private def getAllDatabaseNames: Seq[String] = (for {
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

class FixtureWebCommand @Inject() (configuration: Configuration, environment: Environment, webCommand: WebCommands) {
  webCommand.addHandler(new FixtureWebCommandHandler(configuration, environment))
}

@Singleton
class FixtureWebCommandProvider @Inject() (configuration: Configuration, environment: Environment, webCommands: WebCommands)
    extends Provider[FixtureWebCommand] {
  override def get(): FixtureWebCommand = new FixtureWebCommand(configuration, environment, webCommands)
}

class FixtureWebCommandModule extends Module {
  override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] = {
    Seq(bind[FixtureWebCommand].toProvider[FixtureWebCommandProvider].eagerly)
  }
}
