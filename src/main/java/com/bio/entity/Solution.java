package com.bio.entity;

import lombok.Data;

import java.util.List;

@Data
public class Solution {
    Long id;
    String name;
    List<SolutionReactive> reactives;
    List<SolutionMaterial> materials;
    List<SolutionRef> refs;
    String apply;
}
