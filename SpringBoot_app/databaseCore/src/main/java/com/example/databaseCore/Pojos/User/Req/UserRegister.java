package com.example.databaseCore.Pojos.User.Req;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserRegister {

    @NotBlank
    @Size(min = 1, max = 50)
    private String _firstname;

    @NotBlank
    @Size(min = 1, max = 50)
    private String _lastname;

    @NotBlank
    @Size(min = 5, max = 50)
    @Email
    private String _email;

    @NotBlank
    @Size(max = 100)
    @Pattern(regexp = "^(?=.*[A-Z].*[A-Z])(?=.*[!@#$&*])(?=.*[0-9].*[0-9])(?=.*[a-z].*[a-z].*[a-z]).{8,}$")
    private String _password;

    @NotBlank
    @Size(max = 100)
    private String _confirm_password;

    public UserRegister(String _firstname, String _lastname, String _email, String _password, String _confirm_password) {
        this._firstname = _firstname;
        this._lastname = _lastname;
        this._email = _email;
        this._password = _password;
        this._confirm_password = _confirm_password;
    }

    public String get_firstname() {
        return _firstname;
    }

    public void set_firstname(String _firstname) {
        this._firstname = _firstname;
    }

    public String get_lastname() {
        return _lastname;
    }

    public void set_lastname(String _lastname) {
        this._lastname = _lastname;
    }

    public String get_email() {
        return _email;
    }

    public void set_email(String _email) {
        this._email = _email;
    }

    public String get_password() {
        return _password;
    }

    public void set_password(String _password) {
        this._password = _password;
    }

    public String get_confirm_password() {
        return _confirm_password;
    }

    public void set_confirm_password(String _confirm_password) {
        this._confirm_password = _confirm_password;
    }
}
