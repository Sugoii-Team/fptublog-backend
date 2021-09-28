package com.dsc.fptublog.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
public class AccountEntity {
    protected String id;
    protected String email;
    protected String alternativeEmail;
    protected String firstName;
    protected String lastName;
    protected String password;
    protected String avatarUrl;
    protected String description;
    protected String statusId;
}
