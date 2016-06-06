package com.groupbyinc.versioning;

import java.util.HashSet;
import java.util.Set;


public class Module {
    private Set<Library> libraries = new HashSet<>();
    private String name;
    private String error;
    private String type;

    public Set<Library> getLibraries() {
        return libraries;
    }

    public void setLibraries(Set<Library> libraries) {
        this.libraries = libraries;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
