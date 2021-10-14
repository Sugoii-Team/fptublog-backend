package com.dsc.fptublog.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class BlogHistory {
    private String id;
    private long createdDatetime;
    private int views;
}
