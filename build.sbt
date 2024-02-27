name := "alohura"

organization := "alohura"

version := "1.0.16"

scalaVersion := "2.12.18"

crossScalaVersions := Seq(
  "2.11.12", scalaVersion.value, "2.13.13", "3.0.0-RC1")

resolvers ++= Seq(
  Resolver.typesafeRepo("snapshots"),
  "Tatami Snapshots" at "https://raw.github.com/cchantep/tatami/master/snapshots")

scalacOptions ++= {
  if (scalaBinaryVersion.value == "2.13") {
    Seq(
      "-explaintypes",
      "-Werror",
      "-deprecation",
      "-Wnumeric-widen",
      "-Wdead-code",
      "-Wvalue-discard",
      "-Wextra-implicit",
      "-Wmacros:after",
      "-Wunused")
  } else if (scalaBinaryVersion.value startsWith "2.") {
    Seq(
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
  } else Seq.empty
}

scalacOptions in (Compile, console) ~= {
  _.filterNot { opt => opt.startsWith("-X") || opt.startsWith("-Y") }
}

scalacOptions in (Test, console) ~= {
  _.filterNot { opt => opt.startsWith("-X") || opt.startsWith("-Y") }
}

libraryDependencies ++= Seq(
  "specs2-core", "specs2-junit").map("org.specs2" %% _ % "4.10.6").
  map(_.withDottyCompat(scalaVersion.value))

val dispatchVer = Def.setting[String] {
  if (scalaBinaryVersion.value == "2.11") {
    "1.0.3"
  } else {
    "1.2.0"
  }
}

libraryDependencies ++= Seq(
  "com.jsuereth" %% "scala-arm" % "2.1-SNAPSHOT",
  "org.dispatchhttp" %% "dispatch-core" % dispatchVer.value).
  map(_.withDottyCompat(scalaVersion.value))

publishTo := Some(Resolver.file("file",  new File(Path.userHome.absolutePath+"/.m2/repository")))
