package com.github.tototoshi.fixture.play

import javax.inject.{Inject, Provider, Singleton}

import play.api.inject.{Binding, Module}
import play.api.{Configuration, Environment}
import play.core.WebCommands


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
