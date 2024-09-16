package com.bio.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class Feed {
    Long id;
    String name;
    List<FeedReactive> reactives;
    List<FeedMaterial> materials;
}
