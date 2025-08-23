import {getCookie} from "../../cookieUtils.js";


function formatDistance(meters) {
    const km = Math.floor(meters / 1000);
    const m = meters % 1000;
    return `${km} km ${m} m`
}

function formatDuration(milliseconds) {
    const totalSeconds = Math.floor(milliseconds / 1000);
    const h = Math.floor(totalSeconds / 3600);
    const m = Math.floor((totalSeconds % 3600) / 60)
    const s = totalSeconds % 60;
    return `${h} h ${m} m ${s} s`
}

function displayFinishedTrips(trips) {
    const resultsTable = $('#finishedTrips tbody')
    resultsTable.empty()
    trips
        .sort((a, b) => new Date(a.realisedEndDateOfTrip) - new Date(b.realisedEndDateOfTrip))
        .forEach((trip, index) => {
            const row = $(`<tr></tr>`)

            row.append(`<td class="text-center align-middle">${index + 1}</td>`)
            row.append(`<td class="text-center align-middle">${trip.startDateOfTrip}</td>`)
            row.append(`<td class="text-center align-middle">${trip.endDateOfTrip}</td>`)
            row.append(`<td class="text-center align-middle">${trip.realisedEndDateOfTrip}</td>`)

            const formattedDistance = formatDistance(trip.distance);
            const formattedDuration = formatDuration(trip.duration);
            const formattedRealisedDuration = formatDuration(trip.realisedDuration);
            row.append(`<td class="text-center align-middle">${formattedDistance}</td>`)
            row.append(`<td class="text-center align-middle">${formattedDuration}</td>`)
            row.append(`<td class="text-center align-middle">${formattedRealisedDuration}</td>`)

            resultsTable.append(row)
        })
}

function loadFinishedTrips() {
    const user_id = localStorage.getItem("user_id")
    const token = getCookie("access_token")

    $.ajax({
        url: `/api/rest/map/v1/${user_id}/trips/finished`,
        type: 'GET',
        contentType: 'application/json',
        headers: {
            "Authorization": `Bearer ${token}`
        },
        success: function (data) {
            displayFinishedTrips(data)
        },
        error: function (e) {
            console.error(e)
        }
    })
}

$(document).ready(function () {
    loadFinishedTrips()
})