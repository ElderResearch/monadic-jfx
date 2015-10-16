name := "monadic-jfx"

version := "0.1.0-SNAPSHOT"

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
  "org.scalatest" %% "scalatest" % "3.0.0-M7" % "test",
  "org.testfx" % "testfx-core" % "4.0.+" % "test"
)

// TODO: Get this to happen at initialization.
compile in Compile := { javaVersionCheck.value; (compile in Compile).value }

// Testing

testOptions in Test ++= {
  if (System.getenv("TRAVIS") != null) {
    Seq(Tests.Filter(s ⇒ !s.endsWith("ExampleTest")))
  }
  else Seq.empty
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
