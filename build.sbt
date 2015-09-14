scalariformSettings

name := """scala-fixture"""

version := "0.1.0"

scalaVersion := "2.11.7"

organization := "com.github.tototoshi"

scalacOptions ++= Seq("-deprecation", "-language:_")

parallelExecution in Test := false

libraryDependencies ++= Seq(
  "com.h2database" % "h2" % "[1.3,)" % "test",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "org.flywaydb" % "flyway-core" % "3.2.1" % "test"
)
