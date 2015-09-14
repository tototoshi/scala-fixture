scalariformSettings

name := """scala-fixture"""

version := "0.1.0"

scalaVersion := "2.11.7"

crossScalaVersions := Seq("2.10.5", "2.11.7")

organization := "com.github.tototoshi"

scalacOptions ++= Seq("-deprecation", "-language:_")

parallelExecution in Test := false

libraryDependencies ++= Seq(
  "com.h2database" % "h2" % "[1.3,)" % "test",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "org.flywaydb" % "flyway-core" % "3.2.1" % "test"
)


publishMavenStyle := true

publishTo <<= version { (v: String) => _publishTo(v) }

def _publishTo(v: String) = {
  val nexus = "https://oss.sonatype.org/"
  if (v.trim.endsWith("SNAPSHOT")) Some("snapshots" at nexus + "content/repositories/snapshots")
  else Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

publishArtifact in Test := false

pomExtra :=
    <url>http://github.com/tototoshi/scala-fixture</url>
    <licenses>
      <license>
        <name>Apache License, Version 2.0</name>
        <url>http://www.apache.org/licenses/LICENSE-2.0.html</url>
        <distribution>repo</distribution>
      </license>
    </licenses>
    <scm>
      <url>git@github.com:tototoshi/scala-fixture</url>
      <connection>scm:git:git@github.com:tototoshi/scala-fixture.git</connection>
    </scm>
    <developers>
      <developer>
        <id>tototoshi</id>
        <name>Toshiyuki Takahashi</name>
        <url>http://tototoshi.github.io</url>
      </developer>
    </developers>
