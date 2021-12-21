package org.gradle.resolution.plugin;

import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.FileSystemOperations;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

import javax.inject.Inject;
import java.io.File;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class ResolveSourceTask extends org.gradle.api.DefaultTask {
    @OutputDirectory
    public abstract DirectoryProperty getDestinationDir();

    @InputFiles
    public abstract ConfigurableFileCollection getSources();

    @Inject
    protected abstract FileSystemOperations getFileSystemOperations();

    @TaskAction
    public void printSources() {
        // Fetch the source files from the ArtifactView
        Set<File> sourceFiles = getSources().getFiles();

        // Copy them to the output folder
        getFileSystemOperations().copy(spec -> {
            spec.from(sourceFiles);
            spec.into(getDestinationDir());
        });

        // And print the result to the console
        getLogger().lifecycle("Resolved the following sources files: {}", sourceFiles.stream().map(File::getName).sorted().collect(Collectors.toList()));
    }
}
