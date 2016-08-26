name := """finance-way"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"


libraryDependencies ++= Seq(
  cache,
  ws,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test,
  "org.postgresql" % "postgresql" % "9.4.1209.jre7",
  "com.typesafe.play" %% "play-slick" % "2.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "2.0.0",
  "com.typesafe.slick" %% "slick-codegen" % "3.1.1",
  "org.webjars" % "bootstrap" % "3.3.1"
)

// https://mvnrepository.com/artifact/org.apache.poi/poi

libraryDependencies += "org.apache.poi" % "poi" % "3.14"

// https://mvnrepository.com/artifact/org.apache.poi/poi-ooxml-schemas
libraryDependencies += "org.apache.poi" % "poi-ooxml" % "3.14"








