package org.gradle.resolution.plugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.ArtifactView;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.attributes.*;
import org.gradle.api.model.ObjectFactory;

import javax.inject.Inject;

public abstract class DocsResolutionPlugin implements Plugin<Project> {

    @Inject
    protected abstract ObjectFactory getObjectFactory();

    @Override
    public void apply(Project project) {
        Configuration runtimeClasspath = project.getConfigurations().getByName("runtimeClasspath");
        project.getTasks().register("resolveSource", ResolveDocsTask.class, task -> {
            task.setGroup("documentation");
            task.setDescription("Resolve source artifacts for all runtime dependencies");

            // Set task input and output conventions
            ArtifactView docsView = buildDocumentationView(runtimeClasspath, DocsType.SOURCES);
            task.getDocs().from(docsView.getFiles());
            task.getDestinationDir().convention(project.getLayout().getBuildDirectory().dir("sources"));

            // Always rerun this task
            task.getOutputs().upToDateWhen(e -> false);
        });

        project.getTasks().register("resolveJavadoc", ResolveDocsTask.class, task -> {
            task.setGroup("documentation");
            task.setDescription("Resolve javadoc artifacts for all runtime dependencies");

            // Set task input and output conventions
            ArtifactView docsView = buildDocumentationView(runtimeClasspath, DocsType.JAVADOC);
            task.getDocs().from(docsView.getFiles());
            task.getDestinationDir().convention(project.getLayout().getBuildDirectory().dir("javadoc"));

            // Always rerun this task
            task.getOutputs().upToDateWhen(e -> false);
        });

        project.getTasks().register("resolveDocumentation", Task.class, task -> {
            task.setGroup("documentation");
            task.setDescription("Resolve and download all documentation of runtime dependencies");

            task.dependsOn("resolveSource", "resolveJavadoc");
        });

        Configuration releaseRuntimeClasspath = project.getConfigurations().create("releaseRuntimeClasspath", configuration -> {
            configuration.setCanBeResolved(true);
            configuration.setCanBeConsumed(false);
/*
--------------------------------------------------
Configuration releaseRuntimeClasspath
--------------------------------------------------
Runtime classpath of compilation 'release' (target  (androidJvm)).

Attributes
    - com.android.build.api.attributes.BuildTypeAttr = release
    - org.gradle.usage                               = java-runtime
    - org.jetbrains.kotlin.platform.type             = androidJvm
*/
            configuration.attributes(attributes -> {
                attributes.attribute(Attribute.of("com.android.build.api.attributes.BuildTypeAttr", String.class), "release");
                attributes.attribute(Attribute.of("org.jetbrains.kotlin.platform.type", String.class), "androidJvm");
                attributes.attribute(Usage.USAGE_ATTRIBUTE, getObjectFactory().named(Usage.class, Usage.JAVA_RUNTIME));
            });
        });

        project.getTasks().register("resolveAndroidSource", ResolveDocsTask.class, task -> {
            task.setGroup("documentation");
            task.setDescription("Resolve source artifacts for all Android's runtime dependencies");

            // Set task input and output conventions
            ArtifactView docsView = buildAndroidDocumentationView(releaseRuntimeClasspath, DocsType.SOURCES);
            task.getDocs().from(docsView.getFiles());
            task.getDestinationDir().convention(project.getLayout().getBuildDirectory().dir("sources"));

            // Always rerun this task
            task.getOutputs().upToDateWhen(e -> false);
        });

        project.getTasks().register("resolveAndroidJavadoc", ResolveDocsTask.class, task -> {
            task.setGroup("documentation");
            task.setDescription("Resolve javadoc artifacts for all Android's runtime dependencies");

            // Set task input and output conventions
            ArtifactView docsView = buildAndroidDocumentationView(releaseRuntimeClasspath, DocsType.JAVADOC);
            task.getDocs().from(docsView.getFiles());
            task.getDestinationDir().convention(project.getLayout().getBuildDirectory().dir("javadoc"));

            // Always rerun this task
            task.getOutputs().upToDateWhen(e -> false);
        });

        project.getTasks().register("resolveAndroidDocumentation", Task.class, task -> {
            task.setGroup("documentation");
            task.setDescription("Resolve and download all documentation of Android's runtime dependencies");

            task.dependsOn("resolveAndroidSource", "resolveAndroidJavadoc");
        });

        project.getTasks().register("resolveWithARQ", ResolveARQ.class, task -> {
            task.setGroup("documentation");
            task.setDescription("Resolve source and javadoc artifacts for all runtime dependencies using ArtifactResolutionQuery");

            task.getResolutionResult().convention(project.provider(() -> runtimeClasspath.getIncoming().getResolutionResult()));

            // Always rerun this task
            task.getOutputs().upToDateWhen(e -> false);
        });
    }

    /**
     * Sets up an ArtifactView based on this project's runtime classpath which will fetch documentation.
     *
     * @param graph the resolution graph to retrieve artifacts from
     * @param docsType the type of documentation artifact the returned view will fetch
     * @return ArtifactView which will fetch documentation
     */
    private ArtifactView buildDocumentationView(Configuration graph, String docsType) {
        return graph.getIncoming().artifactView(view -> {
            view.setLenient(true);

            // Uncomment me to view the new behavior
            // view.withVariantReselection();

            AttributeContainer attributes = view.getAttributes();
            attributes.attribute(Category.CATEGORY_ATTRIBUTE, getObjectFactory().named(Category.class, Category.DOCUMENTATION));
            attributes.attribute(Bundling.BUNDLING_ATTRIBUTE, getObjectFactory().named(Bundling.class, Bundling.EXTERNAL));
            attributes.attribute(DocsType.DOCS_TYPE_ATTRIBUTE, getObjectFactory().named(DocsType.class, docsType));
            attributes.attribute(Usage.USAGE_ATTRIBUTE, getObjectFactory().named(Usage.class, Usage.JAVA_RUNTIME));
        });
    }

    private ArtifactView buildAndroidDocumentationView(Configuration graph, String docsType) {
        return graph.getIncoming().artifactView(view -> {
            view.setLenient(true);

            // Uncomment me to view the new behavior
            // view.withVariantReselection();

            AttributeContainer attributes = view.getAttributes();
            attributes.attribute(Attribute.of("com.android.build.api.attributes.BuildTypeAttr", String.class), "release");
            attributes.attribute(Category.CATEGORY_ATTRIBUTE, getObjectFactory().named(Category.class, Category.DOCUMENTATION));
            attributes.attribute(Bundling.BUNDLING_ATTRIBUTE, getObjectFactory().named(Bundling.class, Bundling.EXTERNAL));
            attributes.attribute(DocsType.DOCS_TYPE_ATTRIBUTE, getObjectFactory().named(DocsType.class, docsType));
            attributes.attribute(Usage.USAGE_ATTRIBUTE, getObjectFactory().named(Usage.class, Usage.JAVA_RUNTIME));
        });
    }
}
