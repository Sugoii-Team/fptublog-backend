package com.dsc.fptublog.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BlogEntity {
    private String id;
    private String authorId;
    private String thumbnailUrl;
    private String title;
    private String content;
    private String description;
    private long createdDateTime;
    private String statusId;
    private String categoryId;
    private String reviewerId;
    private long reviewDateTime;
    private int views;
}
