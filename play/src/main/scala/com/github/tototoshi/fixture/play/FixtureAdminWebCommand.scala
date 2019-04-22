package com.github.tototoshi.fixture.play

import java.io.File

import javax.inject.{ Inject, Singleton }
import play.api.mvc.Results._
import play.api.mvc.{ RequestHeader, Result }
import play.api.{ Environment, Mode }
import play.core.{ BuildLink, HandleWebCommandSupport, WebCommands }

class FixtureWebCommandHandler(environment: Environment, fixtures: Fixtures) extends HandleWebCommandSupport {

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
        case _ => None
      }
    }
  }

  private def isDev(environment: Environment): Boolean = environment.mode == Mode.Dev

}

@Singleton
class FixtureAdminWebCommand @Inject() (environment: Environment, webCommand: WebCommands, fixtures: Fixtures) {
  webCommand.addHandler(new FixtureWebCommandHandler(environment, fixtures))
}

