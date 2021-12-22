# Documentation Resolution Demo

This project demonstrates how to use an [ArtifactView](https://docs.gradle.org/current/javadoc/org/gradle/api/artifacts/ArtifactView.html) to obtain source and Javadoc jars for a project's runtime dependencies and download them in parallel.

## Running the Java example

1) Run `./gradlew :lib:resolveDocumentation` to attempt to resolve Source and Javadoc artifacts of all the runtime dependencies of the `lib` project.  These will be printed to the console, downloaded and copied into the `lib/build/sources` directory. 

2) Running this task initially results in no output or files downloaded.  This demonstrates the current behavior.  You should see output like this:

```
> Task :lib:resolveJavadoc
Resolved the following files: []

> Task :lib:resolveSource
Resolved the following files: []

BUILD SUCCESSFUL in 978ms
```

3) Next, open the file `buildSrc/src/main/java/org/gradle/resolution/plugin/DocsResolutionPlugin.java` and uncomment line 113:

```
// view.withVariantReselection();
```

4) Now rerun `./gradlew :lib:resolveDocumentation`.  This demonstrates the new behavior.  You should see output like this:

```
> Task :lib:resolveJavadoc
Resolved the following files: [annotations-13.0-javadoc.jar, bean-validators-0.7.0-javadoc.jar, commons-beanutils-1.9.2-javadoc.jar, commons-collections-3.2.2-javadoc.jar, commons-digester-1.8.1-javadoc.jar, commons-lang3-3.6-javadoc.jar, commons-logging-1.2-javadoc.jar, commons-validator-1.6-javadoc.jar, kotlin-stdlib-1.4.10-javadoc.jar, kotlin-stdlib-common-1.4.10-javadoc.jar, okhttp-4.9.3-javadoc.jar, util-1.0-javadoc.jar, validation-api-1.1.0.Final-javadoc.jar]

> Task :lib:resolveSource
Resolved the following files: [annotations-13.0-sources.jar, bean-validators-0.7.0-sources.jar, commons-beanutils-1.9.2-sources.jar, commons-collections-3.2.2-sources.jar, commons-digester-1.8.1-sources.jar, commons-lang3-3.6-sources.jar, commons-logging-1.2-sources.jar, commons-validator-1.6-sources.jar, kotlin-stdlib-1.4.10-sources.jar, kotlin-stdlib-common-1.4.10-sources.jar, okhttp-4.9.3-sources.jar, validation-api-1.1.0.Final-sources.jar]

BUILD SUCCESSFUL in 786ms
```

5) You should now be able to locate the Source and Javadoc artifacts in the `/lib/build/sources` and `lib/build/javadoc` directories, respectively.

## Running the Android example

1) Run `./gradlew :lib:resolveAndroidDocumentation` to attempt to resolve Source and Javadoc artifacts of all the runtime dependencies of the `lib` project as if it were an Android project.  These will be printed to the console, downloaded and copied into the `lib/build/sources` directory. 

2) Running this task initially results in no output or files downloaded.  This demonstrates the current behavior.  You should see output like this:

```
> Task :lib:resolveAndroidJavadoc
Resolved the following files: []

> Task :lib:resolveAndroidSource
Resolved the following files: []

BUILD SUCCESSFUL in 978ms
```

3) Next, open the file `buildSrc/src/main/java/org/gradle/resolution/plugin/DocsResolutionPlugin.java` and uncomment line 128:

```
// view.withVariantReselection();
```

4) Now rerun `./gradlew :lib:resolveAndroidDocumentation`.  This demonstrates the new behavior.  You should see output like this:

```
> Task :lib:resolveAndroidJavadoc
Resolved the following files: [shaky-3.0.3-SNAPSHOT-release-javadoc.jar]

> Task :lib:resolveAndroidSource
Resolved the following files: [shaky-3.0.3-SNAPSHOT-release-sources.jar]

BUILD SUCCESSFUL in 786ms
```

## Project Structure

There are two projects in this demo: `lib` and `util`.  Lib is the example project for which we're interested in resolving documentation artifacts.  It is a `java-library` project which includes several `api` dependencies upon open source projects.

The direct dependencies of `lib` are example open source projects which publish Gradle Module Metadata and are located in Maven Central.  There is also a dependency upon the included sibling `util` project.

The `util` project is **not** added as a project dependency, but is resolved via the local Maven repository `/mavenRepo`.  It has already been published there, and result of publishing has been committed to this git repo, so you do not have to run any tasks to publish it prior to running the demo.

The `util` project does **not** publish any Source variant information in its Metadata.  It **does** depend upon Bean Validators, which has a Source variant available.  This demonstrates that this situation does **not** impact resolution of other source dependencies.  It does publish Javadoc.  To change either of these behaviors, in the `util/build.gradle` file, you can edit the block:
```
java {
    withJavadocJar()
    // withSourcesJar()
}
```
and then run `./gradlew :util:publishUtilPublicationToDemoRepository` to republish this project.

The Android project is based on this [Android Issue](https://issuetracker.google.com/issues/197636221) and published in `/mavenRepo`.

### The `ResolveDocsTask` Task

`resolveDocumentation` is a lifecycle task which causes 2 copies of `ResolveDocsTask` (located in `buildSrc/src/main/java/org/gradle/resolution/plugin/ResolveDocsTask.java`) to run which resolve the Sources and Javadoc artifacts, print their filenames, and copy them locally.

These tasks work by setting up an `ArtifactView` based on the project's `runtimeClasspath` configuration.  They add the same set of [Attributes](https://docs.gradle.org/current/javadoc/org/gradle/api/attributes/Attribute.html) as would be present in a variant containing Sources or Javadoc published by Gradle.  This causes the view to search for variants with a matching set of attributes in each component found by the `runtimeClasspath`, and to resolve the artifact jars contained within these components for these documentation variants.

These tasks are setup to ignore any `UP-TO-DATE` checking and always run.
