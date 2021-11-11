package com.dsc.fptublog.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class BlogHistory {
    private String id;
    private String authorId;
    private String categoryId;
    private long createdDatetime;
    private int views;
    private float avgRate;
}
