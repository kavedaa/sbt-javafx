name := "sbt-javafx"

organization := "no.vedaadata"

version := "0.6-SNAPSHOT"

scalaVersion := "2.10.2"

sbtPlugin := true

crossBuildingSettings

CrossBuilding.crossSbtVersions := Seq("0.11.2", "0.11.3", "0.12", "0.13")

libraryDependencies += "org.apache.ant" % "ant" % "1.8.2"

publishMavenStyle := true

publishTo <<= version { (v: String) =>
  val nexus = "https://oss.sonatype.org/"
  if (v.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

pomExtra := (
  <url>https://github.com/kavedaa/sbt-javafx</url>
  <licenses>
    <license>
      <name>BSD-style</name>
      <url>http://www.opensource.org/licenses/bsd-license.php</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>git@github.com:kavedaa/sbt-javafx</url>
    <connection>scm:git:git@github.com:kavedaa/sbt-javafx</connection>
  </scm>
  <developers>
    <developer>
      <id>kavedaa</id>
      <name>Knut Arne Vedaa</name>
      <url>http://vedaadata.com</url>
    </developer>
  </developers>
)