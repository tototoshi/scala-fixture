package com.github.tototoshi.fixture.play

import javax.inject.Inject

import play.api.Configuration

import scala.collection.JavaConverters._

case class DatabaseConfiguration(driver: String, url: String, username: String, password: String)

case class FixtureConfiguration(
  database: DatabaseConfiguration,
  auto: Boolean,
  scriptLocation: String,
  scriptPackage: Option[String],
  scripts: Seq[String])

class FixtureConfigurationReader @Inject() (configuration: Configuration) {

  def getFixtureConfigurations: Map[String, FixtureConfiguration] = {
    getAllDatabaseNames.map { databaseName => (databaseName, getFixtureConfiguration(databaseName)) }.toMap
  }

  private def getFixtureConfiguration(databaseName: String): FixtureConfiguration = {
    val driver = getStringConfiguration(configuration, s"db.${databaseName}.driver")
    val url = getStringConfiguration(configuration, s"db.${databaseName}.url")
    val username = configuration.getOptional[String](s"db.${databaseName}.username").orNull
    val password = configuration.getOptional[String](s"db.${databaseName}.password").orNull

    val databaseConfiguration = DatabaseConfiguration(driver, url, username, password)

    // scala-fixture specific configuration
    val scriptLocation = configuration.getOptional[String](s"db.${databaseName}.fixtures.scriptLocation").getOrElse(s"db/fixtures/${databaseName}")
    val scriptPackage = configuration.getOptional[String](s"db.${databaseName}.fixtures.scriptPackage")
    val scripts = getStringSeqConfiguration(configuration, s"db.${databaseName}.fixtures.scripts")

    val auto = configuration.getOptional[Boolean](s"db.${databaseName}.fixtures.auto").getOrElse(false)

    FixtureConfiguration(databaseConfiguration, auto, scriptLocation, scriptPackage, scripts)
  }

  def getAllDatabaseNames: Seq[String] = (for {
    config <- configuration.getOptional[Configuration]("db").toList
    dbName <- config.subKeys
  } yield {
    dbName
  }).distinct

  private def getStringConfiguration(configuration: Configuration, key: String): String =
    configuration.getOptional[String](key).getOrElse(sys.error(s"Configuration of ${key} is missing"))

  private def getStringSeqConfiguration(configuration: Configuration, key: String): Seq[String] = {
    configuration.getOptional[Seq[String]](key).getOrElse(Seq.empty)
  }

}
