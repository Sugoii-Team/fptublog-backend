package com.dsc.fptublog.entity;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class AwardEntity {
    private String id;
    private String name;
    private String iconUrl;
    @Singular("studentAward") private List<StudentAwardEntity> studentAwardList;
    @Singular("lecturerAward") private List<LecturerAwardEntity> lecturerAwardList;
}
