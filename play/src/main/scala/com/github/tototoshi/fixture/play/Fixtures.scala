package com.github.tototoshi.fixture.play

import javax.inject.Inject

import com.github.tototoshi.fixture.Fixture
import play.api.{ Environment, Configuration }
import play.api.inject.ApplicationLifecycle

import scala.concurrent.{ ExecutionContext, Future }

class Fixtures @Inject() (
    configuration: Configuration,
    environment: Environment,
    lifecycle: ApplicationLifecycle,
    executionContext: ExecutionContext
) {

  private val configurationReader = new ConfigurationReader(configuration)

  private val fixtureConfigurations = configurationReader.getFixtureConfigurations

  def allDatabaseNames: Seq[String] = configurationReader.getAllDatabaseNames

  def get(dbName: String): Option[Fixture] = {
    fixtureConfigurations.get(dbName) map { config =>
      val dbConfig = config.database
      val fixture = Fixture(dbConfig.driver, dbConfig.url, dbConfig.username, dbConfig.password).scriptLocation(config.scriptLocation)
      config.scriptPackage match {
        case Some(p) => fixture.scriptPackage(p).scripts(config.scripts)
        case None => fixture.scripts(config.scripts)
      }
    }
  }

  private def initialize(): Unit = {
    lifecycle.addStopHook(() => Future {
      onStop()
    }(executionContext))

    onStart()
  }

  private def withAllDatabasesMarkedAuto(f: Fixture => Unit): Unit = {
    val allFixturesMarkedAuto = for {
      dbName <- allDatabaseNames
      conf <- fixtureConfigurations.get(dbName)
      if conf.auto
      fixture <- get(dbName)
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

