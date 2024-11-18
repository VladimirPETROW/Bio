package com.bio.entity;

public class Organism {
    Long id;
    String name;
    String kind;
    Double doubling;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public Double getDoubling() {
        return doubling;
    }

    public void setDoubling(Double doubling) {
        this.doubling = doubling;
    }
}
