package org.gradle.resolution.plugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class SourceResolutionPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        project.getTasks().register("resolveDocumentation", ResolveSourceTask.class, task -> {
            task.setGroup("documentation");
            task.setDescription("Resolve source dependencies of all runtime dependencies");

            // Set task input and output conventions
            task.getInputs().files(project.getConfigurations().getByName("runtimeClasspath"));
            task.getDestinationDir().convention(project.getLayout().getBuildDirectory().dir("sources"));

            // Always rerun this task
            task.getOutputs().upToDateWhen(e -> false);
        });
    }
}
