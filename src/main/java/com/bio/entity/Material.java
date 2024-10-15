package com.bio.entity;

import lombok.Data;

@Data
public class Material {
    Long id;
    String name;
    String unit;
    Double count;
    Double price;
}
