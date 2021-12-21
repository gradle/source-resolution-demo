# Documentation Resolution Demo

This project demonstrates how to use an [ArtifactView](https://docs.gradle.org/current/javadoc/org/gradle/api/artifacts/ArtifactView.html) to obtain source and Javadoc jars for a project's runtime dependencies in parallel.

## Running the Demo

Run `./gradlew :lib:resolveDocumentation` to resolve documentation of all the runtime dependencies of the `lib` project.  These will be printed to the console, downloaded and copied into the `lib/build/sources` directory.

## Project Structure

There are two projects in this demo: `lib` and `util`.  Lib is a `java-library` project which includes several `api` dependencies upon projects located in Maven Central, and also includes a dependency upon the published `util` project which is available via the local Maven repository `/mavenRepo'.  The published `util` library has been committed to the project, so you do not have to publish it prior to resolving documentation.

The `util` project does **not** publish any Source variant information in its Metadata.  It **does** depend upon Bean Validators, which has a Source variant available.  This demonstrates that this situation does **not** impact resolution of other source dependencies.  It does publish Javadoc.  To change either of these behaviors, in the `util/build.gradle` file, you can edit the block:
```
java {
    withJavadocJar()
    // withSourcesJar()
}
```
and then run `./gradlew :util:publishUtilPublicationToDemoRepository` to republish this project.

### The `ResolveDocsTask` Task

`resolveDocumentation` is a lifecycle task which causes 2 copies of `ResolveDocsTask`s (located in `buildSrc/src/main/java/org/gradle/resolution/plugin/ResolveDocsTask.java`) to run which fetch the Sources and Javadoc and copy them into `/lib/build/sources` and `lib/build/javadoc`, respectively.

These tasks work by setting up an `ArtifactView` based on the project's `runtimeClasspath` configuration.  They add the same set of [Attributes](https://docs.gradle.org/current/javadoc/org/gradle/api/attributes/Attribute.html) as would be present in a variant containing Sources or Javadoc published by Gradle.  This causes the view to search for variants with a matching set of attributes and resolve the artifact jars contained within them for every dependency resolved by that configuration.

These tasks are setup to ignore any `UP-TO-DATE` checking and always run.
