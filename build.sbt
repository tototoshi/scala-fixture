lazy val nonPublishSettings = Seq(
  publishArtifact := false,
  publish := {},
  publishLocal := {}
)

lazy val publishSettings = Seq(
  publishMavenStyle := true,
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
  version := "0.4.1",
  scalaVersion := "2.13.5",
  crossScalaVersions := Seq("2.13.5", "2.12.13", "2.11.12"),
  organization := "com.github.tototoshi",
  scalacOptions ++= Seq("-deprecation", "-language:_"),
  parallelExecution in Test := false,
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
    "com.h2database" % "h2" % "1.4.+" % "test",
    "org.scalatest" %% "scalatest" % "3.0.9" % "test",
    "org.flywaydb" % "flyway-core" % "7.5.4" % "test"
  )
).settings(commonSettings ++ publishSettings)

lazy val play = Project(
  id = "play",
  base = file("play")
).enablePlugins(SbtTwirl).settings(
  name := "scala-fixture-play",
  libraryDependencies ++= Seq(
    "com.typesafe.play" %% "play" % "2.7.9" % "provided"
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
    "org.scalatest" %% "scalatest" % "3.0.9" % "test"
  )
).settings(commonSettings ++ nonPublishSettings).dependsOn(play)

lazy val root = Project(
  id = "scala-fixture",
  base = file(".")
).settings(
  name := "root"
).settings(commonSettings ++ nonPublishSettings)
 .aggregate(core, play)
