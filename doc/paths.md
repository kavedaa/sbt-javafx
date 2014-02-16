## Customizing paths to necessary JavaFX files

Two files from the JavaFX SDK are needed by the plugin:

* `jfxrt.jar` (for compiling and running)
* `ant-javafx.jar` (for packaging)

By default these will be found automatically by the plugin. 

However it is possible to customize their location in different ways:

### Using JDK 7u6 or higher

The JDK from version 7u6 and higher has the JavaFX SDK included with it. Specify the path to the JDK root directory like this, e.g.:

```scala
JFX.devKit := JFX.jdk("C:/Program Files/Java/jdk1.7.0_06")
```

For some reason though, even if it is included, jfxrt.jar is *not* added to the Java classpath. So you'll have to add it manually in SBT, however there is a little convenience setting to do that for you:

```scala
JFX.addJfxrtToClasspath := true
```

(With the longish name it might not seem so convenient, but this helps identify it as a *workaround* needed for older (pre-8) Java versions.) 

It is not necessary to add ant-javafx.jar to the classpath.

### Using JDK 8 or higher

From JDK 8 and upwards, you don't need to add `jfxrt.jar` to the classpath (but you still need to specify `JFX.devKit` for the plugin to be able to find the packaging tool). 

### Using a standalone JavaFX SDK

If you're using JDK 6 (which does not include JavaFX SDK), or for some other reason prefer to use the stand-alone JavaFX SDK, specify the path to the root directory like this, e.g.:
 
```scala
JFX.devKit := JFX.sdk("C:/Program Files/Oracle/JavaFX 2.2 SDK")
```

### Manual configuration

You can also specify the full paths to jfxrt.jar and ant-javafx.jar individually, e.g.:

```scala
JFX.jfxrt := Some("C:/Program Files/Java/jdk1.7.0_07/jre/lib/jfxrt.jar")
```
```scala
JFX.antLib := Some("C:/Program Files/Java/jdk1.7.0_07/lib/ant-javafx.jar")
```

(These will take precedence over corresponding paths possibly calculated from specified JDK or SDK directory.)

*Tip:* SBT does not limit you to use only a single .sbt build settings file for your project. Instead, it combines the settings from all .sbt files in your project's root directory. It is a good idea to keep the path configuration settings in a seperate file than the main build file and exclude this file from version control, especially when you're collaborating with others on the project or for other reasons compiling it on several different machines where the paths may not be the same.  (Note that SBT seems to load the `.sbt` files in alphabetical order, which sometimes matters...)

