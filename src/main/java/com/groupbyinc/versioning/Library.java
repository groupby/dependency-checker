package com.groupbyinc.versioning;

import org.apache.maven.model.Dependency;

import java.util.ArrayList;
import java.util.List;

public class Library {
    private List<String> newerVersions = new ArrayList<>();
    private Dependency dependency;
    private boolean inherited = false;

    public Library(){}

    public Library(Dependency dependency) {
        this.dependency = dependency;
    }

    public List<String> getNewerVersions() {
        return newerVersions;
    }

    public void setNewerVersions(List<String> newerVersions) {
        this.newerVersions = newerVersions;
    }

    public Dependency getDependency() {
        return dependency;
    }

    public void setDependency(Dependency dependency) {
        this.dependency = dependency;
    }

    public boolean isInherited() {
        return inherited;
    }

    public void setInherited(boolean inherited) {
        this.inherited = inherited;
    }

    @Override
    public int hashCode() {
        return dependency.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Library){
            return ((Library)obj).getDependency().getManagementKey().equals(this.getDependency().getManagementKey());
        }
        return false;
    }

    @Override
    public String toString() {
        return dependency.getManagementKey();
    }
}
