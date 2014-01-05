## Packaging

A JavaFX application must have a main class that extends from `javafx.application.Application`, e.g.:

```scala
class MyJavaFXApplication extends Application {

	//	application here
}
```

The name of this class must be configured like this:

```scala
JFX.mainClass := Some("mypackage.MyJavaFXApplication")
```

Execute the `package-javafx` task to package the application.

The packaged application will reside inside `target/<scala-version>/<artifact-name>/`, e.g. `target/scala_2.9.2/my-javafx-application_2.9.2-1.0/`. (It is possible to customize the name of the directory.)

The application will be identical to one packaged with JavaFX's Ant tools, e.g. by using the Netbeans IDE. It will contains, at least, a `.jar` fil, a `.jnlp` file, an `.html` file, as well as a `lib/` directory with all library jars added via any of SBT's library management methods, including the standard Scala library.

### Native bundles

For creating so-called "native bundles", that is, self-contained applications which co-bundles the JRE, use this setting:

```scala
JFX.nativeBundles := bundleType	// where bundleType is a String
```
A typical value for `bundleType` is one of:

* `all` - create all native bundles available on build platform (e.g. `msi` and `exe` on Windows, `dmg` on MacOS X, etc.)
* `deb` - Debian installer file (Linux only)
* `dmg` - MacOS X disk image (MacOS X only)
* `exe` - Windows stand-alone installer (Windows only)
* `image` - `.jar`-only distribution
* `msi` - Windows "installer database" file (Windows only)
* `none` - Don't make native bundle (default)
* `rpm` - Redhat Package Manager file (Linux only)


See the [JavaFX packaging documentation](http://docs.oracle.com/javafx/2/deployment/self-contained-packaging.htm) for possible values and further information.

#### Drop-in Resources

As described in the [Native Packaging Cookbook](https://blogs.oracle.com/talkingjavadeployment/entry/native_packaging_cookbook_using_drop), the native installers generated for each platform may be customized with files copied from the default installer templates (as reported when `verbose="true"` is passed to the `ant deploy` task), and placed in the classpath for the `ClassLoader` associated with `ant-javafx.jar`. This classpath should contain a folder called `package`, where the `deploy` task looks for files with project- and platform-specific files. See the Oracle for specifics, but the base structure is `package/{macosx,windows,linux}/[drop-in-resources]`.

The `JFX.pkgResourcesDir` setting is provided for adding a path to this classpath. For example, if a custom `Info.plist` file for MacOS X is defined in `src/main/resourcespackage/macosx/Info.plist`, the following setting would make it visible to the `deploy` task:

```scala
JFX.pkgResourcesDir := Some(baseDirectory.value + "/src/main/resources")
```

The Oracle-provided `ant-javafx.jar` is not very flexible with regard to paths to drop-in resources, so any encountered problems are likely to be associated with mis-named or mis-located files. To debug the packaging process, set `JFX.verbose := true` in your `build.sbt` file, run `sbt deploy` at least once, and then run `ant` against the generated `target/scala-x.yz/build.xml` file (i.e. `build.xml` in `crossTarget`). Running `ant` with the `deploy` task in verbose mode directly simplifies the debugging process when your drop-in resources aren't being picked up by `ant-javafx.jar`.

#### Using the correct Java version

Self-contained applications must be packaged using the JDK version of the JRE and not the stand-alone JRE. (If you have installed the JDK you will probably have both.) If you use the JRE version, you will get an error message saying "jvm.dll is not found" (on Windows, probably similar on other platforms).

This means that SBT must be started with the JDK version of the JRE. This can be assured by setting JAVA_HOME to the correct path, either globally or within SBT's `sbt.bat` startup file.

### Java-only applications

It is very much possible to use the plugin to package applications written in Java. If your application uses no Scala code at all, you might want to use the `javaOnly` setting:

```scala
JFX.javaOnly := true
```

This is a convenience setting that excludes the standard Scala library from being packaged with the application, and makes the output path a bit simpler, so that it becomes e.g. `target/my-javafx-application-1.0/`.
