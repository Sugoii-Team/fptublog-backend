package com.dsc.fptublog.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class LecturerEntity extends AccountEntity {
}
