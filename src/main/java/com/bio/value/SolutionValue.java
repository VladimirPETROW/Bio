package com.bio.value;

import com.bio.entity.SolutionRef;
import com.bio.entity.SolutionMaterial;
import com.bio.entity.SolutionReactive;
import lombok.Data;

import java.util.List;

@Data
public class SolutionValue {
    String name;
    List<SolutionReactive> reactives;
    List<SolutionMaterial> materials;
    List<SolutionRef> refs;
    String apply;
}
