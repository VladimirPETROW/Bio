package com.bio.value;

import com.bio.entity.FeedMaterial;
import com.bio.entity.FeedReactive;
import lombok.Data;

import java.util.List;

@Data
public class ExperimentValue {
    Long organism;
    Long feed;
    Integer whole;
    Integer product;
    Integer koe;
}
