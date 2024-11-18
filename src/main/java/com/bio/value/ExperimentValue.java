package com.bio.value;

import com.bio.json.LocalDataTimeDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.time.LocalDateTime;

public class ExperimentValue {
    Long organism;
    Long feed;
    @JsonDeserialize(using = LocalDataTimeDeserializer.class)
    LocalDateTime fermentBegin;
    @JsonDeserialize(using = LocalDataTimeDeserializer.class)
    LocalDateTime fermentEnd;
    Double speed;
    Double temperature;
    Double ph;
    Double whole;
    Double product;
    Double koe;
    String comment;

    public Long getOrganism() {
        return organism;
    }

    public void setOrganism(Long organism) {
        this.organism = organism;
    }

    public Long getFeed() {
        return feed;
    }

    public void setFeed(Long feed) {
        this.feed = feed;
    }

    public LocalDateTime getFermentBegin() {
        return fermentBegin;
    }

    public void setFermentBegin(LocalDateTime fermentBegin) {
        this.fermentBegin = fermentBegin;
    }

    public LocalDateTime getFermentEnd() {
        return fermentEnd;
    }

    public void setFermentEnd(LocalDateTime fermentEnd) {
        this.fermentEnd = fermentEnd;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Double getPh() {
        return ph;
    }

    public void setPh(Double ph) {
        this.ph = ph;
    }

    public Double getWhole() {
        return whole;
    }

    public void setWhole(Double whole) {
        this.whole = whole;
    }

    public Double getProduct() {
        return product;
    }

    public void setProduct(Double product) {
        this.product = product;
    }

    public Double getKoe() {
        return koe;
    }

    public void setKoe(Double koe) {
        this.koe = koe;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
