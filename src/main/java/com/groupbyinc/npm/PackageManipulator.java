package com.groupbyinc.npm;

import org.apache.commons.io.FileUtils;
import org.apache.maven.model.Dependency;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Collections.singletonList;

public class PackageManipulator {


  public List<File> changePackage(String newVersion, Dependency d, File packageFile) throws IOException {
    String contents = FileUtils.readFileToString(packageFile, "UTF-8");
    Pattern pattern = Pattern.compile("\"\\s*" + Pattern.quote(d.getArtifactId()) + "\\s*\"\\s*:\\s*\"\\s*" + Pattern.quote(d.getVersion()) + "\\s*\"");
    Matcher matcher = pattern.matcher(contents);
    if (matcher.find()) {
      contents = contents.replaceAll(
          pattern.pattern(),
          "\"" + Matcher.quoteReplacement(d.getArtifactId()) + "\": \"" + Matcher.quoteReplacement(newVersion) + "\"");
      FileUtils.writeStringToFile(packageFile, contents, "UTF-8");
      return singletonList(packageFile);
    }
    return new ArrayList<>();
  }
}