# Dependency Checker 

For a list of projects on github:

1. Downloads the projects.
2. Recurses into every subdirectory looking for pom.xml or package.json files.
3. Inspects the files for stale versions and creates a dashboard showing how out of date your projects are.
4. Clicking on a project allows you to select the versions you wish to update and creates a pull request in that repository.

Currently supports Maven projects that use a pom.xml and JavaScript projects that use a package.json. 

Getting Started (Docker)
----

Run the following command

```bash
docker run -dp 8080:8080 groupbyinc/dependency-checker:1.0-SNAPSHOT
```

Open a browser: <a href="http://localhost:8080/" target="_blank">localhost:8080</a>

Getting Started (Maven)
---

Clone the project and run.

```bash
git clone git@github.com:groupby/dependency-checker
cd dependency-checker
mvn spring-boot:run
```

What it looks like
----

### Dashboard 

We ran the tool on the splunk public repositories.

![dashboard](/src/main/resources/static/images/dependency-checker1.png)

### Creating a pull request

Pull requests can be created to create patched versions of your files. 
The tool tries really hard to create the smallest amount of change to your source files.  

![PR1](/src/main/resources/static/images/dependency-checker2.png)

Chose your version changes and create a pull request
![PR2](/src/main/resources/static/images/dependency-checker3.png)

