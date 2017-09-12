def _publishTo(v: String) = {
  val nexus = "https://oss.sonatype.org/"
  if (v.trim.endsWith("SNAPSHOT")) Some("snapshots" at nexus + "content/repositories/snapshots")
  else Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

lazy val nonPublishSettings = Seq(
  publishArtifact := false,
  publish := {},
  publishLocal := {}
)

lazy val publishSettings = Seq(
  publishMavenStyle := true,
  publishTo <<= version { (v: String) => _publishTo(v) },
  publishArtifact in Test := false,
  pomExtra :=
    <url>https://github.com/tototoshi/scala-fixture</url>
    <licenses>
      <license>
        <name>Apache License, Version 2.0</name>
        <url>https://www.apache.org/licenses/LICENSE-2.0.html</url>
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
        <url>https://tototoshi.github.io</url>
      </developer>
    </developers>
)


lazy val commonSettings = Seq(
  version := "0.2.0",
  scalaVersion := "2.11.8",
  crossScalaVersions := Seq("2.11.8"),
  organization := "com.github.tototoshi",
  scalacOptions ++= Seq("-deprecation", "-language:_"),
  parallelExecution in Test := false
)

lazy val core = Project(
  id = "core",
  base = file("core")
).settings(
  name := "scala-fixture",
  libraryDependencies ++= Seq(
    "com.h2database" % "h2" % "1.4.+" % "test",
    "org.scalatest" %% "scalatest" % "2.2.5" % "test",
    "org.flywaydb" % "flyway-core" % "4.0" % "test"
  )
).settings(commonSettings ++ publishSettings)

lazy val play = Project(
  id = "play",
  base = file("play")
).enablePlugins(SbtTwirl).settings(
  name := "scala-fixture-play",
  libraryDependencies ++= Seq(
    "com.typesafe.play" %% "play" % "2.5.4" % "provided",
    "org.webjars" % "webjars-locator" % "0.30",
    "org.webjars" % "bootstrap" % "3.3.6"
  )
).settings(commonSettings ++ publishSettings).dependsOn(core)

lazy val playapp = Project(
  id = "playapp",
  base = file("playapp")
).enablePlugins(PlayScala).settings(
  name := "scala-fixture-playapp",
  routesGenerator := InjectedRoutesGenerator,
  libraryDependencies ++= Seq(
    "org.flywaydb" %% "flyway-play" % "3.0.0",
    jdbc,
    "org.scalikejdbc" %% "scalikejdbc" % "2.3.5",
    "org.scalikejdbc" %% "scalikejdbc-config" % "2.3.5",
    "org.webjars" %% "webjars-play" % "2.5.0",
    "org.webjars" % "bootstrap" % "3.3.6",
    "org.scalatest" %% "scalatest" % "2.2.5" % "test"
  )
).settings(commonSettings ++ nonPublishSettings).dependsOn(play)

lazy val root = Project(
  id = "scala-fixture",
  base = file(".")
).settings(
  name := "root"
).settings(commonSettings ++ nonPublishSettings)
 .aggregate(core, play)
