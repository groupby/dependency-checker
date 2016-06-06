package com.groupbyinc.maven;

import org.apache.commons.io.FileUtils;
import org.apache.maven.model.Dependency;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Matcher.quoteReplacement;
import static java.util.regex.Pattern.quote;

public class PomManipulator {

  private static final Pattern VERSION_TOKEN = Pattern.compile("<version>\\s*\\$\\{(.*)\\}\\s*</version>");

  public List<File> changePom(String newVersion, Dependency d, File pomFile) throws IOException {
    List<File> changed = new ArrayList<>();
    changePom(newVersion, d, pomFile, new ArrayList<>(), changed);

    return changed;
  }

  private void changePom(String newVersion, Dependency d, File pomFile, List<String> tokens, List<File> changed) throws IOException {
    String contents = FileUtils.readFileToString(pomFile, "UTF-8");
    int place = 0;
    boolean fileModified = false;
    while (place != -1) {
      int artifactStart = contents.indexOf(d.getArtifactId(), place + d.getArtifactId().length());
      if (artifactStart == -1) break;
      int endDependency = contents.indexOf("</dependency>", artifactStart);
      if (endDependency == -1) {
        break;
      }
      int startDependency = contents.substring(0, endDependency).lastIndexOf("<dependency>");
      if (startDependency == -1) {
        break;
      }
      String depContents = contents.substring(startDependency, endDependency);

      if (hasArtifactId(d, depContents) && hasGroupId(d, depContents) && hasVersion(d, depContents)) {
        contents = contents.replace(depContents, depContents.replaceAll("<version>[^>]+</version>", "<version>" + newVersion + "</version>"));
        fileModified = true;
      }
      if (hasArtifactId(d, depContents) && hasGroupId(d, depContents) && hasToken(depContents)) {
        tokens.add(getToken(depContents));
      }
      if (hasArtifactId(d, depContents) && hasGroupId(d, depContents) && hasNoVersion(depContents)) {
        if (parentDefinitionHasVersion(contents, d)) {
          fileModified = true;
          contents = updateParentDefinition(contents, newVersion);
        }
      }
      place = artifactStart;
    }

    if (!tokens.isEmpty()) {
      for (String token : tokens) {
        if (findPattern(token, contents, d.getVersion())) {
          contents = contents.replaceAll("<" + quote(token) + ">\\s*" + quote(d.getVersion()) + "\\s*</" + quote(token) + ">",
              "<" + quoteReplacement(token) + ">" + quoteReplacement(newVersion) + "</" + quoteReplacement(token) + ">");
          fileModified = true;
        }
      }
    }

    if (fileModified) {
      changed.add(pomFile);
      FileUtils.writeStringToFile(pomFile, contents, "UTF-8");
    }

    File parentPom = new File(pomFile.getParentFile().getParentFile().getCanonicalPath() + "/pom.xml");

    if (parentPom.exists()) {
      changePom(newVersion, d, parentPom, tokens, changed);
    }
  }

  private String updateParentDefinition(String contents, String newVersion) {
    String pContents = getParentContents(contents);

    contents = contents.replace(pContents, pContents.replaceAll("<version>[^>]+</version>", "<version>" + newVersion + "</version>"));
    return contents;
  }

  private boolean parentDefinitionHasVersion(String contents, Dependency d) {
    String pContents = getParentContents(contents);

    return pContents == null ? false : hasVersion(d, pContents);

  }

  private String getParentContents(String contents) {
    String pContents = null;
    int pStart = contents.indexOf("<parent>");
    int pEnd = contents.indexOf("</parent>");
    if (pStart != -1 && pEnd != -1) {
      pContents = contents.substring(pStart, pEnd);
    }
    return pContents;
  }

  private boolean hasNoVersion(String depContents) {
    return !depContents.contains("<version>");
  }

  private boolean hasGroupId(Dependency d, String contents) {
    return findPattern("groupId", contents, d.getGroupId()) || findPattern("groupId", contents, "${project.groupId}");
  }

  private boolean hasArtifactId(Dependency d, String contents) {
    return findPattern("artifactId", contents, d.getArtifactId());
  }

  private boolean hasVersion(Dependency d, String contents) {
    return findPattern("version", contents, d.getVersion());
  }


  private boolean findPattern(String token, String contents, String v) {
    Pattern p = Pattern.compile("<" + quote(token) + ">\\s*" + quote(v) + "\\s*</" + quote(token) + ">");
    return p.matcher(contents).find();
  }

  private boolean hasToken(String dependency) {
    return VERSION_TOKEN.matcher(dependency).find();
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  private String getToken(String dependency) {
    Matcher matcher = VERSION_TOKEN.matcher(dependency);
    matcher.find();
    String token = matcher.group(1);
    return token.trim();
  }
}
