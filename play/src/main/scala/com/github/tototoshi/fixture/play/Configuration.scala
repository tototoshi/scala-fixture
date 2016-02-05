package com.github.tototoshi.fixture.play

import play.api.Configuration
import scala.collection.JavaConverters._

case class DatabaseConfiguration(driver: String, url: String, username: String, password: String)

case class FixtureConfiguration(
  database: DatabaseConfiguration,
  auto: Boolean,
  scriptLocation: String,
  scriptPackage: Option[String],
  scripts: Seq[String]
)

class ConfigurationReader(configuration: Configuration) {

  def getFixtureConfigurations: Map[String, FixtureConfiguration] = {
    getAllDatabaseNames.map { databaseName => (databaseName, getFixtureConfiguration(databaseName)) }.toMap
  }

  private def getFixtureConfiguration(databaseName: String): FixtureConfiguration = {
    val driver = getStringConfiguration(configuration, s"db.${databaseName}.driver")
    val url = getStringConfiguration(configuration, s"db.${databaseName}.url")
    val username = getStringConfiguration(configuration, s"db.${databaseName}.username")
    val password = getStringConfiguration(configuration, s"db.${databaseName}.password")

    val databaseConfiguration = DatabaseConfiguration(driver, url, username, password)

    // scala-fixture specific configuration
    val scriptLocation = configuration.getString(s"db.${databaseName}.fixture.scriptLocation").getOrElse(s"db/fixtures/${databaseName}")
    val scriptPackage = configuration.getString(s"db.${databaseName}.fixture.scriptPackage")
    val scripts = getStringSeqConfiguration(configuration, s"db.${databaseName}.fixture.scripts")

    val auto = configuration.getBoolean(s"db.${databaseName}.fixture.auto").getOrElse(false)

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
