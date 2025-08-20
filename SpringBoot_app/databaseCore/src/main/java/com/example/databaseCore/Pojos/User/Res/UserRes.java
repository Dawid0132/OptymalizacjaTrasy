package com.example.databaseCore.Pojos.User.Res;

import java.time.LocalDateTime;

public class UserRes {
    private String firstname;

    private String lastname;

    private LocalDateTime createdAt;

    private LocalDateTime lastLogin;

    public UserRes(String firstname, String lastname, LocalDateTime createdAt, LocalDateTime lastLogin) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.createdAt = createdAt;
        this.lastLogin = lastLogin;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }
}
