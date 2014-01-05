## Running from within SBT

SBT is not able to launch a `javafx.application.Application` on its own. It needs a class with a static `main` method for its `run` task, which name must be configured in the `mainClass` setting (which is distinct from `JFX.mainClass`).

By default, the plugin will set SBT's `mainClass` to the same value as `JFX.mainClass`. This makes it simple to add the necessary launcher code.

### Scala-based applications

You can use a companion object with a `main` method that has code to launch the JavaFX application, e.g.:

```scala
object MyJavaFXApplication {
  def main(args: Array[String]) {
    Application launch(classOf[MyJavaFXApplication], args: _*)
  }
}
```
 
If you for some reason would want to name this differently, you can override SBT's `mainClass` like this, e.g.:

```scala
mainClass in (Compile, run) := Some("some.other.Launcher")
```

### Java-based applications

You can add a static `main` method to your JavaFX application class, e.g.:


```java
public class MyJavaFXApplication extends Application {

	public static void main(String[] args) {
		Application.launch(args);
	}
	
	// rest of application here
}
```

