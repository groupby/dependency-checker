package com.groupbyinc.github;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.kohsuke.github.*;
import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

public class GitHub {


  private static final transient Logger LOG = LoggerFactory.getLogger(GitHub.class);

  public String rootFolder ;
  private final String token;

  public GitHub(String token) {
    this(token, "/tmp/dependency-checker/");
  }
  public GitHub(String token, String rootFolder) {
    this.token = token;
    this.rootFolder = rootFolder;
  }

  public List<String> getRepositories(String company, List<String> excludes) throws IOException {
    if (excludes == null) {
      excludes = new ArrayList<>();
    }
    if (StringUtils.isBlank(company)) {
      throw new IllegalStateException("Company must be set");
    }
    InputStream in = null;
    List<String> repos = new ArrayList<>();
    try {
      URL url = new URL("https://api.github.com/orgs/" + company + "/repos?per_page=100");
      HttpURLConnection uc = (HttpURLConnection) url.openConnection();
      uc.setRequestMethod("GET");
      uc.setRequestProperty("Authorization", "token " + token);
      uc.setUseCaches(false);
      uc.setDoInput(true);
      in = uc.getInputStream();
      String s = IOUtils.toString(in);
      List<Map> response = new ObjectMapper().readValue(s, List.class);
      for (Map map : response) {
        String name = (String) map.get("name");
        if (!"true".equals(map.get("fork")) && !excludes.contains(name)) {
          repos.add(name);
        }
      }
      return repos;
    } finally {
      IOUtils.closeQuietly(in);
    }
  }

  public String createPullRequest(String company, String project, Set<File> modified, String message, String markdown) throws IOException {
    org.kohsuke.github.GitHub github = new GitHubBuilder().withOAuthToken(token).build();
    File rootProjectFolder = new File(rootFolder + company + "/" + project);
    File[] dirs = rootProjectFolder.listFiles(File::isDirectory);
    String rootGitFolder = dirs[0].getCanonicalPath();
    GHRepository repository = github.getRepository(company + "/" + project);
    String defaultBranch = repository.getDefaultBranch();
    GHRef defaultBranchRef = repository.getRef("heads/" + defaultBranch);
    Date now = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
    String branchName = "versionchange" + sdf.format(now);
    GHRef ref = repository.createRef("refs/heads/" + branchName, defaultBranchRef.getObject().getSha());
    for (File file : modified) {
      LOG.info("Modified: " + file.getCanonicalPath());
    }
    for (File file : modified) {
      String newContents = FileUtils.readFileToString(file, "UTF-8");
      String filePath = file.getCanonicalPath();
      filePath = filePath.substring(rootGitFolder.length() + 1);
      LOG.info("modifying file: " + filePath + " on branch: " + branchName);

      GHContent fileContent = repository.getFileContent(filePath, ref.getRef());
      GHContentUpdateResponse content = fileContent.update(newContents, "Update: " + filePath, branchName);
      GHCommit commit = content.getCommit();
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
      }
      commit.listStatuses();
    }

    GHPullRequest pullRequest = repository.createPullRequest(message, branchName, defaultBranch, markdown);
    return pullRequest.getHtmlUrl().toString();

  }

  public void checkoutRaw(String company, String project, File rootProjectFolder) throws IOException {
    InputStream in = null;
    FileOutputStream out = null;
    try {
      FileUtils.deleteDirectory(rootProjectFolder);
      URL url = new URL("https://api.github.com/repos/" + company + "/" + project + "/tarball");
      HttpURLConnection uc = (HttpURLConnection) url.openConnection();
      uc.setRequestMethod("GET");
      uc.setRequestProperty("Authorization", "token " + token);
      uc.setUseCaches(false);
      uc.setDoInput(true);
      uc.setDoOutput(true);
      in = uc.getInputStream();
      File sourceFile = new File("/tmp/dependency-checker/" + company + "." + project + ".tar.gz");
      out = new FileOutputStream(sourceFile);
      IOUtils.copy(in, out);
      Archiver archiver = ArchiverFactory.createArchiver("tar", "gz");
      archiver.extract(sourceFile, rootProjectFolder);
    } finally {
      IOUtils.closeQuietly(in);
      IOUtils.closeQuietly(out);
    }
  }

  public String getRootFolder() {
    return rootFolder;
  }

  public void setRootFolder(String rootFolder) {
    this.rootFolder = rootFolder;
  }

  public String getToken() {
    return token;
  }
}
