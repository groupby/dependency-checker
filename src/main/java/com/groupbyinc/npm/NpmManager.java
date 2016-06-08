package com.groupbyinc.npm;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;

import static java.util.Arrays.asList;

@Component
public class NpmManager {

  private String npmCheckUpdatesBinary;

  @Autowired
  public NpmManager(@Value("${npm.check.updates.binary}") String npmCheckUpdatesBinary) {
    this.npmCheckUpdatesBinary = npmCheckUpdatesBinary;
  }

  public String getDependencyJson(File dir, String rawJson) throws Exception {
    String[] cmd = new String[]{ "/bin/bash", "-c", "export PATH=$PATH:/usr/bin && " + npmCheckUpdatesBinary + " --packageFile package.json | grep →"};
    ProcessBuilder builder = new ProcessBuilder();
    builder.directory(dir);
    builder.command(cmd);
    builder.redirectErrorStream(true);
    BufferedReader r = null;
    try {
      Process p = builder.start();

      r = new BufferedReader(new InputStreamReader(p.getInputStream()));
      OutputStream o = p.getOutputStream();
      String line;
      StringBuilder output = new StringBuilder();
      o.write(rawJson.getBytes());
      while ((line = r.readLine()) != null && p.isAlive()) {
        output.append(line)
            .append("\n");
      }
      p.waitFor();

      int exitValue = p.exitValue();
      if (exitValue != 0) {
        throw new Exception("Non zero exit code (" + exitValue + "): " + asList(cmd) + "\noutput: " + output.toString());
      }
      return output.toString();
    } finally {
      IOUtils.closeQuietly(r);
    }
  }


}
