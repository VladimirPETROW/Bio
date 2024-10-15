package com.bio.entity;

import lombok.Data;

@Data
public class Organism {
    Long id;
    String name;
    String kind;
    Double doubling;
}
