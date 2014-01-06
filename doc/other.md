## Other settings

### Application info

The following keys allow specification of additional metadata for the installer and application manifest. Details are provided in the [fx:info JavaFX Ant Task Reference](http://docs.oracle.com/javafx/2/deployment/javafx_ant_task_reference.htm#CIAIEJHG).

```scala
JFX.vendor := "ACME Inc."

JFX.title := "Rocket Launcher"

JFX.category := "Mission critical"

JFX.description := "Launches rockets"

JFX.copyright := "ACME 2013"

JFX.license := "ACME"
```

### Signing

Application component signing may be required for JNLP, Applet, and native installer deployments, depending on platform and security settings. See the [fx:signjar JavaFX Ant Task Reference](http://docs.oracle.com/javafx/2/deployment/javafx_ant_task_reference.htm#CIADDAEE) for details.


```scala
JFX.elevated := true

JFX.keyStore := Some("path/to/keystore")

JFX.storePass := Some("mypassword")

JFX.alias := Some("myalias")

JFX.keyPass := Some("mykeypass")
```

