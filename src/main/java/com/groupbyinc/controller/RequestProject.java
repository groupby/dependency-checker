package com.groupbyinc.controller;

import com.groupbyinc.versioning.Upgrade;

import java.util.ArrayList;
import java.util.List;

public class RequestProject {

  private String company;
  private List<String> excludes;
  private List<String> includes;
  private String projectName;
  private String moduleName;
  private List<Upgrade> upgrades = new ArrayList<>();
  private String moduleType;


  public String getCompany() {
    return company;
  }

  public void setCompany(String company) {
    this.company = company;
  }

  public List<String> getExcludes() {
    return excludes;
  }

  public void setExcludes(List<String> excludes) {
    this.excludes = excludes;
  }

  public String getProjectName() {
    return projectName;
  }

  public void setProjectName(String projectName) {
    this.projectName = projectName;
  }

  public String getModuleName() {
    return moduleName;
  }

  public void setModuleName(String moduleName) {
    this.moduleName = moduleName;
  }

  public List<Upgrade> getUpgrades() {
    return upgrades;
  }

  public void setUpgrades(List<Upgrade> upgrades) {
    this.upgrades = upgrades;
  }

  public String getModuleType() {
    return moduleType;
  }

  public void setModuleType(String moduleType) {
    this.moduleType = moduleType;
  }

  public List<String> getIncludes() {
    return includes;
  }

  public void setIncludes(List<String> includes) {
    this.includes = includes;
  }
}
