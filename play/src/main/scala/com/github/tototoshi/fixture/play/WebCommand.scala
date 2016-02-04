package com.github.tototoshi.fixture.play

import java.io.{ByteArrayOutputStream, File, InputStream}
import java.util.regex.Pattern

import com.github.tototoshi.fixture.Fixture
import org.webjars.WebJarAssetLocator
import play.api.mvc.Results._
import play.api.mvc.{RequestHeader, Result}
import play.api.{Configuration, Environment, Mode}
import play.core.{BuildLink, HandleWebCommandSupport}

class FixtureWebCommandHandler(configuration: Configuration, environment: Environment) extends HandleWebCommandSupport {

  private val configurationReader = new ConfigurationReader(configuration)

  private val webJarAssetLocator = new WebJarAssetLocator(WebJarAssetLocator.getFullPathIndex(Pattern.compile(".*"), environment.classLoader))

  private val fixtureConfigurations = configurationReader.getFixtureConfigurations

  private object Path {
    def unapplySeq(s: String): Option[Seq[String]] =
      if (s.trim.isEmpty) {
        None
      } else {
        Some(s.split("/").dropWhile(_.isEmpty).takeWhile(_.nonEmpty))
      }
  }

  def handleWebCommand(request: RequestHeader, buildLink: BuildLink, path: File): Option[Result] = {

    def readInputStreamToString(stream: InputStream): String = {
      val buffer = new Array[Byte](8192)
      var len = stream.read(buffer)
      using(new ByteArrayOutputStream()) { out =>
        while (len != -1) {
          out.write(buffer, 0, len)
          len = stream.read(buffer)
        }
        new String(out.toByteArray)
      }
    }

    if (!isDev(environment)) {
      None
    } else {
      request.path match {
        case Path("@fixture") => Some(Ok(views.html.index(configurationReader.getAllDatabaseNames)))
        case Path("@fixture", dbName) =>
          fixtureConfigurations.get(dbName).map { configuration =>
            Some(Ok(views.html.show(dbName, configuration.scripts)))
          }.getOrElse(Some(NotFound))
        case Path("@fixture", dbName, "setUp") =>
          createFixture(configuration, dbName).setUp()
          Some(Redirect(s"/@fixture/${dbName}"))
        case Path("@fixture", dbName, "tearDown") =>
          createFixture(configuration, dbName).tearDown()
          Some(Redirect(s"/@fixture/${dbName}"))
        case Path("@fixture", "assets", assets@_*) =>
          Option(environment.classLoader.getResource(webJarAssetLocator.getFullPath(assets.mkString("/")))).map {
            resource =>
              using(resource.openStream()) { stream =>
                Some(Ok(readInputStreamToString(stream)))
              }
          }.getOrElse(Some(NotFound))
        case _ => None
      }
    }
  }

  private def using[A <: {def close() : Unit}, B](resource: A)(f: A => B): B = {
    try {
      f(resource)
    } finally {
      resource.close()
    }
  }

  private def createFixture(configuration: Configuration, dbName: String): Fixture = {
    fixtureConfigurations.get(dbName) match {
      case Some(config) =>
        val dbConfig = config.database
        val fixture = Fixture(dbConfig.driver, dbConfig.url, dbConfig.username, dbConfig.password).scriptLocation(config.scriptLocation)

        config.scriptPackage match {
          case Some(p) => fixture.scriptPackage(p).scripts(config.scripts)
          case None => fixture.scripts(config.scripts)
        }
      case None => sys.error( s"""Configuration of Database "${dbName}" is missing""")
    }
  }

  private def isDev(environment: Environment): Boolean = environment.mode == Mode.Dev

}
