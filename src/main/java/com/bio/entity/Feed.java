package com.bio.entity;

import lombok.Data;

@Data
public class Feed {
    Long id;
    Solution solution;
    String apply;
    Long organism;
    String purpose;
}
