# Source Resolution Demo

This project demonstrates how to use an [ArtifactView](https://docs.gradle.org/current/javadoc/org/gradle/api/artifacts/ArtifactView.html) to obtain source jars for a project's dependencies.

## Running the Demo

Run `./gradlew :lib:resolveDocumentation` to resolve documentation of all the runtime dependencies of the `lib` project.  These will be printed to the console, downloaded and copied into the `lib/build/sources` directory.

## Project Structure

There are two projects in this demo: `lib` and `util`.  Lib is a `java-library` project which includes several `api` dependencies upon projects located in Maven Central, and also includes a dependency upon the published `util` project via a local Maven repository in `/mavenRepo'.  The published library has been committed to the project, so you do not have to publish it prior to resolving documentation.

The `util` project does **not** publish any Source variant information in its Metadata.  It ** does** depend upon Bean Validators, which has a Source variant available.  This demonstrates that this situation does **not** impact resolution of other source dependencies.

### The `resolveDocumentation` Task

This task (located in `buildSrc/src/main/java/org/gradle/resolution/plugin/ResolveSourceTask`) sets up an `ArtifactView` based on the project's `runtimeClasspath` configuration.  It adds the `Category.DOCUMENTATION` attribute, which causes the view to search for jars containing source information for every dependency resolved by that configuration.

This task is setup to ignore any `UP-TO-DATE` checking and always run.
