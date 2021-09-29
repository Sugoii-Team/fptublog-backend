package com.dsc.fptublog.entity;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class AwardEntity {
    private String id;
    private String name;
    private String iconUrl;
}
