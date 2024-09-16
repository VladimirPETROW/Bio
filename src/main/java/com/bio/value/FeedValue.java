package com.bio.value;

import com.bio.entity.FeedMaterial;
import com.bio.entity.FeedReactive;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class FeedValue {
    String name;
    List<FeedReactive> reactives;
    List<FeedMaterial> materials;
}
