import {getCookie} from "../../cookieUtils.js";


$('#edit').click(function () {
    $(this).toggleClass('active')
    const edit = $(generateVerifyPassword())
    if ($(this).hasClass('active')) {
        $('#password_container').after(edit)
    } else {
        $('.visible').remove()
    }
})

$(document).on('submit', '#delete', function (e) {
    e.preventDefault()

    const user_id = localStorage.getItem('user_id')
    const token = getCookie("access_token")

    if (confirm("Czy napewno chcesz usunąć konto?")) {
        $.ajax({
            url: `http://localhost:8888/rest/user/v1/${user_id}/account/delete`,
            type: 'GET',
            contentType: 'application/json',
            headers: {
                "Authorization": `Bearer ${token}`
            },
            success: function () {
                alert("Pomyślnie udało Ci się usunąć konto")
                window.location.href = '../../HomePage'
            },
            error: function (error) {
                console.error("Nie udało się usunąć konta", error);
                alert("Nie udało się usunąć konta.");
            }
        })
    }
})

$(document).on('submit', '#change_password', function (e) {
    e.preventDefault()

    const user_id = localStorage.getItem('user_id')
    const token = getCookie("access_token")

    const password = $('#password').val();
    const new_password = $('#new_password').val();

    $.ajax({
        url: `http://localhost:8888/rest/user/v1/${user_id}/password/change`,
        type: 'POST',
        contentType: 'application/json',
        headers: {
            "Authorization": `Bearer ${token}`
        },
        data: JSON.stringify({
            password: password,
            new_password: new_password
        }),
        success: function () {
            alert("Pomyślnie udało Ci się zmienić hasło")
        },
        error: function (error) {
            console.error("Nie udało się zmienić hasła", error);
            alert("Nie udało się zmienić hasła.");
        }
    })
})

$(document).on('submit', '#verify_password', function (e) {
    e.preventDefault()

    const user_id = localStorage.getItem('user_id')
    const token = getCookie("access_token")

    const password = $('#password_verify').val();
    const new_password = $('#new_password_verify').val();

    $.ajax({
        url: `http://localhost:8888/rest/user/v1/${user_id}/password/verify`,
        type: 'POST',
        contentType: 'application/json',
        headers: {
            "Authorization": `Bearer ${token}`
        },
        data: JSON.stringify({
            password: password,
            new_password: new_password
        }),
        success: function () {
            $('#verify_password').css('display', 'none')
            const edit = $(generateChangePassword())
            $('#password_container').after(edit)
            $('input').val('');
            alert("Wprowadź nowe hasło")
        },
        error: function (error) {
            console.error("Hasła się nie zgadzają", error);
            alert("Hasła się nie zgadzają.");
        }
    })
})

function generateChangePassword() {
    return '<form id="change_password" class="visible">' +
        '<div class="col-md-6 mt-3">' +
        '<input id="password" class="form-control" type="password" placeholder="Wprowadź nowe hasło" required/>' +
        '</div>' +
        '<div class="col-md-6 mt-3">' +
        '<input id="new_password" class="form-control" type="password" placeholder="Potwierdź hasło" required>' +
        '</div>' +
        '<div class="col-md-6 mt-3">' +
        '<button type="submit" class="btn btn-primary w-100">Wyślij</button>' +
        '</div>' +
        '</form>'
}

function generateVerifyPassword() {
    return '<form id="verify_password" class="visible">' +
        '<div class="col-md-6 mt-3">' +
        '<input id="password_verify" class="form-control" type="password" placeholder="Wprowadź aktualne hasło" required/>' +
        '</div>' +
        '<div class="col-md-6 mt-3">' +
        '<input id="new_password_verify" class="form-control" type="password" placeholder="Potwierdź hasło" required>' +
        '</div>' +
        '<div class="col-md-6 mt-3">' +
        '<button type="submit" class="btn btn-primary w-100">Wyślij</button>' +
        '</div>' +
        '</form>'
}

function fillUserData(data) {
    const firstname = $('#firstname')
    const lastname = $('#lastname')
    const created_at = $('#created_at')
    const last_login = $('#last_login')

    firstname.text(data.firstname);
    lastname.text(data.lastname);

    const dateCreated_at = new Date(data.createdAt)
    created_at.text(dateCreated_at.toLocaleString());

    const dateLastLogin = new Date(data.lastLogin)
    last_login.text(dateLastLogin.toLocaleString());
}

function fetchUserData() {

    const user_id = localStorage.getItem("user_id")
    const token = getCookie("access_token")

    $.ajax({
        url: `http://localhost:8888/rest/user/v1/${user_id}/user/get`,
        type: 'GET',
        contentType: 'application/json',
        headers: {
            "Authorization": `Bearer ${token}`
        }, success: function (data) {
            console.log(data)
            fillUserData(data)
        }, error: function (e) {
            console.error("User not found:", e);
            alert("Nie udało się pobrać danych użytkownika. Spróbuj ponownie później.")
            // alert("Failed to load user. Please try again.");
        }
    })
}

$(document).ready(function () {
    fetchUserData()
})