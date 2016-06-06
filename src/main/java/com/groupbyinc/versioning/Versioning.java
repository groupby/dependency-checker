package com.groupbyinc.versioning;

import com.groupbyinc.github.GitHub;
import com.groupbyinc.maven.PomInterpolator;
import com.groupbyinc.npm.NpmManager;
import com.jcabi.aether.Aether;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.building.ModelBuildingException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuildingException;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.DependencyResolutionException;
import org.sonatype.aether.util.artifact.DefaultArtifact;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static java.util.Collections.singletonList;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class Versioning {

  private static final transient Logger LOG = LoggerFactory.getLogger(Versioning.class);

  private static Aether aether = null;

  static {

    try {

      File local = new File("/home/" + System.getProperty("user.name") + "/.m2/repository/");
      File tmpDir = new File("/tmp/dependency-checker/");
      local.mkdirs();
      tmpDir.mkdirs();


      Collection<RemoteRepository> remotes = singletonList(
          new RemoteRepository(
              "maven-central",
              "default",
              "http://repo1.maven.org/maven2/"
          )
      );
      aether = new Aether(remotes, local);
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }

  }

  private final GitHub github;
  private Runner runner = new Runner();
  private NpmManager npmManager;

  private class Runner implements Runnable {
    private boolean running = false;
    private List<Project> projects = null;
    private boolean stop = false;


    boolean isRunning() {
      return running;
    }

    void setProjects(List<Project> projects) {
      this.projects = projects;
    }

    @Override
    public void run() {
      try {
        running = true;
        for (Project project : projects) {
          try {
            if (shouldStop()) {
              break;
            }
            createDependencies(project, "/");
          } catch (Exception e) {
            LOG.error("Error creating dependencies", e);
          }

        }
      } catch (Exception e) {
        e.printStackTrace();
      }
      running = false;
    }

    void stop() {
      stop = true;

    }

    boolean shouldStop() {
      return stop;
    }

    void waitForStop() {
      while (running) {
        try {
          Thread.sleep(5);
        } catch (InterruptedException e) {
          // do nothing.
        }
      }
      stop = false;
    }
  }

  public void createProjects(List<Project> projects, String company, List<String> excludes, List<String> includes) throws IOException, XmlPullParserException,
      DependencyResolutionException, ModelBuildingException, ProjectBuildingException {
    if (projects == null) {
      throw new NullPointerException("Projects must be defined");
    }
    if (company == null) {
      throw new NullPointerException("Company must be defined");
    }
    if (runner.isRunning()) {
      runner.stop();
      runner.waitForStop();
    }
    projects.clear();
    List<String> repositories;
    if (includes.size() != 0) {
      repositories = includes;
    } else {
      repositories = github.getRepositories(company, excludes);
    }
    for (String repo : repositories) {
      Project project = new Project();
      project.setCompany(company);
      project.setName(repo);
      projects.add(project);
    }
    runner.setProjects(projects);
    new Thread(runner).start();
  }

  @SuppressWarnings("WeakerAccess")
  public Versioning(GitHub github, NpmManager npmManager) {
    this.github = github;
    this.npmManager = npmManager;
  }


  public void createDependencies(Project project2, String subPath) throws IOException, XmlPullParserException, ModelBuildingException,
      ProjectBuildingException, DependencyResolutionException {

    File rootProjectFolder = new File("/tmp/dependency-checker/" + project2.getCompany() + "/" + project2.getName());
    if ("/".equals(subPath)) {
      FileUtils.deleteDirectory(rootProjectFolder);
      LOG.debug("Checking out project:" + project2.getCompany() + "/" + project2.getName());
      github.checkoutRaw(project2.getCompany(), project2.getName(), rootProjectFolder);
    }


    if (runner.shouldStop()) {
      return;
    }

    File[] dirs = rootProjectFolder.listFiles(File::isDirectory);

    File dir = dirs[0];
    File projectFolder = new File(dir.getPath() + subPath);
    File pomFile = new File(dir.getPath() + subPath + "pom.xml");
    File packageFile = new File(dir.getPath() + subPath + "package.json");

    if (pomFile.exists()) {
      if (findMavenDependencies(project2, subPath, pomFile)) return;
    }
    if (packageFile.exists()) {
      if (findNpmDependencies(project2, subPath, projectFolder, FileUtils.readFileToString(packageFile))) return;
    }
    File[] folders = projectFolder.listFiles(File::isDirectory);
    for (File folder : folders) {
      if (runner.shouldStop()) {
        return;
      }
      createDependencies(project2, subPath + folder.getName() + "/");
    }

  }

  //  npm-check-updates output:
  //
  //  mocha             ^2.4.5  →   ^2.5.3
  private boolean findNpmDependencies(Project project2, String subPath, File packageDir, String rawJson) throws IOException, DependencyResolutionException {
    Module module = new Module();
    try {
      module.setName(subPath.substring(0, subPath.length() - 1));
      module.setType("javascript");
      project2.getModules().add(module);
      LOG.info(project2.getCompany() + "/" + project2.getName() + module.getName());
      String dependencyTxt = npmManager.getDependencyJson(packageDir, rawJson);
      String[] lines = dependencyTxt.split("\n");
      for (String line : lines) {
        Dependency d = new Dependency();
        Library lib = new Library(d);
        populateDependency(lib, line);
        module.getLibraries().add(lib);
      }
    } catch (Exception e) {
      LOG.info("Error parsing project: " + e.getMessage());
      module.setError(e.getMessage());
    }
    return false;
  }

  private void populateDependency(Library lib, String line) {
    Dependency d = lib.getDependency();
    String[] nameOldNew = line.replaceAll("[→]", "").replaceAll("[ ]{1,50}", " ").trim().split(" ");
    d.setArtifactId(nameOldNew[0]);
    d.setVersion(nameOldNew[1]);
    lib.getNewerVersions().add(nameOldNew[2]);

  }

  private boolean findMavenDependencies(Project project2, String subPath, File pomFile) throws IOException, DependencyResolutionException {
    Module module = new Module();
    try {
      module.setName(subPath.substring(0, subPath.length() - 1));
      module.setType("maven");
      project2.getModules().add(module);
      LOG.info(project2.getCompany() + "/" + project2.getName() + module.getName());
      String rawPom = FileUtils.readFileToString(pomFile, "UTF-8");
      MavenProject interpolatedPom = new PomInterpolator().getInterpolatedPom(pomFile);
      Set<String> inherited = new HashSet<>();
      int badVersions = 0;
      int testVersions = 0;
      List<Dependency> dependencies = interpolatedPom.getDependencies();
      for (Dependency d : dependencies) {

        if (runner.shouldStop()) {
          return true;
        }
        try {

          String current = d.getGroupId() + ":" + d.getArtifactId() + ":" + d.getVersion();
          boolean isTest = "test".equals(d.getScope());

          List<String> versions = new ArrayList<>();
          for (int decimals = 0; decimals < 4; decimals++) {
            List<Artifact> smallestNewestVersions = getSmallestArtifacts(d, decimals);

            Artifact n = smallestNewestVersions.get(0);

            if (!d.getVersion().equals(n.getVersion())) {
              Library library = new Library(d);
              if (!rawPom.contains(d.getArtifactId()) && !module.getName().equals("")) {
                inherited.add(d.getArtifactId());
                library.setInherited(true);
                if (!module.getLibraries().contains(library)) {
                  module.getLibraries().add(library);
                }
                continue;
              }
              String versionString = current + " --> " + n.getVersion();
              if (!versions.contains(versionString)) {
                versions.add(versionString);
                module.getLibraries().add(library);
                library.getNewerVersions().add(n.getVersion());
                badVersions++;
                if (isTest) {
                  testVersions++;
                }
              }
            }
          }
          for (int i = 0; i < versions.size(); i++) {
            String version = versions.get(i);
            LOG.info("  " + StringUtils.repeat(".", i) + (i > 0 ? " " : "") + (isTest ? "TEST " : "") + version + (!rawPom.contains(d
                .getArtifactId()) &&
                module.getName().equals
                    ("") ? " (inherited)" : ""));
          }
        } catch (DependencyResolutionException e) {
          if (!e.getMessage().contains("without authentication")) {
            throw e;
          }
        }
      }


      if (!inherited.isEmpty()) {
        LOG.info("  > needed version changes inherited from parents: " + inherited);
      }
      LOG.info(StringUtils.repeat("X", badVersions + inherited.size()) + " " + project2.getCompany() + "/" + project2.getName() + module.getName()
          + " "
          + (badVersions + inherited.size()) + " libraries behind:\n  "
          + badVersions + " are in this pom, " + testVersions + " are tests\n  "
          + inherited.size() + " inherited from parents");
    } catch (ProjectBuildingException e) {
      LOG.info("Error parsing project: " + e.getMessage());
      module.setError(e.getMessage());
    }
    return false;
  }

  private List<Artifact> getSmallestArtifacts(Dependency d, int decimals) throws DependencyResolutionException {
    return aether.resolve(new DefaultArtifact(d.getGroupId(), d.getArtifactId(), d.getClassifier(), d.getType(), "[" + d.getVersion() + "," +
        getNextLargestIncrement(d.getVersion(), decimals + 1) + ")"), "");
  }


  private static String getNextLargestIncrement(String version, int decimals) {
    if (!version.contains(".")) {
      return "10000";
    }
    for (int i = 0; i < decimals; i++) {
      int endIndex = version.lastIndexOf(".");
      if (endIndex == -1) {
        return "10000";
      }
      version = version.substring(0, endIndex);
    }
    return version + ".10000";

  }
}