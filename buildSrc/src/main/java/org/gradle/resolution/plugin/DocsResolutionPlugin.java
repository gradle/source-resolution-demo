package org.gradle.resolution.plugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.ArtifactView;
import org.gradle.api.attributes.*;

public final class DocsResolutionPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        project.getTasks().register("resolveSource", ResolveDocsTask.class, task -> {
            task.setGroup("documentation");
            task.setDescription("Resolve source artifacts for all runtime dependencies");

            // Set task input and output conventions
            ArtifactView docsView = buildDocumentationView(project, DocsType.SOURCES);
            task.getDocs().from(docsView.getFiles());
            task.getDestinationDir().convention(project.getLayout().getBuildDirectory().dir("sources"));

            // Always rerun this task
            task.getOutputs().upToDateWhen(e -> false);
        });

        project.getTasks().register("resolveJavadoc", ResolveDocsTask.class, task -> {
            task.setGroup("documentation");
            task.setDescription("Resolve javadoc artifacts for all runtime dependencies");

            // Set task input and output conventions
            ArtifactView docsView = buildDocumentationView(project, DocsType.JAVADOC);
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
    }

    /**
     * Sets up an ArtifactView based on this project's runtime classpath which will fetch documentation.
     *
     * @param project the project to investigate
     * @param docsType the type of documentation artifact the returned view will fetch
     * @return ArtifactView which will fetch documentation
     */
    private ArtifactView buildDocumentationView(Project project, String docsType) {
        return project.getConfigurations().getByName("runtimeClasspath").getIncoming().artifactView(view -> {
            view.setLenient(true);
            AttributeContainer attributes = view.getAttributes();
            attributes.attribute(Category.CATEGORY_ATTRIBUTE, project.getObjects().named(Category.class, Category.DOCUMENTATION));
            attributes.attribute(Bundling.BUNDLING_ATTRIBUTE, project.getObjects().named(Bundling.class, Bundling.EXTERNAL));
            attributes.attribute(DocsType.DOCS_TYPE_ATTRIBUTE, project.getObjects().named(DocsType.class, docsType));
            attributes.attribute(Usage.USAGE_ATTRIBUTE, project.getObjects().named(Usage.class, Usage.JAVA_RUNTIME));
        });
    }
}
