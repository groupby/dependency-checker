package com.groupbyinc.maven;


import org.apache.log4j.Logger;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy;
import org.apache.maven.artifact.repository.MavenArtifactRepository;
import org.apache.maven.artifact.repository.layout.DefaultRepositoryLayout;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.project.*;
import org.apache.maven.project.ProjectBuildingRequest.RepositoryMerging;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;

import java.io.File;
import java.util.ArrayList;

/**
 * This class is intended to interpolate a given pom with it's
 * dependencies
 *
 * @author Karl Heinz Marbaise
 *
 */
public class PomInterpolator {
    private static Logger LOGGER = Logger.getLogger(PomInterpolator.class);

    private RepositorySystemSession repositorySystemSession;
    private RepositorySystem repositorySystem;
    private ProjectBuildingRequest request;

    /**
     * This is needed to resolve dependencies etc. for a pom.xml
     */
    private ProjectBuilder projectBuilder;

    public PomInterpolator() {
        repositorySystem = Booter.newRepositorySystem();

        repositorySystemSession = Booter.newRepositorySystemSession( repositorySystem );

        PlexusContainer plexusContainer;
        try {
            plexusContainer = new DefaultPlexusContainer();
        } catch (PlexusContainerException e) {
            throw new IllegalStateException(e);
        }
        try {
            projectBuilder = plexusContainer.lookup(ProjectBuilder.class);
        } catch (ComponentLookupException e) {
            throw new IllegalStateException(e);
        }

        request = new DefaultProjectBuildingRequest();

        request.setRepositorySession(repositorySystemSession);
        request.setProcessPlugins(false);
        request.setValidationLevel(ModelBuildingRequest.VALIDATION_LEVEL_MAVEN_3_0);

        initRepositories();

    }

    public ArtifactRepository createExtReleasesRepository() {
    	MavenArtifactRepository repository = new MavenArtifactRepository(
                "central",
                "www.heise.de",
                new DefaultRepositoryLayout(),
                new ArtifactRepositoryPolicy(false, ArtifactRepositoryPolicy.UPDATE_POLICY_NEVER, ArtifactRepositoryPolicy.CHECKSUM_POLICY_WARN), //SNAPSHOT
                new ArtifactRepositoryPolicy(true, ArtifactRepositoryPolicy.UPDATE_POLICY_DAILY, ArtifactRepositoryPolicy.CHECKSUM_POLICY_WARN)  //RELEASE
         );
        return repository;
    }

    public ArtifactRepository createPluginsRepository() {
    	MavenArtifactRepository repository = new MavenArtifactRepository(
                "plugins",
                "www.cnn.com",
                new DefaultRepositoryLayout(),
                new ArtifactRepositoryPolicy(false, ArtifactRepositoryPolicy.UPDATE_POLICY_NEVER, ArtifactRepositoryPolicy.CHECKSUM_POLICY_WARN), //SNAPSHOT
                new ArtifactRepositoryPolicy(true, ArtifactRepositoryPolicy.UPDATE_POLICY_DAILY, ArtifactRepositoryPolicy.CHECKSUM_POLICY_WARN)  //RELEASE
            );
        return repository;
    }
    /**
     * FIXME: This method must be merged with the one from versions-check cause we use the same
     * repository list.
     */
    private void initRepositories() {
        ArrayList<ArtifactRepository> reposList = new ArrayList<ArtifactRepository>();

//        RepositoryUtils.toRepos()...
        reposList.add(createExtReleasesRepository());
        request.setRemoteRepositories(reposList);

        ArrayList<ArtifactRepository> pluginRepositoryList = new ArrayList<ArtifactRepository>();

        pluginRepositoryList.add(createPluginsRepository());
        request.setPluginArtifactRepositories(pluginRepositoryList);
    }

    /**
     * This will load a pom file but will not resolve the dependencies but
     * other things like properties, dependencyManagement are already resolved.
     * In this case you don't have information about the transitive dependencies.
     *
     * @param pomFile The pom file which should be interpolated.
     * @return The interpolated MavenProject.
     * @throws ProjectBuildingException In case of error.
     */
    public MavenProject getInterpolatedPom(File pomFile) throws ProjectBuildingException {
        return getInterpolatedPom(pomFile, false);
    }

    /**
     * This will interpolate the given POM and solve the properties etc. and in this case
     * it will solve all dependencies of this pom and download the dependencies from the
     * repositories if needed. So this can take a while until this call will return,
     * based on the number of  artifacts which needed to be downloaded.
     * @param pomFile The pom you would like to interpolate.
     * @return Interpolated MavenProject with solved dependency information.
     * @throws ProjectBuildingException in case of an error.
     */
    public MavenProject getInterpolatedPomWithResolvedDependencies(File pomFile) throws ProjectBuildingException {
        return getInterpolatedPom(pomFile, true);
    }

    private MavenProject getInterpolatedPom(File pomFile, boolean resolveDependencies) throws ProjectBuildingException {
        request.setResolveDependencies(resolveDependencies);
        request.setRepositoryMerging(RepositoryMerging.REQUEST_DOMINANT);
        ProjectBuildingResult result = projectBuilder.build(pomFile, request);
        //TODO: Logging out result.getProblems()
        return result.getProject();
    }

}
