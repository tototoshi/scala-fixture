package com.github.tototoshi.fixture.play

import javax.inject.Inject

import play.api.Configuration

import scala.collection.JavaConverters._

case class DatabaseConfiguration(driver: String, url: String, username: Option[String], password: Option[String])

case class FixtureConfiguration(
  database: DatabaseConfiguration,
  auto: Boolean,
  scriptLocation: String,
  scriptPackage: Option[String],
  scripts: Seq[String]
)

class FixtureConfigurationReader @Inject() (configuration: Configuration) {

  def getFixtureConfigurations: Map[String, FixtureConfiguration] = {
    getAllDatabaseNames.map { databaseName => (databaseName, getFixtureConfiguration(databaseName)) }.toMap
  }

  private def getFixtureConfiguration(databaseName: String): FixtureConfiguration = {
    val driver = getStringConfiguration(configuration, s"db.${databaseName}.driver")
    val url = getStringConfiguration(configuration, s"db.${databaseName}.url")
    val username = configuration.getString(s"db.${databaseName}.username")
      .orElse(configuration.getString(s"db.${databaseName}.user"))
    val password = configuration.getString(s"db.${databaseName}.password")

    val databaseConfiguration = DatabaseConfiguration(driver, url, username, password)

    // scala-fixture specific configuration
    val scriptLocation = configuration.getString(s"db.${databaseName}.fixtures.scriptLocation").getOrElse(s"db/fixtures/${databaseName}")
    val scriptPackage = configuration.getString(s"db.${databaseName}.fixtures.scriptPackage")
    val scripts = getStringSeqConfiguration(configuration, s"db.${databaseName}.fixtures.scripts")

    val auto = configuration.getBoolean(s"db.${databaseName}.fixtures.auto").getOrElse(false)

    FixtureConfiguration(databaseConfiguration, auto, scriptLocation, scriptPackage, scripts)
  }

  def getAllDatabaseNames: Seq[String] = (for {
    config <- configuration.getConfig("db").toList
    dbName <- config.subKeys
  } yield {
    dbName
  }).distinct

  private def getStringConfiguration(configuration: Configuration, key: String): String =
    configuration.getString(key).getOrElse(sys.error(s"Configuration of ${key} is missing"))

  private def getStringSeqConfiguration(configuration: Configuration, key: String): Seq[String] = {
    configuration.getStringList(key).getOrElse(sys.error(s"Configuration of ${key} is missing")).asScala
  }

}
