name := "alohura"

organization := "alohura"

version := "1.0.15"

scalaVersion := "2.11.11"

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
  "-g:vars",
  "-Yconst-opt",
  "-Yclosure-elim",
  "-Ydead-code",
  "-Yopt:_"
)

scalacOptions in (Compile, console) ~= {
  _.filterNot { opt => opt.startsWith("-X") || opt.startsWith("-Y") }
}

scalacOptions in (Test, console) ~= {
  _.filterNot { opt => opt.startsWith("-X") || opt.startsWith("-Y") }
}

libraryDependencies ++= Seq(
  "specs2-core", "specs2-junit").map("org.specs2" %% _ % "4.0.0")

libraryDependencies ++= Seq(
  "com.jsuereth" %% "scala-arm" % "2.0",
  "net.databinder.dispatch" %% "dispatch-core" % "0.12.0")

publishTo := Some(Resolver.file("file",  new File(Path.userHome.absolutePath+"/.m2/repository")))

// Scapegoat
scapegoatVersion := "1.3.2"

scapegoatDisabledInspections := Seq("FinalModifierOnCaseClass")

scapegoatReports := Seq("xml")
