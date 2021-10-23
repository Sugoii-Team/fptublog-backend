package com.dsc.fptublog.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class LecturerStudentAwardEntity {
    private String id;
    private String lecturerId;
    private String studentId;
    private String awardId;
}
