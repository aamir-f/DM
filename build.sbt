name := "DM"

version := "0.1"

scalaVersion := "2.11.7"
libraryDependencies ++= Seq(
  "org.mockito" % "mockito-all" % "1.10.19",
  "org.scalatest" %% "scalatest" % "3.0.1",
  "commons-net" % "commons-net" % "3.3",
  "com.typesafe" % "config" % "1.2.1",
  "org.json4s" %% "json4s-native" % "3.6.0-M2",
  "com.typesafe.akka" %% "akka-actor" % "2.4.17",
  "com.typesafe.akka" %% "akka-persistence" % "2.4.17",
  "com.typesafe.akka" %% "akka-stream" % "2.4.17",
  "com.typesafe.akka" %% "akka-testkit" % "2.4.17",
  "com.typesafe.akka" %% "akka-http-core" % "10.0.1",
  "com.typesafe.akka" % "akka-cluster-tools_2.11" % "2.4.17",
  "com.typesafe.akka" % "akka-remote_2.11" % "2.4.17",
  "org.json4s" %% "json4s-jackson" % "3.5.3",
  "joda-time" % "joda-time" % "2.9.9",
  "com.jcraft" % "jsch" % "0.1.54",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
   "ch.qos.logback" % "logback-classic" % "1.1.2",
  "org.specs2" %% "specs2-core" % "4.0.1" % Test


)