name := "sbt-javafx-modified"

organization := "no.vedaadata"

version := "0.6.2"

scalaVersion := "2.10.2"

sbtPlugin := true

crossBuildingSettings

CrossBuilding.crossSbtVersions := Seq("0.11.2", "0.11.3", "0.12", "0.13")

libraryDependencies += "org.apache.ant" % "ant" % "1.8.2"

publishMavenStyle := true

publishTo := {
  val artifactory = "http://bill.part.net:8081/artifactory/"
  if (version.value.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at artifactory + "libs-snapshot-local-nonunique")
  else
    Some("releases"  at artifactory + "libs-release-local")
}

credentials += Credentials(Path.userHome / ".sbt" / "credentials")

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
