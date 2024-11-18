package com.bio;

public class Program {
    public String version;
    public Boolean portable;

    public Program() {
        version = "${project.version}";
    }
}
