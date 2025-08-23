import {setCookie} from "../../cookieUtils.js"

$('#Logo').on('click', (e) => {
    e.preventDefault();

    window.location.href = '/smartroute/homePage';
})

$('#Logowanie').on('click', (e) => {
    e.preventDefault()

    window.location.href = '/user/login'
})

$('#Rejestracja').on('click', (e) => {
    e.preventDefault();

    window.location.href = '/user/register'
})

function clearFields(email, password) {
    if (email) {
        $('#email').removeClass('is-invalid is-valid');
    }
    if (password) {
        $('#password').removeClass('is-invalid is-valid');
    }
}

function invalidFields(email, password) {
    if (email) {
        $('#email_invalid').text("Niepoprawny email");
        $('#email').addClass('is-invalid');
    } else {
        $('#email').addClass('is-valid');
    }
    if (password) {
        $('#password_invalid').text("Niepoprawne hasło");
        $('#password').addClass('is-invalid');
    } else {
        $('#password').addClass('is-valid');
    }
}

$(document).ready(function () {
    $('#login_submit').on('click', (e) => {
        e.preventDefault()
        const email = $('#email').val();
        const password = $('#password').val()


        $.ajax({
            url: '/user/signin',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({
                email: email, password: password
            }),
            success: function (response) {
                invalidFields(false, false);

                localStorage.setItem("user_id", response.id)
                setCookie(response.token)
                window.location.href = "/dashboard/"
            },
            error: function (xhr, status, error) {
                clearFields(true, true);
                const statusCode = xhr.status
                if (statusCode === 401) {
                    invalidFields(true, true);
                } else {
                    alert("Aktualnie nie możesz się zalogować w SmartRoute. Spróbuj ponownie poźniej.")
                }
            }
        })
    })
})