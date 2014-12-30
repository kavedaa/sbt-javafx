### Manually edit *build.xml* when packaging

The packaging task creates a *build.xml* file in the target directory that is subsequently run by the JavaFX Ant packaging tool.

It is possible to break up this process so that you create the build file and run it as seperate steps. This allows for manually editing the build file before it is run, which could be useful if you want to experiment with JavaFX packaging settings that is currently not supported by the plugin.

To prepare the build file: execute the `javafx-prepare-build` task.

To run the build file: execute the `javafx-run-build` task.

The standard `package-javafx` task simply executes these two tasks one after another.


### Transform the *build.xml* file programmatically

As a complementary feature to the above, it is also possible to automate any editing of the XML that gets written to the *build.xml* file.

For this, you define a transformation function that takes the generated build XML as a parameter and returns XML possible transformed by your code:

```scala
JFX.transformXml := { xml =>
	//	process it here (not shown)
	xml
}
```

### Post-process the packaged artifact

If you want to do some custom processing of the finished packaged artifact (such as creating a zip file of it or copying it elsewhere), you can add a post-processing hook like this: 

```
JFX.postProcess <<= name { n => { dir =>  
	val imageDir = dir / "bundles" / n
	println("postprocessing " + imageDir)	// or do something useful
} }
```