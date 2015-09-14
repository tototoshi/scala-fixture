package com.github.tototoshi.fixture

import scala.util.control.NonFatal

private[fixture] class JavaFixtureScriptScanner(classLoader: ClassLoader, scriptPackage: String) extends Scanner {

  override def scan(scriptName: String): Option[FixtureScript] = {
    try {
      val className = if (scriptPackage == "") scriptName else scriptPackage + "." + scriptName
      Some(Class.forName(className, true, classLoader).newInstance().asInstanceOf[FixtureScript])
    } catch {
      case NonFatal(e) => None
    }
  }

}
