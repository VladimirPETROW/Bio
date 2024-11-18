package com.bio.entity;

import java.util.List;

public class Solution {
    Long id;
    String name;
    List<SolutionReactive> reactives;
    List<SolutionMaterial> materials;
    List<SolutionRef> refs;
    String apply;

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

    public List<SolutionReactive> getReactives() {
        return reactives;
    }

    public void setReactives(List<SolutionReactive> reactives) {
        this.reactives = reactives;
    }

    public List<SolutionMaterial> getMaterials() {
        return materials;
    }

    public void setMaterials(List<SolutionMaterial> materials) {
        this.materials = materials;
    }

    public List<SolutionRef> getRefs() {
        return refs;
    }

    public void setRefs(List<SolutionRef> refs) {
        this.refs = refs;
    }

    public String getApply() {
        return apply;
    }

    public void setApply(String apply) {
        this.apply = apply;
    }
}
