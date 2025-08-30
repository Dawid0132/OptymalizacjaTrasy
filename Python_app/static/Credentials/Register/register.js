import User_register from "./user_register.js";

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

function invalidFields(firstname, lastname, email, password, correctPassword) {
    if (firstname) {
        $('#firstname_invalid').text("Imię musi zawierać od 2 do 20 znaków");
        $('#firstname').addClass('is-invalid');
    } else {
        $('#firstname').addClass('is-valid');
    }
    if (lastname) {
        $('#lastname_invalid').text("Nazwisko musi zawierać od 2 do 20 znaków");
        $('#lastname').addClass('is-invalid');
    } else {
        $('#lastname').addClass('is-valid');
    }
    if (email) {
        $('#email_invalid').text("Email musi być zgodny z zasadami adresów pocztowych");
        $('#email').addClass('is-invalid');
    } else {
        $('#email').addClass('is-valid');
    }
    if (password) {
        $('#password_invalid').text("Hasło musi zawierać conajmniej 3 małe litery, 2 duże litery, 2 cyfry i  1 znak specjalny.");
        $('#password').addClass('is-invalid');
    } else {
        $('#password').addClass('is-valid');
    }
    if (correctPassword) {
        $('#confirm_password_invalid').text("Hało musi się zgadzać z wcześniej wprowadzonym");
        $('#confirm_password').addClass('is-invalid');
    } else {
        $('#confirm_password').addClass('is-valid');
    }
}

function clearFields(firstname, lastname, email, password, correctPassword) {
    if (firstname) {
        $('#firstname').removeClass('is-invalid is-valid');
    }
    if (lastname) {
        $('#lastname').removeClass('is-invalid is-valid');
    }
    if (email) {
        $('#email').removeClass('is-invalid is-valid');
    }
    if (password) {
        $('#password').removeClass('is-invalid is-valid');
    }
    if (correctPassword) {
        $('#confirm_password').removeClass('is-invalid is-valid');
    }
}

$(document).ready(function () {
    function checkFormOverflow() {
        const navbar = $('.navbar');
        const navbarHeight = navbar.outerHeight(true);
        const availableHeight = $(window).height() - navbarHeight;

        $('#register_container').css({
            'overflow-y': 'auto',
            'max-height': availableHeight + 'px'
        })
    }

    $('#register_submit').on('click', (e) => {
        e.preventDefault();

        const firstname = $('#firstname').val()
        const lastname = $('#lastname').val()
        const email = $('#email').val();
        const password = $('#password').val()
        const confirm_password = $('#confirm_password').val()

        const new_user = new User_register(firstname, lastname, email, password, confirm_password)

        $.ajax({
            url: '/api/rest/user/v1/register',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(new_user),
            success: function (response) {
                invalidFields(false, false, false, false, false);
                alert("Pomyślnie udało Ci się założyc konto.")
                window.location.href = "/user/login"
            },
            error: function (xhr, status, error) {
                clearFields(true, true, true, true, true);
                const statusCode = xhr.status
                console.log(statusCode)
                if (statusCode === 400) {
                    invalidFields(true, true, true, true, true);
                } else if (statusCode === 409) {
                    $('#email_invalid').text("Użytkownik z takim adresem już istnieje.");
                    $('#email').addClass('is-invalid');
                } else if (statusCode === 406) {
                    invalidFields(false, false, false, true, true);
                } else {
                    alert("Aktualnie nie możesz założyć konta w SmartRoute. Spróbuj ponownie poźniej.");
                }
            }
        })
    })

    checkFormOverflow();

    $(window).on('resize', checkFormOverflow);

    $('.navbar-toggler').on('click', function () {
        setTimeout(checkFormOverflow, 300);
    })
})