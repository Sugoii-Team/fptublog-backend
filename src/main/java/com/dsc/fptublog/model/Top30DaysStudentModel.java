package com.dsc.fptublog.model;

import com.dsc.fptublog.entity.StudentEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Top30DaysStudentModel {
    private StudentEntity student;
    private int numberOfBlog;
}
