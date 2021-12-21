package org.gradle.resolution.plugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ArtifactView;
import org.gradle.api.attributes.Category;

public class SourceResolutionPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        project.getTasks().register("resolveDocumentation", ResolveSourceTask.class, task -> {
            task.setGroup("documentation");
            task.setDescription("Resolve source dependencies of all runtime dependencies");

            // Set task input and output conventions
            ArtifactView sourcesView = buildSourcesView(project);
            task.getSources().from(sourcesView.getFiles());
            task.getDestinationDir().convention(project.getLayout().getBuildDirectory().dir("sources"));

            // Always rerun this task
            task.getOutputs().upToDateWhen(e -> false);
        });
    }

    /**
     * Sets up an ArtifactView based on this projects's runtime classpath which will fetch documentation.
     *
     * @param project the project to investigate
     * @return ArtifaceView which will fetch documentation
     */
    private ArtifactView buildSourcesView(Project project) {
        return project.getConfigurations().getByName("runtimeClasspath").getIncoming().artifactView(view -> {
            view.setLenient(true);
            view.getAttributes().attribute(Category.CATEGORY_ATTRIBUTE, project.getObjects().named(Category.class, Category.DOCUMENTATION));
        });
    }
}
