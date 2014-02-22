package no.vedaadata.sbtjavafx

import sbt._
import Keys._
import classpath.ClasspathUtilities
import org.apache.tools.ant

//	Types and utils used for jdk/sdk configuration

sealed trait DevKit
case class JDK(path: String) extends DevKit
case class SDK(path: String) extends DevKit

object DevKit {
  def jfxrt(devKit: DevKit) = devKit match {
    case JDK(path) => path + "/jre/lib/jfxrt.jar"
    case SDK(path) => path + "/rt/lib/jfxrt.jar"
  }
  def antLib(devKit: DevKit) = devKit match {
    case JDK(path) => path + "/lib/ant-javafx.jar"
    case SDK(path) => path + "/lib/ant-javafx.jar"
  }
  def isJdk(devKit: DevKit) = devKit.isInstanceOf[JDK]
}

//	Wrapper classes for grouping the settings, since there's a lot of them

case class JFX(
  paths: Paths,
  mainClass: Option[String],
  output: Output,
  template: Template,
  dimensions: Dimensions,
  permissions: Permissions,
  info: Info,
  signing: Signing,
  misc: Misc)

case class Paths(devKit: Option[DevKit], jfxrt: Option[String], antLib: Option[String], pkgResourcesDir: Option[String])

case class Output(nativeBundles: String, artifactBaseName: (String, ModuleID, Artifact) => String, artifactBaseNameValue: String)

case class Template(file: Option[String], destFile: Option[String], placeholderId: String)

case class Permissions(elevated: Boolean, cacheCertificates: Boolean)

case class Signing(keyStore: Option[File], storePass: Option[String], alias: Option[String], keyPass: Option[String], storeType: Option[String])

case class Dimensions(width: Int, height: Int, embeddedWidth: String, embeddedHeight: String)

case class Misc(cssToBin: Boolean, verbose: Boolean)

case class Info(vendor: String, title: String, category: String, copyright: String, description: String, license: String)

//	The plugin

object JavaFXPlugin extends Plugin {

  //	Define the keys

  val jfx = SettingKey[JFX]("javafx", "All JavaFX settings.")

  object JFX {

    private def prefixed(name: String) = List(jfx.key.label, name) mkString "-"

    val devKit = SettingKey[Option[DevKit]](prefixed("dev-kit"), "Path to JDK or JavaFX SDK.")

    val jfxrt = SettingKey[Option[String]](prefixed("jfxrt"), "Path to jfxrt.jar.")

    val antLib = SettingKey[Option[String]](prefixed("ant-lib"), "Path to ant-javafx.jar.")

    val pkgResourcesDir = SettingKey[Option[String]](prefixed("pkg-resources-dir"), "Path containing the `package/{windows,macosx,linux}` directory, to be added to the ant-javafx.jar classpath for drop-in resources. See https://blogs.oracle.com/talkingjavadeployment/entry/native_packaging_cookbook_using_drop for details.")

    val paths = SettingKey[Paths](prefixed("paths"), "JavaFX paths settings.")

    val addJfxrtToClasspath = SettingKey[Boolean](prefixed("add-jfxrt-to-classpath"), "Whether jfxrt.jar should be added to compile and runtime classpaths.")

    val mainClass = SettingKey[Option[String]](prefixed("main-class"), "Entry point for JavaFX application, must extend javafx.application.Application and implement the start() method.")

    val javaOnly = SettingKey[Boolean](prefixed("java-only"), "Convenience setting for JavaFX applications in pure Java, sets some other settings to usable defaults for this scenario.")

    val output = SettingKey[Output](prefixed("output"), "JavaFX output settings.")

    val nativeBundles = SettingKey[String](prefixed("native-bundles"), "Which native bundles to create, if any.")
    val artifactBaseName = SettingKey[(String, ModuleID, Artifact) => String](prefixed("artifact-base-name"), "Function that produces the JavaFX artifact name (without file extension) from its definition.")
    val artifactBaseNameValue = SettingKey[String](prefixed("artifact-base-name-value"), "The actual name of the JavaFX artifact (without file extension).")

    val template = SettingKey[Template](prefixed("template"), "JavaFX HTML template settings.")

    val templateFile = SettingKey[Option[String]](prefixed("template-file"), "HTML template input file.")
    val templateDestFile = SettingKey[Option[String]](prefixed("template-dest-file"), "HTML template output file.")
    val placeholderId = SettingKey[String](prefixed("placeholder-id"), "HTML template placeholder id.")

    val info = SettingKey[Info](prefixed("info"), "Application info settings")

    val vendor = SettingKey[String](prefixed("vendor"), "Application vendor")
    val title = SettingKey[String](prefixed("title"), "Application title")
    val category = SettingKey[String](prefixed("category"), "Application category")
    val description = SettingKey[String](prefixed("description"), "Application description")
    val copyright = SettingKey[String](prefixed("copyright"), "Application copyright")
    val license = SettingKey[String](prefixed("license"), "Application license")

    val dimensions = SettingKey[Dimensions](prefixed("dimensions"), "JavaFX dimensions settings.")

    val width = SettingKey[Int](prefixed("width"), "JavaFX application width.")
    val height = SettingKey[Int](prefixed("height"), "JavaFX application height.")
    val embeddedWidth = SettingKey[String](prefixed("embedded-width"), "JavaFX applet width.")
    val embeddedHeight = SettingKey[String](prefixed("embedded-height"), "JavaFX applet height.")

    val permissions = SettingKey[Permissions](prefixed("permissions"), "JavaFX application permission settings.")

    val elevated = SettingKey[Boolean](prefixed("elevated"), "Whether this JavaFX application requires elevated permissions.")
    val cacheCertificates = SettingKey[Boolean](prefixed("cache-certificates"), "Whether the signing certificates should be cached in the deployment descriptor.")

    val signing = SettingKey[Signing](prefixed("signing"), "Settings for JavaFX jar signing.")

    val keyStore = SettingKey[Option[File]](prefixed("keystore"), "Filename for keystore for jar signing.")
    val storePass = SettingKey[Option[String]](prefixed("storepass"), "Password for keystore for jar signing.")
    val alias = SettingKey[Option[String]](prefixed("alias"), "Key name for jar signing.")
    val keyPass = SettingKey[Option[String]](prefixed("keypass"), "Key password for jar signing.")
    val storeType = SettingKey[Option[String]](prefixed("storetype"), "Keystore type for signing.")

    val misc = SettingKey[Misc](prefixed("misc"), "Misc JavaFX settings.")

    val cssToBin = SettingKey[Boolean](prefixed("css-to-bin"), "Convert CSS files to binary.")
    val verbose = SettingKey[Boolean](prefixed("verbose"), "Enable verbose output from packager.")

    val packageJavaFx = TaskKey[Unit]("package-javafx", "Packages a JavaFX application.")

    //	Some convenience methods

    def jdk(s: String) = Some(JDK(s))
    def sdk(s: String) = Some(SDK(s))
  }

  //	Define the packaging task

  val packageJavaFxTask = (jfx, name, classDirectory in Compile, fullClasspath in Runtime, baseDirectory, crossTarget) map {
    (jfx, name, classDir, fullClasspath, baseDirectory, crossTarget) =>

      //	Check that the JavaFX Ant library is present

      val antLib = jfx.paths.antLib getOrElse sys.error("Path to ant-javafx.jar not defined.")

      if (!file(antLib).exists) sys.error(antLib + " does not exist.")

      //	Setup paths and delete anything left over from previous build

      import IO._

      val pkgResourcesDir = jfx.paths.pkgResourcesDir getOrElse baseDirectory.getAbsolutePath

      assertDirectory(file(pkgResourcesDir))

      val libDir = crossTarget / "lib"
      val distDir = crossTarget / jfx.output.artifactBaseNameValue

      val jarFile = distDir / (jfx.output.artifactBaseNameValue + ".jar")

      delete(libDir)
      delete(distDir)

      if (distDir.exists && distDir.list.nonEmpty)
        sys.error("Could not delete previous build. Make sure no files are open in " + distDir)

      val templateFile = jfx.template.file map { f =>
        if (file(f).isAbsolute) file(f)
        else (baseDirectory / f)
      }

      val templateDestFile = jfx.template.destFile orElse jfx.template.file map { f =>
        if (file(f).isAbsolute) file(f)
        else (distDir / f)
      }

      //	All library jars that should be packaged with the application

      val libJars = fullClasspath map (_.data) filter ClasspathUtilities.isArchive filterNot (_.getName endsWith "jfxrt.jar")

      //	Copy the jars to temporary lib folder

      val srcToDest = libJars map (src => (src, libDir / src.getName))

      copy(srcToDest)

      //	Generate the Ant buildfile

      val antBuildXml =
        <project name={ name } default="default" basedir="." xmlns:fx="javafx:com.sun.javafx.tools.ant">
          <target name="default">
            <taskdef resource="com/sun/javafx/tools/ant/antlib.xml" uri="javafx:com.sun.javafx.tools.ant" classpath={ pkgResourcesDir + ":" + antLib }/>
            {
              if (jfx.misc.cssToBin) {
                <delete>
                  <fileset dir={ classDir.getAbsolutePath } includes="**/*.bss"/>
                </delete>
                <fx:csstobin outdir={ classDir.getAbsolutePath }>
                  <fileset dir={ classDir.getAbsolutePath } includes="**/*.css"/>
                </fx:csstobin>
              }
            }
            <fx:application id={ name } name={ name } mainClass={ jfx.mainClass getOrElse sys.error("JFX.mainClass not defined") }/>
            <fx:jar destfile={ jarFile.getAbsolutePath }>
              <fx:application refid={ name }/>
              <fx:fileset dir={ classDir.getAbsolutePath }/>
              <fx:resources>
                { if (libJars.nonEmpty) <fx:fileset dir={ crossTarget.getAbsolutePath } includes="lib/*.jar"/> }
              </fx:resources>
            </fx:jar>
            {
              if (jfx.permissions.elevated) {
                <fx:signjar destdir={ distDir.getAbsolutePath } keyStore={ jfx.signing.keyStore map (_.getAbsolutePath) getOrElse sys.error("fx-key-store is not defined") } storePass={ jfx.signing.storePass getOrElse sys.error("fx-store-pass is not defined") } alias={ jfx.signing.alias getOrElse sys.error("fx-alias is not defined") } keyPass={ jfx.signing.keyPass getOrElse sys.error("fx-key-pass is not defined") } storeType={ jfx.signing.storeType getOrElse "jks" }>
                  <fx:fileset dir={ distDir.getAbsolutePath }/>
                </fx:signjar>
              }
            }
            {
              if (jfx.permissions.elevated && libJars.nonEmpty) {
                <fx:signjar destdir={ libDir.getAbsolutePath } keyStore={ jfx.signing.keyStore map (_.getAbsolutePath) getOrElse sys.error("fx-key-store is not defined") } storePass={ jfx.signing.storePass getOrElse sys.error("fx-store-pass is not defined") } alias={ jfx.signing.alias getOrElse sys.error("fx-alias is not defined") } keyPass={ jfx.signing.keyPass getOrElse sys.error("fx-key-pass is not defined") } storeType={ jfx.signing.storeType getOrElse "jks" }>
                  <fx:fileset dir={ libDir.getAbsolutePath }/>
                </fx:signjar>
              }
            }
            <fx:deploy width={ jfx.dimensions.width.toString } height={ jfx.dimensions.height.toString } embeddedWidth={ jfx.dimensions.embeddedWidth } embeddedHeight={ jfx.dimensions.embeddedHeight } outdir={ distDir.getAbsolutePath } outfile={ jfx.output.artifactBaseNameValue } placeholderId={ jfx.template.placeholderId } nativeBundles={ jfx.output.nativeBundles } verbose={ jfx.misc.verbose.toString }>
              <fx:application refid={ name }/>
              <fx:info vendor={ jfx.info.vendor } title={ jfx.info.title } category={ jfx.info.category } description={ jfx.info.description } copyright={ jfx.info.copyright } license={ jfx.info.license }></fx:info>
              <fx:resources>
                <fx:fileset dir={ distDir.getAbsolutePath } includes={ jfx.output.artifactBaseNameValue + ".jar" }/>
                { if (libJars.nonEmpty) <fx:fileset dir={ crossTarget.getAbsolutePath } includes="lib/*.jar"/> }
              </fx:resources>
              <fx:permissions elevated={ jfx.permissions.elevated.toString } cacheCertificates={ jfx.permissions.cacheCertificates.toString }/>
              {
                if (templateFile.isDefined) {
                  val tf = templateFile.get
                  <fx:template file={ tf.getAbsolutePath } tofile={ templateDestFile map (_.getAbsolutePath) getOrElse tf.getAbsolutePath }/>
                }
              }
            </fx:deploy>
          </target>
        </project>

      val buildFile = crossTarget / "build.xml"

      write(buildFile, antBuildXml.toString)

      //	Run the buildfile

      val antProject = new ant.Project

      antProject setUserProperty ("ant.file", buildFile.getAbsolutePath)
      antProject init ()

      val helper = ant.ProjectHelper.getProjectHelper
      helper parse (antProject, buildFile)

      println("Packaging to " + distDir + "...")

      antProject executeTarget "default"
  }

  //	Settings that are automatically loaded (as defaults)

  override val settings = Seq(
    JFX.devKit := JFX.jdk(System.getProperty("java.home") + "/.."),
    JFX.jfxrt <<= JFX.devKit(_ map DevKit.jfxrt),
    JFX.antLib <<= JFX.devKit(_ map DevKit.antLib),
    JFX.pkgResourcesDir := None,
    JFX.paths <<= (JFX.devKit, JFX.jfxrt, JFX.antLib, JFX.pkgResourcesDir) apply Paths.apply,
    JFX.addJfxrtToClasspath <<= JFX.devKit(_ map (devKit => !DevKit.isJdk(devKit)) getOrElse false),
    JFX.mainClass := None,
    JFX.javaOnly := false,
    JFX.nativeBundles := "none",
    JFX.artifactBaseName <<= crossPaths(p => (v, id, a) => List(Some(a.name), if (p) Some("_" + v) else None, Some("-" + id.revision)).flatten.mkString),
    JFX.artifactBaseNameValue <<= (scalaVersion, projectID, artifact, JFX.artifactBaseName) apply { (v, id, a, f) => f(v, id, a) },
    JFX.output <<= (JFX.nativeBundles, JFX.artifactBaseName, JFX.artifactBaseNameValue) apply Output.apply,
    JFX.templateFile := None,
    JFX.templateDestFile := None,
    JFX.placeholderId := "javafx",
    JFX.template <<= (JFX.templateFile, JFX.templateDestFile, JFX.placeholderId) apply Template.apply,
    JFX.width := 800,
    JFX.height := 600,
    JFX.embeddedWidth := "100%",
    JFX.embeddedHeight := "100%",
    JFX.dimensions <<= (JFX.width, JFX.height, JFX.embeddedWidth, JFX.embeddedHeight) apply Dimensions.apply,
    JFX.elevated := false,
    JFX.cacheCertificates := false,
    JFX.permissions <<= (JFX.elevated, JFX.cacheCertificates) apply { Permissions(_, _) },
    JFX.vendor := "Unknown",
    JFX.title <<= name,
    JFX.category := "",
    JFX.description := "",
    JFX.copyright := "",
    JFX.license := "",
    JFX.info <<= (JFX.vendor, JFX.title, JFX.category, JFX.description, JFX.copyright, JFX.license) apply Info.apply,
    JFX.keyStore := None,
    JFX.storePass := None,
    JFX.alias := None,
    JFX.keyPass := None,
    JFX.storeType := None,
    JFX.signing <<= (JFX.keyStore, JFX.storePass, JFX.alias, JFX.keyPass, JFX.storeType) apply Signing.apply,
    JFX.cssToBin := false,
    JFX.verbose := false,
    JFX.misc <<= (JFX.cssToBin, JFX.verbose) apply Misc.apply)

  //	Settings that must be manually loaded

  val jfxSettings = Seq(
    mainClass in (Compile, run) <<= JFX.mainClass map (x => x),
    (unmanagedClasspath in Compile) <<= (unmanagedClasspath in Compile, JFX.addJfxrtToClasspath, JFX.jfxrt) map { (cp, add, jfxrt) => if (add) cp :+ Attributed.blank(file(jfxrt getOrElse sys.error("Path to jfxrt.jar not defined."))) else cp },
    (unmanagedClasspath in Runtime) <<= (unmanagedClasspath in Runtime, JFX.addJfxrtToClasspath, JFX.jfxrt) map { (cp, add, jfxrt) => if (add) cp :+ Attributed.blank(file(jfxrt getOrElse sys.error("Path to jfxrt.jar not defined."))) else cp },
    (unmanagedClasspath in Test) <<= (unmanagedClasspath in Test, JFX.addJfxrtToClasspath, JFX.jfxrt) map { (cp, add, jfxrt) => if (add) cp :+ Attributed.blank(file(jfxrt getOrElse sys.error("Path to jfxrt.jar not defined."))) else cp },
    autoScalaLibrary <<= JFX.javaOnly(x => !x),
    crossPaths <<= JFX.javaOnly(x => !x),
    fork in run := true,
    JFX.packageJavaFx <<= packageJavaFxTask,
    jfx <<= (JFX.paths, JFX.mainClass, JFX.output, JFX.template, JFX.dimensions, JFX.permissions, JFX.info, JFX.signing, JFX.misc) apply { new JFX(_, _, _, _, _, _, _, _, _) })
}
