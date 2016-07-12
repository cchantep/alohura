name := "alohura"

organization := "alohura"

version := "1.0.13"

scalaVersion := "2.11.8"

resolvers += "Typesafe Snapshots" at "http://repo.typesafe.com/typesafe/snapshots/"

libraryDependencies ++= Seq(
  "specs2-core", "specs2-junit").map("org.specs2" %% _ % "3.8.3")

libraryDependencies ++= Seq(
  "com.jsuereth" %% "scala-arm" % "1.4",
  "net.databinder.dispatch" %% "dispatch-core" % "0.11.2")

publishTo := Some(Resolver.file("file",  new File(Path.userHome.absolutePath+"/.m2/repository")))
