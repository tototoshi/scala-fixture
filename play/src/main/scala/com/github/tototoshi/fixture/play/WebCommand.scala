package com.github.tototoshi.fixture.play

import java.io.File
import java.util.regex.Pattern
import javax.inject.{ Singleton, Inject }

import org.webjars.WebJarAssetLocator
import play.api.mvc.Results._
import play.api.mvc.{ RequestHeader, Result }
import play.api.{ Configuration, Environment, Mode }
import play.core.{ BuildLink, HandleWebCommandSupport, WebCommands }

class FixtureWebCommandHandler(configuration: Configuration, environment: Environment, fixtures: Fixtures) extends HandleWebCommandSupport {

  private val webJarAssetLocator = new WebJarAssetLocator(WebJarAssetLocator.getFullPathIndex(Pattern.compile(".*"), environment.classLoader))

  private object Path {
    def unapplySeq(s: String): Option[Seq[String]] =
      if (s.trim.isEmpty) {
        None
      } else {
        Some(s.split("/").dropWhile(_.isEmpty).takeWhile(_.nonEmpty))
      }
  }

  def handleWebCommand(request: RequestHeader, buildLink: BuildLink, path: File): Option[Result] = {

    if (!isDev(environment)) {
      None
    } else {
      request.path match {
        case Path("@fixture") => Some(Ok(views.html.index(fixtures.allDatabaseNames)))
        case Path("@fixture", dbName) =>
          fixtures.get(dbName).map { fixture =>
            Some(Ok(views.html.show(dbName, fixture.scripts)))
          }.getOrElse(Some(NotFound))
        case Path("@fixture", dbName, "setUp") =>
          fixtures.get(dbName).foreach(_.setUp())
          Some(Redirect(s"/@fixture/${dbName}"))
        case Path("@fixture", dbName, "tearDown") =>
          fixtures.get(dbName).foreach(_.tearDown())
          Some(Redirect(s"/@fixture/${dbName}"))
        case Path("@fixture", "assets", assets @ _*) =>
          Option(environment.classLoader.getResource(webJarAssetLocator.getFullPath(assets.mkString("/")))).map {
            resource =>
              IOUtil.using(resource.openStream()) { stream =>
                Some(Ok(IOUtil.readInputStreamToString(stream)))
              }
          }.getOrElse(Some(NotFound))
        case _ => None
      }
    }
  }

  private def isDev(environment: Environment): Boolean = environment.mode == Mode.Dev

}

@Singleton
class FixtureWebCommand @Inject() (configuration: Configuration, environment: Environment, webCommand: WebCommands, fixtures: Fixtures) {
  webCommand.addHandler(new FixtureWebCommandHandler(configuration, environment, fixtures))
}

