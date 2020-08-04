name := "alohura"

organization := "alohura"

version := "1.0.16"

scalaVersion := "2.12.8"

crossScalaVersions in ThisBuild := Seq(
  "2.11.12", scalaVersion.value)

resolvers += "Typesafe Snapshots" at "http://repo.typesafe.com/typesafe/snapshots/"

scalacOptions ++= Seq(
  "-encoding", "UTF-8",
  "-target:jvm-1.8",
  "-unchecked",
  "-deprecation",
  "-feature",
  "-Xfatal-warnings",
  "-Xlint",
  "-Ywarn-numeric-widen",
  "-Ywarn-infer-any",
  "-Ywarn-dead-code",
  "-Ywarn-unused",
  "-Ywarn-unused-import",
  "-Ywarn-value-discard",
  "-g:vars"
)

scalacOptions in (Compile, console) ~= {
  _.filterNot { opt => opt.startsWith("-X") || opt.startsWith("-Y") }
}

scalacOptions in (Test, console) ~= {
  _.filterNot { opt => opt.startsWith("-X") || opt.startsWith("-Y") }
}

libraryDependencies ++= Seq(
  "specs2-core", "specs2-junit").map("org.specs2" %% _ % "4.10.2")

libraryDependencies ++= Seq(
  "com.jsuereth" %% "scala-arm" % "2.0",
  "net.databinder.dispatch" %% "dispatch-core" % "0.13.4")

publishTo := Some(Resolver.file("file",  new File(Path.userHome.absolutePath+"/.m2/repository")))

// Scapegoat
scapegoatVersion in ThisBuild := "1.3.3"

scapegoatDisabledInspections := Seq("FinalModifierOnCaseClass")

scapegoatReports in ThisBuild := Seq("xml")