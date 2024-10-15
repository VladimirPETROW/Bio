package com.bio.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Experiment {
    Long id;
    LocalDateTime created;
    Long organism;
    Long feed;
    Double whole;
    Double product;
    Double koe;
}
