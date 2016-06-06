package com.groupbyinc.npm;

import org.apache.commons.io.FileUtils;
import org.apache.maven.model.Dependency;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class PackageManipulatorTest {


  @SuppressWarnings("ResultOfMethodCallIgnored")
  @Before
  public void before() throws IOException {
    FileUtils.deleteDirectory(new File("./target/rootPackageDir"));
    new File("./target/rootPackageDir").mkdirs();
  }

  @Test
  public void chillasticTest() throws IOException {
    String textToChange = "{\n" +
        "  \"name\": \"groupby-api\",\n" +
        " \"license\": \"MIT\",\n" +
        "  \"devDependencies\": {\n" +
        "    \"supertest-as-promised\": \"^3.1.0\"\n" +
        "  },\n" +
        "  \"dependencies\": {\n" +
        "    \"bluebird\": \"^3.3.5\",\n" +
        "    \"cookie-parser\": \"^1.4.1\"\n" +
        "  }\n" +
        "}";
    File packageJson = new File("./target/rootPackageDir/package.json");
    FileUtils.writeStringToFile(packageJson, textToChange);

    Dependency d = new Dependency();
    d.setArtifactId("cookie-parser");
    d.setVersion("^1.4.1");
    List<File> files = new PackageManipulator().changePackage("^1.4.3", d, packageJson);
    assertEquals(     "{\n" +
        "  \"name\": \"groupby-api\",\n" +
        " \"license\": \"MIT\",\n" +
        "  \"devDependencies\": {\n" +
        "    \"supertest-as-promised\": \"^3.1.0\"\n" +
        "  },\n" +
        "  \"dependencies\": {\n" +
        "    \"bluebird\": \"^3.3.5\",\n" +
        "    \"cookie-parser\": \"^1.4.3\"\n" +
        "  }\n" +
        "}", FileUtils.readFileToString(files.get(0)));

  }

  @Test
  public void changePackage() throws Exception {
    String textToChange = "{\n" +
        "  \"name\": \"groupby-api\",\n" +
        "  \"devDependencies\": {\n" +
        "    \"typescript\": \"^1.5.3\"\n" +
        "  },\n" +
        "  \"dependencies\": {\n" +
        "    \"qs\": \"^6.1.0\"\n" +
        "  }\n" +
        "}";
    File packageJson = new File("./target/rootPackageDir/package.json");
    FileUtils.writeStringToFile(packageJson, textToChange);

    Dependency d = new Dependency();
    d.setArtifactId("typescript");
    d.setVersion("^1.5.3");
    List<File> files = new PackageManipulator().changePackage("~1.5.8", d, packageJson);
    assertEquals(     "{\n" +
        "  \"name\": \"groupby-api\",\n" +
        "  \"devDependencies\": {\n" +
        "    \"typescript\": \"~1.5.8\"\n" +
        "  },\n" +
        "  \"dependencies\": {\n" +
        "    \"qs\": \"^6.1.0\"\n" +
        "  }\n" +
        "}", FileUtils.readFileToString(files.get(0)));
  }

}