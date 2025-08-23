import {getCookie} from "../../cookieUtils.js";

function displaySavedPlaces(data) {
    const resultsTable = $('#savedPlaces tbody')
    resultsTable.empty()
    data
        .sort((a, b) => new Date(a.startDate) - new Date(b.startDate))
        .forEach((trip, index) => {
            const row = $(`<tr></tr>`)

            const btnPreviewId = `btn_preview_${index}`
            const btnDoneId = `btn_done_${index}`
            const btnDeleteId = `btn_delete_${index}`

            row.append(`<td class="text-center align-middle">${index + 1}</td>`)
            row.append(`<td class="text-center align-middle">${trip.startDate}</td>`)
            row.append(`<td class="text-center align-middle">${trip.endDate}</td>`)
            row.append(`<td><div class="d-flex justify-content-center align-items-center"><form id=${btnPreviewId}><button id='btn_preview' type='submit' class="btn btn-info"><img src="${searchIconSrc}" id="done" alt="finish"></button></form></div></td>`)
            row.append(`<td><div class="d-flex justify-content-center align-items-center"><form id=${btnDoneId}><button id='btn_done' class="btn btn-success"><img src="${checkIconSrc}" alt="edit"></button></form></div></td>`)
            row.append(`<td><div class="d-flex justify-content-center align-items-center"><form id=${btnDeleteId}><button id='btn_delete' class="btn btn-danger"><img src="${trashIconSrc}" alt="delete"></button></form></div></td>`)

            resultsTable.append(row)

            $(`#${btnPreviewId}`).on('submit', function (e) {
                e.preventDefault()

                const user_id = JSON.parse(localStorage.getItem('user_id'))
                const token = getCookie("access_token");

                $.ajax({
                    url: `/trip/upcoming/preview/${user_id}?trip_id=${trip.id}`,
                    type: 'GET',
                    contentType: 'application/json',
                    headers: {
                        "Authorization": `Bearer ${token}`
                    },
                    success: function (data) {
                        window.location.href = `/trip/upcoming/preview?map_name=${data.map_name}&map_id=${data.frame_name}`
                    }, error: function (e) {
                        console.error("Error preview trip:", e);
                        alert("Nie udało Ci się podejrzeć wycieczki. Spróbuj później.");
                    }
                })
            })

            $(`#${btnDoneId}`).on('submit', function (e) {
                e.preventDefault()

                const user_id = JSON.parse(localStorage.getItem('user_id'))
                const token = getCookie("access_token");

                $.ajax({
                    url: `/api/rest/map/v1/${user_id}/trips/finish?trip_id=${trip.id}`,
                    type: 'GET',
                    contentType: 'application/json',
                    headers: {
                        "Authorization": `Bearer ${token}`
                    }, success: function (data) {
                        displaySavedPlaces(data)
                        alert("Podróż pomyślnie zakończona.")
                    }, error: function (xhr, status, error) {
                        if (xhr.status === 406) {
                            alert("Aby zakończyć podróż musisz wyłączyć tryb jazdy.")
                        }
                        console.error(error)
                    }
                })
            })

            $(`#${btnDeleteId}`).on('submit', function (e) {
                e.preventDefault()

                const user_id = JSON.parse(localStorage.getItem('user_id'))
                const token = getCookie("access_token");

                $.ajax({
                    url: `/trip/upcoming/delete/${user_id}?trip_id=${trip.id}&map_name=${trip.mapName}`,
                    type: 'DELETE',
                    contentType: 'application/json',
                    headers: {
                        "Authorization": `Bearer ${token}`
                    }, success: function (data) {
                        displaySavedPlaces(data)
                        alert("Pomyślnie udało Ci się usunąć wybraną wycieczkę.")
                    }, error: function (e) {
                        alert(e.responseJSON.message)
                    }
                })
            })

        })
}

function loadSavedPlaces() {
    const user_id = JSON.parse(localStorage.getItem('user_id'))
    const token = getCookie("access_token");
    $.ajax({
        url: `/api/rest/map/v1/${user_id}/trips/unfinished`,
        type: 'GET',
        contentType: 'application/json',
        headers: {
            "Authorization": `Bearer ${token}`
        },
        success: function (data) {
            displaySavedPlaces(data)
        },
        error: function (e) {
            console.error("Error loading saved places:", e)
            alert("Nie udało się pobrać aktualnych podróży. Spróbuj ponownie później.")
        }
    })
}

$(document).ready(function () {
    loadSavedPlaces()
})