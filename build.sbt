name := "alohura"

organization := "fr.applicius"

version := "1.0.11"

scalaVersion := "2.10.4"

resolvers += "Typesafe Snapshots" at "http://repo.typesafe.com/typesafe/snapshots/"

libraryDependencies ++= Seq(
  "org.specs2" %% "specs2" % "2.3.11",
  "com.jsuereth" %% "scala-arm" % "1.3",
  "net.databinder.dispatch" %% "dispatch-core" % "0.10.0")

publishTo := Some(Resolver.file("file",  new File(Path.userHome.absolutePath+"/.m2/repository")))
