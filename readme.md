== Getting

For now, you'll have to clone the project from GitHub and run the SBT command `publish-local` on it. (Or `+ publish-local` for a cross build.)

Then add the following line to your project's plugin configuration:

```scala
addSbtPlugin("no.vedaadata" %% "sbt-javafx" % "0.2-SNAPSHOT")
```


== Configuring paths to necessary JavaFX files

Two files from the JavaFX SDK are needed by the plugin:

* jfxrt.jar (for compiling and running)
* ant-javafx.jar (for packaging)

The location of the these can be configured in several different ways:

=== Using JDK 7u6 or higher

The JDK from version 7u6 and higher has the JavaFX SDK included with it. Specify the path to the JDK root directory like this, e.g.:

```scala
JFX.jdkDir := Some("C:/Program Files/Java/jdk1.7.0_06")
```

For some reason though, even if it is included, jfxrt.jar is *not* added to the Java classpath. So you'll have to add it manually in SBT, however there is a little convenience setting to do that for you:

```scala
JFX.addJfxRtToClasspath := true
```

(With the longish name it might not seem so convenient, but this helps identify it as a temporary *workaround* that will hopefully no longer be needed when JavaFX becomes an official part of Java SE.) 

It is not necessary to add ant-javafx.jar to the classpath.

=== Using a standalone JavaFX SDK

If you're using JDK 6 (which does not include JavaFX SDK), or for some other reason prefer to use the stand-alone JavaFX SDK, specify the path to the root directory like this, e.g.:
 
```scala
JFX.sdkDir := Some("C:/Program Files/Oracle/JavaFX 2.2 SDK")
```

(Note that if you specify both jdkDir and sdkDir, jdkDir will be used.)

=== Manual configuration

You can also specify the full paths to jfxrt.jar and ant-javafx.jar individually, e.g.:

```scala
JFX.jfxRt := Some("C:/Program Files/Java/jdk1.7.0_07/jre/lib/jfxrt.jar")

JFX.antLib := Some("C:/Program Files/Java/jdk1.7.0_07/lib/ant-javafx.jar")
```

This will override any setting of jdkDir and/or sdkDir.

*Tip:* SBT does not limit you to use only a single .sbt build settings file for your project. Instead, it combines the settings from all .sbt files in your project's root directory. It is a good idea to keep the path configuration settings in a seperate file than the main build file and exclude this file from version control, especially when you're collaborating with others on the project or for other reasons compiling it on several different machines where the paths may not be the same. 
