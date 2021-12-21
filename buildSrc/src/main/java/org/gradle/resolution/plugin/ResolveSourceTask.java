package org.gradle.resolution.plugin;

import org.gradle.api.attributes.Category;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.artifacts.ArtifactView;

import java.io.File;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class ResolveSourceTask extends org.gradle.api.DefaultTask {
    @OutputDirectory
    public abstract DirectoryProperty getDestinationDir();

    @TaskAction
    public void printSources() {
        // Set up an ArtifactView based on the runtime classpath which will fetch documentation
        ArtifactView sourcesView = getProject().getConfigurations().getByName("runtimeClasspath").getIncoming().artifactView(view -> {
            view.setLenient(true);
            view.getAttributes().attribute(Category.CATEGORY_ATTRIBUTE, getProject().getObjects().named(Category.class, Category.DOCUMENTATION));
        });

        // Fetch the source files from the ArtifactView
        Set<File> sourceFiles = sourcesView.getFiles().getFiles();

        // Copy them to the output folder
        getProject().copy(spec -> {
            spec.from(sourceFiles);
            spec.into(getDestinationDir());
        });

        // And print the result to the console
        getLogger().lifecycle("Resolved the following sources files: {}", sourceFiles.stream().map(File::getName).sorted().collect(Collectors.toList()));
    }
}
