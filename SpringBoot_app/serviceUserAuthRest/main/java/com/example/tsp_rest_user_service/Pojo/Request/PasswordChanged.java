package com.example.tsp_rest_user_service.Pojo.Request;

public class PasswordChanged {
    private String password;
    private String new_password;


    public PasswordChanged(String password, String new_password) {
        this.password = password;
        this.new_password = new_password;
    }

    public PasswordChanged() {
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNew_password() {
        return new_password;
    }

    public void setNew_password(String new_password) {
        this.new_password = new_password;
    }
}
