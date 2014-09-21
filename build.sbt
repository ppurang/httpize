name := "httpize"

version := "0.1.0"

organization := "org.purang.net"

scalaVersion := "2.10.4"

val http4sVersion = "0.3.0"

libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-core" % http4sVersion,
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "org.http4s" %% "http4s-blazeserver" % http4sVersion,
  "io.argonaut" %% "argonaut" % "6.1-M4" changing(),
  "org.scalacheck" %% "scalacheck" % "1.11.4" % "test",
  "org.scalatest" %% "scalatest" % "2.1.6" % "test"
  )

resolvers ++= Seq(
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
  "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases"
)

scalacOptions ++= Seq("-encoding",
  "UTF-8",
  "-deprecation", "-feature", "-unchecked", "-language:_")
  //"-optimize",
  //"-Yinline", "-Yinline-warnings" , "-Ywarn-all")
//ran into this: https://issues.scala-lang.org/browse/SI-3882

Revolver.settings

seq(com.typesafe.sbt.SbtStartScript.startScriptForClassesSettings: _*)

cancelable := true

fork := true

logBuffered := false

//seq(bintrayPublishSettings:_*)

licenses += ("BSD", url("http://www.tldrlegal.com/license/bsd-3-clause-license-%28revised%29"))
