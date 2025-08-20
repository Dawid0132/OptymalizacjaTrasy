package com.example.tspsecurity.Pojo.Response;

public class LoginRes {
    private Long id;
    private String token;

    public LoginRes(Long id, String token) {
        this.id = id;
        this.token = token;
    }

    public LoginRes() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
