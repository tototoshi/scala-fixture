package com.github.tototoshi.fixture

import java.nio.file.{ Files, Paths }

private[fixture] class SQLFixtureScriptScanner(classLoader: ClassLoader, resourceLocation: String) extends Scanner {

  private[fixture] def createResourcePath(resourcePrefix: String, scriptName: String): String =
    if (resourcePrefix == "") {
      StringUtil.trimSlashes(scriptName)
    } else {
      StringUtil.trimSlashes(resourcePrefix) + "/" + StringUtil.trimSlashes(scriptName)
    }

  def scan(scriptName: String): Option[SQLFixtureScript] = {
    val resource = createResourcePath(resourceLocation, scriptName)
    val path = Option(classLoader.getResource(resource)).map(resource => Paths.get(resource.toURI))
    path.map(p => SQLFixtureScript(resource, new String(Files.readAllBytes(p), "UTF-8")))
  }

}
