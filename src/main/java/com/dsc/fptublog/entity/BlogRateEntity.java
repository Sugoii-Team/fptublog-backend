package com.dsc.fptublog.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class BlogRateEntity {
    private String id;
    private String blogHistoryId;
    private String rateId;
    private int amount;
}
