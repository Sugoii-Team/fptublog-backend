package com.dsc.fptublog.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class AwardEntity {
    private String id;
    private String name;
    private String iconUrl;
    private int point;
}
