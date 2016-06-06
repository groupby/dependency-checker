package com.groupbyinc.versioning;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.groupbyinc.controller.RequestProject;
import org.apache.maven.model.Dependency;
import org.junit.Test;

import java.io.IOException;


public class UpgradeTest {

  @Test
  public void upgradeTest() throws IOException {
    ObjectMapper om = new ObjectMapper();
    om.addMixIn(Dependency.class, IgnoreOptional.class);
    om.readValue("{}", RequestProject.class);
  }

  private class IgnoreOptional {
    @JsonIgnore
    public void setOptional(String bob){

    }
  }
}
