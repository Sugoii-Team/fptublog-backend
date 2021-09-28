package com.dsc.fptublog.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class LecturerEntity extends AccountEntity {
    @Singular("lecturerFiled") private List<LecturerFieldEntity> lecturerFieldList;
    @Singular("lecturerAward") private List<LecturerAwardEntity> lecturerAwardList;

    public LecturerEntity(AccountEntity account,
                               List<LecturerFieldEntity> lecturerFieldList,
                               List<LecturerAwardEntity> lecturerAwardList) {
        super(account);
        this.lecturerFieldList = lecturerFieldList;
        this.lecturerAwardList = lecturerAwardList;
    }
}
