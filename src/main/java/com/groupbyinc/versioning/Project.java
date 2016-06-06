package com.groupbyinc.versioning;

import java.util.ArrayList;
import java.util.List;

public class Project {

    private String company;
    private String name ;
    private List<Module> modules = new ArrayList<>();


    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Module> getModules() {
        return modules;
    }

    public void setModules(List<Module> modules) {
        this.modules = modules;
    }
}
