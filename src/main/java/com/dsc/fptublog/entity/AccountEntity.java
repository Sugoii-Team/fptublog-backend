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
    protected String avatarUrl;
    protected String description;
    protected String statusId;
    protected String role;
    protected int blogsNumber;
    protected float avgRate;

    public void setAccountInfo(AccountEntity account) {
        this.id = account.getId();
        this.email = account.getEmail();
        this.alternativeEmail = account.getAlternativeEmail();
        this.firstName = account.getFirstName();
        this.lastName = account.getLastName();
        this.avatarUrl = account.getAvatarUrl();
        this.description = account.getDescription();
        this.statusId = account.getStatusId();
        this.blogsNumber = account.getBlogsNumber();
        this.avgRate = account.getAvgRate();
    }
}
