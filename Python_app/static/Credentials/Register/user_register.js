class User_register {
    constructor(firstname, lastname, email, password, confirm_password) {
        this._firstname = firstname;
        this._lastname = lastname;
        this._email = email;
        this._password = password;
        this._confirm_password = confirm_password;
    }


    get firstname() {
        return this._firstname;
    }

    set firstname(value) {
        this._firstname = value;
    }

    get lastname() {
        return this._lastname;
    }

    set lastname(value) {
        this._lastname = value;
    }

    get email() {
        return this._email;
    }

    set email(value) {
        this._email = value;
    }

    get password() {
        return this._password;
    }

    set password(value) {
        this._password = value;
    }

    get confirm_password() {
        return this._confirm_password;
    }

    set confirm_password(value) {
        this._confirm_password = value;
    }
}

export default User_register;