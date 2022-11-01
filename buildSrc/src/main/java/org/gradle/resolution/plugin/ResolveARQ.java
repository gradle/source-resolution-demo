package org.gradle.resolution.plugin;

import org.gradle.api.DefaultTask;
import org.gradle.api.artifacts.component.ComponentIdentifier;
import org.gradle.api.artifacts.query.ArtifactResolutionQuery;
import org.gradle.api.artifacts.result.ArtifactResolutionResult;
import org.gradle.api.artifacts.result.ArtifactResult;
import org.gradle.api.artifacts.result.ComponentArtifactsResult;
import org.gradle.api.artifacts.result.ResolutionResult;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.TaskAction;
import org.gradle.jvm.JvmLibrary;
import org.gradle.language.base.artifact.SourcesArtifact;
import org.gradle.language.java.artifact.JavadocArtifact;

import java.util.Set;
import java.util.stream.Collectors;

public abstract class ResolveARQ extends DefaultTask {
    @Internal
    public abstract Property<ResolutionResult> getResolutionResult();

    @TaskAction
    public void resolve() {
        ArtifactResolutionQuery query = getProject().getDependencies().createArtifactResolutionQuery();
        Set<ComponentIdentifier> components = getResolutionResult().get().getAllComponents().stream().map(resolved -> resolved.getId()).collect(Collectors.toSet());
        ArtifactResolutionResult result = query.forComponents(components).withArtifacts(JvmLibrary.class, JavadocArtifact.class, SourcesArtifact.class).execute();
        for (ComponentArtifactsResult resolved : result.getResolvedComponents()) {
            for (ArtifactResult javadoc : resolved.getArtifacts(JavadocArtifact.class)) {
                System.out.println("Found Javadoc " + javadoc.getId());
            }
            for (ArtifactResult sources : resolved.getArtifacts(SourcesArtifact.class)) {
                System.out.println("Found sources " + sources.getId());
            }
        }
    }
}
