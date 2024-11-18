package com.bio.entity;

public class SolutionMaterial {
    Long material;
    String unit;
    Double count;
    String apply;

    public Long getMaterial() {
        return material;
    }

    public void setMaterial(Long material) {
        this.material = material;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Double getCount() {
        return count;
    }

    public void setCount(Double count) {
        this.count = count;
    }

    public String getApply() {
        return apply;
    }

    public void setApply(String apply) {
        this.apply = apply;
    }
}
