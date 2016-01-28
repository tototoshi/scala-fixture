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
    val driver = getStringConfiguration(configuration, "db.default.driver")
    val url = getStringConfiguration(configuration, "db.default.url")
    val username = getStringConfiguration(configuration, "db.default.username")
    val password = getStringConfiguration(configuration, "db.default.password")

    // scala-fixture specific configuration
    val scriptLocation = configuration.getString("db.default.fixture.scriptLocation").getOrElse("db/fixtures/default")
    val scriptPackage = configuration.getString("db.default.fixture.scriptPackage")
    val scripts = getStringSeqConfiguration(configuration, "db.default.fixture.scripts")

    val fixture = Fixture(driver, url, username, password).scriptLocation(scriptLocation)

    scriptPackage match {
      case Some(p) => fixture.scriptPackage(p).scripts(scripts)
      case None => fixture.scripts(scripts)
    }
  }

  private def isDev(environment: Environment): Boolean = environment.mode == Mode.Dev

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
