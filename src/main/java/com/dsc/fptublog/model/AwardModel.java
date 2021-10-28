package com.dsc.fptublog.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AwardModel {
    private String id;
    private String name;
    private String iconUrl;
    private int point;
    private int count;
}
