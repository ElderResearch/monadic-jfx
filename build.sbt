name := "monadic-jfx"

version := "0.1.2-SNAPSHOT"

organization := "com.elderresearch"

description := "Enrichment classes over JavaFX bindings to provide monadic operations in Scala."

licenses := Seq("Apache License, Version 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0"))

startYear := Some(2015)

homepage := Some(url("http://github.com/ElderResearch/monadic-jfx"))

scalaVersion := "2.11.7"

javaVersionPrefix in javaVersionCheck := Some("1.8")

val catsVersion = "0.2.0"

libraryDependencies ++= Seq(
  "org.spire-math" %% "cats-core" % catsVersion,
  "org.spire-math" %% "cats-laws" % catsVersion % "test",
  "org.typelevel" %% "discipline" % "0.4" % "test",
  "org.scalatest" %% "scalatest" % "3.0.0-M7" % "test"
)

// Force Java8 compliance at startup.
onLoad in Global := {
  val previous = (onLoad in Global).value
  val checker = (s: State) => {
    Project.runTask(javaVersionCheck, s).map(_._1).getOrElse(s)
  }
  checker compose previous
}

// Testing
testOptions in Test ++= {
  if (System.getenv("TRAVIS") != null) {
    Seq(Tests.Filter(s ⇒ !s.endsWith("ExampleTest")))
  }
  else {
    Seq.empty
  }
}

fork in Test := true

// Publishing

publishMavenStyle := true

publishArtifact in Test := false

pomExtra in Global := {
  <scm>
    <url>http://github.com/ElderResearch/monadic-jfx</url>
    <connection>scm:git:https://github.com/ElderResearch/monadic-jfx.git</connection>
    <developerConnection>scm:git:git@github.com:/ElderResearch/monadic-jfx.git</developerConnection>
  </scm>
    <developers>
      <developer>
        <id>metasim</id>
        <name>Simeon H.K. Fitch</name>
        <url>https://github.com/metasim</url>
      </developer>
    </developers>
}
