package com.bio.entity;

public class Feed {
    Long id;
    Solution solution;
    String apply;
    Long organism;
    String purpose;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Solution getSolution() {
        return solution;
    }

    public void setSolution(Solution solution) {
        this.solution = solution;
    }

    public String getApply() {
        return apply;
    }

    public void setApply(String apply) {
        this.apply = apply;
    }

    public Long getOrganism() {
        return organism;
    }

    public void setOrganism(Long organism) {
        this.organism = organism;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }
}
