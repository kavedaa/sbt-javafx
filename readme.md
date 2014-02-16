# sbt-javafx

sbt-javafx is a plugin for [SBT](http://www.scala-sbt.org/) (Simple Build Tool) for packaging JavaFX applications. It can be used to run and package both Scala- and Java-based applications.

## Quick start

sbt-javafx is available from Maven Central with a groupId of `no.vedaadata` and an artifactId of `sbt-javafx`.

To use it in an SBT project, add an `.sbt` file (e.g. `plugins.sbt`) to the project's `project` directory, with the following content:  

```scala
addSbtPlugin("no.vedaadata" %% "sbt-javafx" % "0.6")
```

A minimal `.sbt` build file (e.g. `build.sbt`) could then look like this:

```scala
name := "my-javafx-application"

version := "1.0"

jfxSettings

JFX.mainClass := Some("mypackage.MyJavaFXApplication")
```

To package the application, simply run the `package-javafx` task.

This is mostly what you need to know. For more details, read on below.

* [Enabling the plugin](doc/enabling.md)
* [Configuring paths to necessary JavaFX files](doc/paths.md)
* [Running from within SBT](doc/running.md)
* [Packaging](doc/packaging.md)
* [Other settings](doc/other.md)
* [Examples](doc/examples.md)