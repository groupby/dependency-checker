package com.groupbyinc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.groupbyinc.github.GitHub;
import com.groupbyinc.manager.ProjectManager;
import com.groupbyinc.versioning.Project;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.maven.model.building.ModelBuildingException;
import org.apache.maven.project.ProjectBuildingException;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.sonatype.aether.resolution.DependencyResolutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ProjectController {

  private static final transient Logger LOG = Logger.getLogger(ProjectController.class);
  private ProjectManager projectManager;


  @Autowired
  private ObjectMapper objectMapper = null;

  @Autowired
  public ProjectController(ProjectManager projectManager) {
    this.projectManager = projectManager;
  }


  @RequestMapping(value = "project/{token}", method = {RequestMethod.POST}, consumes = "application/json;charset=UTF-8")
  public List<Project> createProject(HttpServletResponse response, @PathVariable String token,
                                     @RequestBody RequestProject requestProject) throws IOException, XmlPullParserException, DependencyResolutionException, ModelBuildingException, ProjectBuildingException {
    LOG.info("Request to create project: (" + requestProject.getCompany() + ", " + requestProject.getExcludes() + ")");
    if (StringUtils.isEmpty(token)) {
      throw new IllegalStateException("Token must be set");
    }
    return projectManager.createProject(token, requestProject.getCompany(), requestProject.getExcludes(), requestProject.getIncludes());
  }

  @RequestMapping(value = "project/{token}", method = {RequestMethod.GET})
  public List<Project> getProject(HttpServletResponse response, @PathVariable String token) throws IOException, XmlPullParserException, DependencyResolutionException, ModelBuildingException, ProjectBuildingException {
    LOG.debug("Request to get project");
    if (StringUtils.isEmpty(token)) {
      throw new IllegalStateException("Token must be set");
    }
    return projectManager.getProject(token);
  }

  @RequestMapping(value = "project/{token}", method = {RequestMethod.PUT})
  public Map createPullRequest(HttpServletResponse response, @PathVariable String token,
                               @RequestBody RequestProject requestProject) throws IOException {
    LOG.info("Request: " + objectMapper.writeValueAsString(requestProject));
    String prUrl = projectManager.createPullRequest(new GitHub(token),
        requestProject.getCompany(),
        requestProject.getProjectName(),
        requestProject.getModuleName(),
        requestProject.getUpgrades(),
        requestProject.getModuleType());
    Map<String, String> results = new HashMap<>();
    results.put("pullRequest", prUrl);
    return results;
  }


}
