package com.github.tototoshi.fixture.play

import javax.inject.{ Inject, Singleton }

import com.github.tototoshi.fixture.Fixture
import play.api.inject.ApplicationLifecycle
import play.api.{ Configuration, Environment }

import scala.concurrent.{ ExecutionContext, Future }

@Singleton
class FixtureInitializer @Inject() (
    configuration: Configuration,
    environment: Environment,
    lifecycle: ApplicationLifecycle,
    executionContext: ExecutionContext,
    fixtures: Fixtures
) {

  private val configurationReader = new FixtureConfigurationReader(configuration)

  private val fixtureConfigurations = configurationReader.getFixtureConfigurations

  private def initialize(): Unit = {
    lifecycle.addStopHook(() => Future {
      onStop()
    }(executionContext))

    onStart()
  }

  private def withAllDatabasesMarkedAuto(f: Fixture => Unit): Unit = {
    val allFixturesMarkedAuto = for {
      dbName <- fixtures.allDatabaseNames
      conf <- fixtureConfigurations.get(dbName)
      if conf.auto
      fixture <- fixtures.get(dbName)
    } yield fixture

    allFixturesMarkedAuto.foreach(f)
  }

  private def onStart(): Unit = {
    withAllDatabasesMarkedAuto { fixture => fixture.setUp() }
  }

  private def onStop(): Unit = {
    withAllDatabasesMarkedAuto { fixture => fixture.tearDown() }
  }

  initialize()
}