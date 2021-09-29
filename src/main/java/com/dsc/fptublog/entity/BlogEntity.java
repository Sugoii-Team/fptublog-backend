package com.dsc.fptublog.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BlogEntity {
    private String id;
    private AccountEntity author;
    private String title;
    private String content;
    private long postedDateTime;
    private BlogStatusEntity status;
    private CategoryEntity category;
    private AccountEntity reviewer;
    private long reviewDateTime;
    private int views;
    private List<BlogTagEntity> blogTags;
}
