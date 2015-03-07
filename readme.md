# sbt-javafx

sbt-javafx is a plugin for [SBT](http://www.scala-sbt.org/) (Simple Build Tool) for packaging JavaFX applications.

## Quick start

Add an `.sbt` file (e.g. `plugins.sbt`) to the project's `project` directory, with the following content:  

```scala
addSbtPlugin("no.vedaadata" %% "sbt-javafx" % "0.7")
```

A minimal `.sbt` build file (e.g. `build.sbt`) could then look like this:

```scala
name := "my-javafx-application"

jfxSettings

JFX.mainClass := Some("mypackage.MyJavaFXApplication")
```

To package the application, simply run the `package-javafx` task.

More details:

* [Enabling the plugin](doc/enabling.md)
* [Customizing paths to necessary JavaFX files](doc/paths.md)
* [Running from within SBT](doc/running.md)
* [Packaging](doc/packaging.md)
* [Other settings](doc/other.md)
* [Advanced features](doc/advanced.md)
* [Examples](https://github.com/kavedaa/sbt-javafx-examples)