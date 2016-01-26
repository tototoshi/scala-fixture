package com.github.tototoshi.fixture.play

import java.io.File
import javax.inject.{Provider, Inject, Singleton}

import play.api.{Configuration, Environment}
import play.api.inject.{Binding, Module}
import play.api.mvc.{Result, RequestHeader, Results}
import play.core.{BuildLink, WebCommands, HandleWebCommandSupport}


class FixtureWebCommandHandler extends HandleWebCommandSupport {
  def handleWebCommand(request: RequestHeader, buildLink: BuildLink, path: File): Option[Result] = {
    request.path match {
      case """/@fixture""" => Some(Results.Ok("Hi"))
      case _ => None
    }
  }
}

class FixtureWebCommand @Inject() (webCommand: WebCommands) {
  webCommand.addHandler(new FixtureWebCommandHandler())
}

@Singleton
class FixtureWebCommandProvider @Inject() (webCommands: WebCommands) extends Provider[FixtureWebCommand] {
  override def get(): FixtureWebCommand = new FixtureWebCommand(webCommands)
}

class FixtureWebCommandModule extends Module {
  override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] = {
    Seq(bind[FixtureWebCommand].toProvider[FixtureWebCommandProvider].eagerly)
  }
}

