package com.dsc.fptublog.entity;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class FieldEntity {
    private String id;
    private String name;
    @Singular("lecturerField") private List<LecturerFieldEntity> lecturerFieldList;
    private List<CategoryEntity> categoryList;
}
