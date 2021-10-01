package com.dsc.fptublog.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CommentEntity {
    private String id;
    private String blogId;
    private String authorId;
    private String content;
    private long postedDatetime;
    private String statusId;
    private String replyTo;
}
