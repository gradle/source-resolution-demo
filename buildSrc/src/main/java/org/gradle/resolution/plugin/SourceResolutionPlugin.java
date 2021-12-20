package org.gradle.resolution.plugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskProvider;

public class SourceResolutionPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        TaskProvider<ResolveSourceTask> resolveTask = project.getTasks().register("resolveDocumentation", ResolveSourceTask.class);
        resolveTask.configure(task -> {
            task.setGroup("documentation");
            task.setDescription("Resolve source dependencies of all runtime dependencies");

            task.getDestinationDir().convention(project.getLayout().getBuildDirectory().dir("sources"));
            task.getInputs().files(project.getConfigurations().getByName("runtimeClasspath"));
        });
    }
}
