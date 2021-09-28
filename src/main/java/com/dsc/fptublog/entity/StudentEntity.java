package com.dsc.fptublog.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class StudentEntity extends AccountEntity {
    private short schoolYear;
    private String majorId;
}
