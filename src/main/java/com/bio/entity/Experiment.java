package com.bio.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class Experiment {
    Long id;
    LocalDateTime created;
    Long organism;
    Long feed;
    Integer whole;
    Integer product;
    Integer koe;
}
