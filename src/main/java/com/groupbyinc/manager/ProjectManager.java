package com.groupbyinc.manager;

import com.groupbyinc.github.GitHub;
import com.groupbyinc.maven.PomManipulator;
import com.groupbyinc.npm.NpmManager;
import com.groupbyinc.npm.PackageManipulator;
import com.groupbyinc.versioning.Project;
import com.groupbyinc.versioning.Upgrade;
import com.groupbyinc.versioning.Versioning;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.building.ModelBuildingException;
import org.apache.maven.project.ProjectBuildingException;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.aether.resolution.DependencyResolutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Component
public class ProjectManager {

  private static final transient Logger LOG = LoggerFactory.getLogger(ProjectManager.class);

  private static final Map<String, Versioning> tokenToVersioning = new HashMap<>();
  private static final Map<String, List<Project>> tokenToProjectList = new HashMap<>();
  private NpmManager npmManager;

  @Autowired
  public ProjectManager(NpmManager npmManager) {
    this.npmManager = npmManager;
  }

  public List<Project> createProject(String token, String company, List<String> excludes, List<String> includes) throws ProjectBuildingException, XmlPullParserException, DependencyResolutionException, ModelBuildingException, IOException {
    if (!tokenToVersioning.containsKey(token)) {
      tokenToVersioning.put(token, new Versioning(new GitHub(token), npmManager));
    }
    if (!tokenToProjectList.containsKey(token)) {
      tokenToProjectList.put(token, new ArrayList<>());
    }

    Versioning versioning = tokenToVersioning.get(token);
    List<Project> projects = tokenToProjectList.get(token);
    versioning.createProjects(projects, company, excludes, includes);
    return projects;
  }

  public List<Project> getProject(String token) {
    return tokenToProjectList.containsKey(token) ? tokenToProjectList.get(token) : new ArrayList<>();
  }


  public String createPullRequest(GitHub github, String company, String projectName, String moduleName, List<Upgrade> upgrades, String moduleType) throws IOException {
    PomManipulator pomManipulator = new PomManipulator();
    PackageManipulator packageManipulator = new PackageManipulator();
    File rootProjectFolder = new File(github.getRootFolder() + company + "/" + projectName);
    File[] dirs = rootProjectFolder.listFiles(File::isDirectory);
    File fileToChange = new File(dirs[0].getPath() + "/" + moduleName + ("maven".equals(moduleType) ? "/pom.xml" : "/package.json"));
    Set<File> changed = new HashSet<>();
    List<String> artifactsChanged = new ArrayList<>();
    StringBuilder sb = new StringBuilder();
    for (Upgrade upgrade : upgrades) {
      LOG.info("Upgrading library in " + fileToChange.getCanonicalPath());
      Dependency d = upgrade.getLibrary().getDependency();
      LOG.info("  " + upgrade.getNewVersion() + " from " + d.getVersion() + " in " + d.getArtifactId());
      List<File> filesChanged;
      if ("maven".equals(moduleType)) {
        filesChanged = pomManipulator.changePom(upgrade.getNewVersion(), d, fileToChange);
      } else {
        filesChanged = packageManipulator.changePackage(upgrade.getNewVersion(), d, fileToChange);
      }
      changed.addAll(filesChanged);
      if (!filesChanged.isEmpty()) {
        artifactsChanged.add(d.getArtifactId());
      }
      sb.append(" - " + (!filesChanged.isEmpty() ? "✓ `" : "? `") + (d.getGroupId() != null ? d.getGroupId() + ":" : "") + d.getArtifactId() + ":" + d.getVersion() + " --> " + " " + upgrade.getNewVersion() + "`\n");
    }
    sb.insert(0, "### Library Upgrade\n\n");
    sb.append("\n------\nUpgrade brought to you by GroupBy <a href=\"https://github.com/groupby/dependency-checker\">dependency-checker</a>");
    LOG.info("Upgrade Info:\n" + sb.toString());
    if (changed.size() == 0) {
      return "No Files Updated";
    }
    return github.createPullRequest(company, projectName, changed, "Version Upgrade " + artifactsChanged.toString(), sb.toString());
  }
}
