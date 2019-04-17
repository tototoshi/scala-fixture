resolvers += "Sonatype OSS Releases" at "https://oss.sonatype.org/service/local/staging/deploy/maven2"

addSbtPlugin("org.scalariform" % "sbt-scalariform" % "1.8.0")

addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "2.5")

addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.1.2")

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.6.21")

addSbtPlugin("com.typesafe.sbt" % "sbt-twirl" % "1.3.4")
