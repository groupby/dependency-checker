package com.groupbyinc.maven;


import org.apache.commons.io.FileUtils;
import org.apache.maven.model.Dependency;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.aspectj.bridge.MessageUtil.fail;
import static org.junit.Assert.assertEquals;

public class PomManipulatorTest {

  @SuppressWarnings("ResultOfMethodCallIgnored")
  @Before
  public void before() throws IOException {
    FileUtils.deleteDirectory(new File("./target/rootPomDir"));
    
  }

  @Test
  public void testGroupIdInherited() throws IOException {
    String projectXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
        "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n" +
        "    <modelVersion>4.0.0</modelVersion>\n" +
        "    <parent>\n" +
        "        <!-- Your own application should inherit from spring-boot-starter-parent -->\n" +
        "        <groupId>org.springframework.boot</groupId>\n" +
        "        <artifactId>spring-boot-starter-parent</artifactId>\n" +
        "        <version>1.3.3.RELEASE ✓</version>\n" +
        "    </parent>\n" +
        "    <properties>\n" +
        "        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>\n" +
        "        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>\n" +
        "        <java.version>1.8</java.version>\n" +
        "        <groupbyinc.api.version>[2.2.57,3.0.0)</groupbyinc.api.version>\n" +
        "    </properties>" +
        "    <groupId>com.groupbyinc</groupId>\n" +
        "    <artifactId>quickstart-java</artifactId>\n" +
        "    <version>1.0.1</version>\n" +
        "    <packaging>war</packaging>\n" +
        "    <dependencies>\n" +
        "        <dependency>\n" +
        "            <groupId>${project.groupId}</groupId>\n" +
        "            <artifactId>api-java-flux</artifactId>\n" +
        "            <version>${groupbyinc.api.version}</version>\n" +
        "            <classifier>uber</classifier>\n" +
        "        </dependency>\n" +
        "        <dependency>\n" +
        "            <groupId>org.springframework.boot</groupId>\n" +
        "            <artifactId>spring-boot-starter-web</artifactId>\n" +
        "        </dependency>\n" +
        "    </dependencies>\n" +
        "</project>";

    File pomFile = new File("./target/rootPomDir/api/pom.xml");
    FileUtils.writeStringToFile(pomFile, projectXml, "UTF-8");

    PomManipulator test = new PomManipulator();
    Dependency d = new Dependency();
    d.setArtifactId("api-java-flux");
    d.setGroupId("com.groupbyinc");
    d.setVersion("[2.2.57,3.0.0)");
    List<File> changedFiles = test.changePom("4.1.13", d, pomFile);


    assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
        "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n" +
        "    <modelVersion>4.0.0</modelVersion>\n" +
        "    <parent>\n" +
        "        <!-- Your own application should inherit from spring-boot-starter-parent -->\n" +
        "        <groupId>org.springframework.boot</groupId>\n" +
        "        <artifactId>spring-boot-starter-parent</artifactId>\n" +
        "        <version>1.3.3.RELEASE ✓</version>\n" +
        "    </parent>\n" +
        "    <properties>\n" +
        "        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>\n" +
        "        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>\n" +
        "        <java.version>1.8</java.version>\n" +
        "        <groupbyinc.api.version>4.1.13</groupbyinc.api.version>\n" +
        "    </properties>    <groupId>com.groupbyinc</groupId>\n" +
        "    <artifactId>quickstart-java</artifactId>\n" +
        "    <version>1.0.1</version>\n" +
        "    <packaging>war</packaging>\n" +
        "    <dependencies>\n" +
        "        <dependency>\n" +
        "            <groupId>${project.groupId}</groupId>\n" +
        "            <artifactId>api-java-flux</artifactId>\n" +
        "            <version>${groupbyinc.api.version}</version>\n" +
        "            <classifier>uber</classifier>\n" +
        "        </dependency>\n" +
        "        <dependency>\n" +
        "            <groupId>org.springframework.boot</groupId>\n" +
        "            <artifactId>spring-boot-starter-web</artifactId>\n" +
        "        </dependency>\n" +
        "    </dependencies>\n" +
        "</project>", FileUtils.readFileToString(changedFiles.get(0), "UTF-8"));
  }

  @Test
  public void testUpdateParent() throws IOException {
    String projectXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
        "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n" +
        "    <modelVersion>4.0.0</modelVersion>\n" +
        "    <parent>\n" +
        "        <!-- Your own application should inherit from spring-boot-starter-parent -->\n" +
        "        <groupId>org.springframework.boot</groupId>\n" +
        "        <artifactId>spring-boot-starter-parent</artifactId>\n" +
        "        <version>1.3.3.RELEASE</version>\n" +
        "    </parent>\n" +
        "    <properties>\n" +
        "        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>\n" +
        "        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>\n" +
        "        <java.version>1.8</java.version>\n" +
        "        <groupbyinc.api.version>[2.2.57,3.0.0)</groupbyinc.api.version>\n" +
        "    </properties>" +
        "    <groupId>com.groupbyinc</groupId>\n" +
        "    <artifactId>quickstart-java</artifactId>\n" +
        "    <version>1.0.1</version>\n" +
        "    <packaging>war</packaging>\n" +
        "    <dependencies>\n" +
        "        <dependency>\n" +
        "            <groupId>${project.groupId}</groupId>\n" +
        "            <artifactId>api-java-flux</artifactId>\n" +
        "            <version>${groupbyinc.api.version}</version>\n" +
        "            <classifier>uber</classifier>\n" +
        "        </dependency>\n" +
        "        <dependency>\n" +
        "            <groupId>org.springframework.boot</groupId>\n" +
        "            <artifactId>spring-boot-starter-web</artifactId>\n" +
        "        </dependency>\n" +
        "    </dependencies>\n" +
        "</project>";

    File pomFile = new File("./target/rootPomDir/api/pom.xml");
    FileUtils.writeStringToFile(pomFile, projectXml, "UTF-8");

    PomManipulator test = new PomManipulator();
    Dependency d = new Dependency();
    d.setArtifactId("spring-boot-starter-web");
    d.setGroupId("org.springframework.boot");
    d.setVersion("1.3.3.RELEASE");
    List<File> changedFiles = test.changePom("1.3.5.RELEASE", d, pomFile);


    assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
        "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n" +
        "    <modelVersion>4.0.0</modelVersion>\n" +
        "    <parent>\n" +
        "        <!-- Your own application should inherit from spring-boot-starter-parent -->\n" +
        "        <groupId>org.springframework.boot</groupId>\n" +
        "        <artifactId>spring-boot-starter-parent</artifactId>\n" +
        "        <version>1.3.5.RELEASE</version>\n" +
        "    </parent>\n" +
        "    <properties>\n" +
        "        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>\n" +
        "        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>\n" +
        "        <java.version>1.8</java.version>\n" +
        "        <groupbyinc.api.version>[2.2.57,3.0.0)</groupbyinc.api.version>\n" +
        "    </properties>    <groupId>com.groupbyinc</groupId>\n" +
        "    <artifactId>quickstart-java</artifactId>\n" +
        "    <version>1.0.1</version>\n" +
        "    <packaging>war</packaging>\n" +
        "    <dependencies>\n" +
        "        <dependency>\n" +
        "            <groupId>${project.groupId}</groupId>\n" +
        "            <artifactId>api-java-flux</artifactId>\n" +
        "            <version>${groupbyinc.api.version}</version>\n" +
        "            <classifier>uber</classifier>\n" +
        "        </dependency>\n" +
        "        <dependency>\n" +
        "            <groupId>org.springframework.boot</groupId>\n" +
        "            <artifactId>spring-boot-starter-web</artifactId>\n" +
        "        </dependency>\n" +
        "    </dependencies>\n" +
        "</project>", FileUtils.readFileToString(changedFiles.get(0), "UTF-8"));
  }

  @Test
  public void testNoDependencies() throws IOException {
    File pomFile = new File("./target/rootPomDir/api/pom.xml");
    FileUtils.writeStringToFile(pomFile, "<project>" +
        "<properties>" +
        "<junit.version>4.1.12</junit.version>" +
        "</properties>" +
        "</project>", "UTF-8");
    PomManipulator test = new PomManipulator();
    Dependency d = new Dependency();
    d.setArtifactId("junit");
    d.setGroupId("junit");
    d.setVersion("4.1.12");
    List<File> changedFiles = test.changePom("4.1.13", d, pomFile);

    assertEquals(0, changedFiles.size());
  }

  @Test
  public void testConversion() throws IOException {
    
    File pomFile = new File("./target/rootPomDir/api/pom.xml");
    FileUtils.writeStringToFile(pomFile, "<project>" +
        "<properties>" +
        "<junit.version>4.1.12</junit.version>" +
        "</properties>" +
        "<dependencies>" +
        "    <dependency>\n" +
        "      <groupId>junit</groupId>\n" +
        "      <artifactId>junit</artifactId>\n" +
        "      <version>4.1.12</version>\n" +
        "      <scope>test</scope>\n" +
        "    </dependency>" +
        "</dependencies>" +
        "</project>", "UTF-8");
    PomManipulator test = new PomManipulator();
    Dependency d = new Dependency();
    d.setArtifactId("junit");
    d.setGroupId("junit");
    d.setVersion("4.1.12");
    List<File> changedFiles = test.changePom("4.1.13", d, pomFile);

    assertEquals(1, changedFiles.size());
    assertEquals("<project><properties><junit.version>4.1.12</junit.version></properties><dependencies>    <dependency>\n" +
        "      <groupId>junit</groupId>\n" +
        "      <artifactId>junit</artifactId>\n" +
        "      <version>4.1.13</version>\n" +
        "      <scope>test</scope>\n" +
        "    </dependency></dependencies></project>", FileUtils.readFileToString(pomFile, "UTF-8"));
  }

  @Test
  public void testConversionMultiple() throws IOException {
    
    File pomFile = new File("./target/rootPomDir/api/pom.xml");
    FileUtils.writeStringToFile(pomFile, "<project>" +
        "<properties>" +
        "<junit.version>4.1.12</junit.version>" +
        "</properties>" +
        "<dependencies>" +
        "    <dependency>\n" +
        "      <groupId>junit</groupId>\n" +
        "      <artifactId>junit</artifactId>\n" +
        "      <version>4.1.12</version>\n" +
        "      <scope>test</scope>\n" +
        "    </dependency>" +
        "    <dependency>\n" +
        "      <groupId>junit</groupId>\n" +
        "      <artifactId>junit</artifactId>\n" +
        "      <version>4.1.15</version>\n" +
        "      <scope>test</scope>\n" +
        "    </dependency>" +
        "</dependencies>" +
        "</project>", "UTF-8");
    PomManipulator test = new PomManipulator();
    Dependency d = new Dependency();
    d.setArtifactId("junit");
    d.setGroupId("junit");
    d.setVersion("4.1.12");
    List<File> changedFiles = test.changePom("4.1.13", d, pomFile);

    assertEquals(1, changedFiles.size());
    assertEquals("<project>" +
        "<properties>" +
        "<junit.version>4.1.12</junit.version>" +
        "</properties>" +
        "<dependencies>" +
        "    <dependency>\n" +
        "      <groupId>junit</groupId>\n" +
        "      <artifactId>junit</artifactId>\n" +
        "      <version>4.1.13</version>\n" +
        "      <scope>test</scope>\n" +
        "    </dependency>" +
        "    <dependency>\n" +
        "      <groupId>junit</groupId>\n" +
        "      <artifactId>junit</artifactId>\n" +
        "      <version>4.1.15</version>\n" +
        "      <scope>test</scope>\n" +
        "    </dependency>" +
        "</dependencies>" +
        "</project>", FileUtils.readFileToString(pomFile, "UTF-8"));
  }

  @Test
  public void testConversionMultipleTheSame() throws IOException {
    
    File pomFile = new File("./target/rootPomDir/api/pom.xml");
    FileUtils.writeStringToFile(pomFile, "<project>" +
        "<properties>" +
        "<junit.version>4.1.12</junit.version>" +
        "</properties>" +
        "<dependencies>" +
        "    <dependency>\n" +
        "      <groupId>junit</groupId>\n" +
        "      <artifactId>junit</artifactId>\n" +
        "      <version>4.1.12</version>\n" +
        "      <scope>test</scope>\n" +
        "    </dependency>" +
        "    <dependency>\n" +
        "      <groupId>junit</groupId>\n" +
        "      <artifactId>junit</artifactId>\n" +
        "      <version>4.1.12</version>\n" +
        "      <scope>test</scope>\n" +
        "    </dependency>" +
        "</dependencies>" +
        "</project>", "UTF-8");
    PomManipulator test = new PomManipulator();
    Dependency d = new Dependency();
    d.setArtifactId("junit");
    d.setGroupId("junit");
    d.setVersion("4.1.12");
    List<File> changedFiles = test.changePom("4.1.13", d, pomFile);

    assertEquals(1, changedFiles.size());
    assertEquals("<project>" +
        "<properties>" +
        "<junit.version>4.1.12</junit.version>" +
        "</properties>" +
        "<dependencies>" +
        "    <dependency>\n" +
        "      <groupId>junit</groupId>\n" +
        "      <artifactId>junit</artifactId>\n" +
        "      <version>4.1.13</version>\n" +
        "      <scope>test</scope>\n" +
        "    </dependency>" +
        "    <dependency>\n" +
        "      <groupId>junit</groupId>\n" +
        "      <artifactId>junit</artifactId>\n" +
        "      <version>4.1.13</version>\n" +
        "      <scope>test</scope>\n" +
        "    </dependency>" +
        "</dependencies>" +
        "</project>", FileUtils.readFileToString(pomFile, "UTF-8"));
  }

  @Test
  public void testConversionToken() throws IOException {
    
    File pomFile = new File("./target/rootPomDir/api/pom.xml");
    FileUtils.writeStringToFile(pomFile, "<project>" +
        "<properties>" +
        "<junit.version>4.1.12</junit.version>" +
        "</properties>" +
        "<dependencies>" +
        "    <dependency>\n" +
        "      <groupId>junit</groupId>\n" +
        "      <artifactId>junit</artifactId>\n" +
        "      <version>4.1.12</version>\n" +
        "      <scope>test</scope>\n" +
        "    </dependency>" +
        "    <dependency>\n" +
        "      <groupId>junit</groupId>\n" +
        "      <artifactId>junit</artifactId>\n" +
        "      <version>${junit.version}</version>\n" +
        "      <scope>test</scope>\n" +
        "    </dependency>" +
        "</dependencies>" +
        "</project>", "UTF-8");
    PomManipulator test = new PomManipulator();
    Dependency d = new Dependency();
    d.setArtifactId("junit");
    d.setGroupId("junit");
    d.setVersion("4.1.12");
    List<File> changedFiles = test.changePom("4.1.13", d, pomFile);

    assertEquals(1, changedFiles.size());
    assertEquals("<project>" +
        "<properties>" +
        "<junit.version>4.1.13</junit.version>" +
        "</properties>" +
        "<dependencies>" +
        "    <dependency>\n" +
        "      <groupId>junit</groupId>\n" +
        "      <artifactId>junit</artifactId>\n" +
        "      <version>4.1.13</version>\n" +
        "      <scope>test</scope>\n" +
        "    </dependency>" +
        "    <dependency>\n" +
        "      <groupId>junit</groupId>\n" +
        "      <artifactId>junit</artifactId>\n" +
        "      <version>${junit.version}</version>\n" +
        "      <scope>test</scope>\n" +
        "    </dependency>" +
        "</dependencies>" +
        "</project>", FileUtils.readFileToString(pomFile, "UTF-8"));
  }

  @Test
  public void testParentToken() throws IOException {
    
    File pomFile = new File("./target/rootPomDir/api/pom.xml");
    File parentPomFile = new File("./target/rootPomDir/pom.xml");
    FileUtils.writeStringToFile(pomFile, "<project>" +
        "<dependencies>" +
        "    <dependency>\n" +
        "      <groupId>junit</groupId>\n" +
        "      <artifactId>junit</artifactId>\n" +
        "      <version>4.1.12  </version>\n" +
        "      <scope>test</scope>\n" +
        "    </dependency>" +
        "    <dependency>\n" +
        "      <groupId>junit</groupId>\n" +
        "      <artifactId>junit</artifactId>\n" +
        "      <version>${junit.version  }" +
        "\n" +
        "\n" +
        "</version>\n" +
        "      <scope>test</scope>\n" +
        "    </dependency>" +
        "</dependencies>" +
        "</project>", "UTF-8");

    FileUtils.writeStringToFile(parentPomFile, "<project>" +
        "<properties>" +
        "<junit.version>4.1.12</junit.version>" +
        "</properties>" +
        "</project>", "UTF-8");
    PomManipulator test = new PomManipulator();
    Dependency d = new Dependency();
    d.setArtifactId("junit");
    d.setGroupId("junit");
    d.setVersion("4.1.12");
    List<File> changedFiles = test.changePom("4.1.13", d, pomFile);

    assertEquals(2, changedFiles.size());
    assertEquals("<project>" +
        "<dependencies>" +
        "    <dependency>\n" +
        "      <groupId>junit</groupId>\n" +
        "      <artifactId>junit</artifactId>\n" +
        "      <version>4.1.13</version>\n" +
        "      <scope>test</scope>\n" +
        "    </dependency>" +
        "    <dependency>\n" +
        "      <groupId>junit</groupId>\n" +
        "      <artifactId>junit</artifactId>\n" +
        "      <version>${junit.version  }\n" +
        "\n" +
        "</version>\n" +
        "      <scope>test</scope>\n" +
        "    </dependency>" +
        "</dependencies>" +
        "</project>", FileUtils.readFileToString(pomFile, "UTF-8"));

    assertEquals("<project>" +
        "<properties><junit.version>4.1.13</junit.version></properties>" +
        "</project>", FileUtils.readFileToString(parentPomFile, "UTF-8"));
  }




}
