package com.dsc.fptublog.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class StudentEntity extends AccountEntity {
    private short schoolYear;
    private MajorEntity major;
    @Singular("studentAward") private List<StudentAwardEntity> studentAwardList;

    public StudentEntity(AccountEntity account, short schoolYear, MajorEntity major, List<StudentAwardEntity> studentAwardList) {
        super(account);
        this.schoolYear = schoolYear;
        this.major = major;
        this.studentAwardList = studentAwardList;
    }
}
