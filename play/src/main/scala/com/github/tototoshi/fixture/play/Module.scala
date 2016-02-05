package com.github.tototoshi.fixture.play

import play.api.inject.{ Binding, Module }
import play.api.{ Configuration, Environment }

class FixtureModule extends Module {
  override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] = {
    Seq(
      bind[Fixtures].toSelf,
      bind[FixtureInitializer].toSelf.eagerly(),
      bind[FixtureWebCommand].toSelf.eagerly
    )
  }
}
