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

#### Drop-in Packaging Resources

As described in the article [Native Packaging Cookbook](https://blogs.oracle.com/talkingjavadeployment/entry/native_packaging_cookbook_using_drop), the native installers generated for each platform may be customized with modified versions of files from the installer templates. The Oracle-provided `fx:deploy` task in `ant-javafx.jar` is not very flexible with regard to this specification of these "drop-in" resources, so tweaking an installer can be frustrating the first time around. Any encountered problems are likely to be associated with mis-named or mis-located files, and *not* a problem with **sbt-javafx**. For an example build configuration, see the `example-packaging` source in the [examples repository](https://github.com/kavedaa/sbt-javafx-examples).

At the heart of the process of specifying the location of drop-in resources is ensuring the classpath of the ClassLoader executing `fx:deploy` can resolve the desired resources. The **sbt-javafx** plugin provides the `JFX.pkgResourcesDir` setting for prepending a path to the `fx:deploy` classpath (which is *not* the same as the SBT classpath or the `scalac` classpath).

For example, if a custom `Info.plist` file for MacOS X is defined in `src/main/resources/package/macosx/Info.plist`, the following setting would make it visible to the `fx:deploy` ant task:

```scala
JFX.pkgResourcesDir := Some(baseDirectory.value + "/src/main/resources")
```

Note that the placement of `Info.plist` in `package/macosx` is a requirement imposed by `fx:deploy`, not **sbt-javafx**.

When the `fx:deploy` ant task is run with attribute `verbose="true"`, a list of customizable files is reported to the ant console, and defaults saved to a temporary directory for copying. If customized versions of these files are placed in a specific location in the classpath for `ant-javafx.jar`'s ClassLoader. This classpath should contain a folder called `package`. This is where the `fx:deploy` task looks for files with project- and platform-specific resource files. See the Oracle docs for specifics, but the basic structure is `package/{macosx,windows,linux}/[drop-in-resources]`.

To debug the packaging process, set `JFX.verbose := true` in your `build.sbt` file, run `sbt package-javafx` at least once, and then run `ant` against the generated `target/scala-x.yz/build.xml` file (i.e. value of `crossTarget.value + "/build.xml"`). Running `ant` with the `fx:deploy` task in verbose mode simplifies the debugging process when your drop-in resources aren't being picked up by `ant-javafx.jar`, and helps understand what additional resources might be customized. As mentioned, the `fx:deploy` task is fussy about names and locations of these resource files. For example, the name of application replacement icons have to match application name, and the `package/{os-name}/` structure is required.

#### Using the correct Java version

Self-contained applications must be packaged using the JDK version of the JRE and not the stand-alone JRE. (On Windows, if you have installed the JDK you will probably have both.) If you attempt to use the JRE version, you will get an error message saying "jvm.dll is not found".

This means that SBT must be started with the JDK version of the JRE. This can be assured by setting `JAVA_HOME` to the correct path, either globally or within SBT's `sbt.bat` startup file. Another option on Windows is to uninstall the JRE and ensure `JAVA_HOME` and applicable `PATH` entries point to the JDK binaries.

### Java-only applications

It is very much possible to use the plugin to package applications written in Java. If your application uses no Scala code at all, you might want to use the `javaOnly` setting:

```scala
JFX.javaOnly := true
```

This is a convenience setting that excludes the standard Scala library from being packaged with the application, and makes the output path a bit simpler, so that it becomes e.g. `target/my-javafx-application-1.0/`.
