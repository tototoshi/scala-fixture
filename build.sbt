lazy val nonPublishSettings = Seq(
  publishArtifact := false,
  publish := {},
  publishLocal := {}
)

lazy val publishSettings = Seq(
  publishMavenStyle := true,
  Test / publishArtifact := false,
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
  version := "0.5.0-SNAPSHOT",
  scalaVersion := "2.13.9",
  crossScalaVersions := Seq("2.13.9", "2.12.17"),
  organization := "com.github.tototoshi",
  scalacOptions ++= Seq("-deprecation", "-language:_"),
  Test / parallelExecution := false,
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (version.value.trim.endsWith("SNAPSHOT")) Some("snapshots" at nexus + "content/repositories/snapshots")
    else Some("releases" at nexus + "service/local/staging/deploy/maven2")
  }
)

lazy val core = Project(
  id = "core",
  base = file("core")
).settings(
  name := "scala-fixture",
  libraryDependencies ++= Seq(
    "com.h2database" % "h2" % "2.2.220" % "test",
    "org.scalatest" %% "scalatest" % "3.2.13" % "test",
    "org.flywaydb" % "flyway-core" % "9.3.1" % "test"
  )
).settings(commonSettings ++ publishSettings)

lazy val play = Project(
  id = "play",
  base = file("play")
).enablePlugins(SbtTwirl).settings(
  name := "scala-fixture-play",
  libraryDependencies ++= Seq(
    "com.typesafe.play" %% "play" % _root_.play.core.PlayVersion.current % "provided"
  )
).settings(commonSettings ++ publishSettings).dependsOn(core)

lazy val playapp = Project(
  id = "playapp",
  base = file("playapp")
).enablePlugins(PlayScala).settings(
  name := "scala-fixture-playapp",
  routesGenerator := InjectedRoutesGenerator,
  libraryDependencies ++= Seq(
    guice,
    "org.flywaydb" %% "flyway-play" % "5.3.2",
    jdbc,
    "org.scalikejdbc" %% "scalikejdbc" % "3.3.4",
    "org.scalikejdbc" %% "scalikejdbc-config" % "3.3.4",
    "com.h2database" % "h2" % "1.4.+",
    "org.scalatest" %% "scalatest" % "3.2.13" % "test"
  )
).settings(commonSettings ++ nonPublishSettings).dependsOn(play)

lazy val root = Project(
  id = "scala-fixture",
  base = file(".")
).settings(
  name := "root"
).settings(commonSettings ++ nonPublishSettings)
 .aggregate(core, play)
