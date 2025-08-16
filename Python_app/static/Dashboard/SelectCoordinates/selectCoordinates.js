import {getCookie} from '../../cookieUtils.js'


let markerLayer = null

function showSpinner() {
    document.getElementById('body').style.display = 'none'
    document.getElementById('spinner').style.display = 'block'
}

function hideSpinner() {
    document.getElementById('spinner').style.display = 'none'
    document.getElementById('body').style.display = 'block'
}

function get_places_for_visit(event) {
    event.preventDefault()
    const user_id = JSON.parse(localStorage.getItem('user_id'))
    const token = getCookie("access_token")

    const latitude = $('#latitude').val()
    const longitude = $('#longitude').val()

    if (!latitude || !longitude) {
        alert("Proszę wypełnić współrzędne punktu.")
        return
    }

    $.ajax({
        url: `http://localhost:8888/rest/map/v1/${user_id}/coordinatesVerify`,
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({
            latitude: latitude, longitude: longitude
        }),
        headers: {
            "Authorization": `Bearer ${token}`
        },
        success: function (data) {
            $('#latitude').val('')
            $('#longitude').val('')
            displayPlacesForVisit(data)
            displayExistingPlacesOnMap(data)
        },
        error: function (error) {
            console.error("Nie udało się dodać współrzędnych", error);
            alert("Nie udało się dodać współrzędnych.");
        }
    })
}

function displayPlacesForVisit(data) {
    const resultsTable = $('#places_for_visit tbody')
    resultsTable.empty()
    for (let i = 0; i < data.length; i++) {
        const row = $(`<tr></tr>`)
        row.append(`<td>${i + 1}</td>`)
        row.append(`<td><input id="${data[i].id}" class="form-check-input" type="checkbox"></td>`)
        row.append(`<td>${data[i].latitude}</td>`)
        row.append(`<td>${data[i].longitude}</td>`)
        resultsTable.append(row)
    }
}

function getCoordinatesFromTable() {
    const coordinatesList = [];

    $('#places_for_visit tbody tr').each(function () {
        const tds = $(this).find('td');
        const id = tds.eq(1).find('input').attr('id');
        const latitude = tds.eq(2).text();
        const longitude = tds.eq(3).text();

        coordinatesList.push({id, latitude, longitude})
    })

    return coordinatesList;
}

function displayExistingPlacesOnMap(places) {
    const iframe = $('#map_frame')

    const contentWindow = iframe[0].contentWindow
    const map = contentWindow[mapName]
    const L = contentWindow.L
    if (!map) {
        console.error('Mapa Leaflet nie jest dostępna!');
        return;
    }
    if (markerLayer) {
        markerLayer.clearLayers();
    } else {
        markerLayer = L.layerGroup().addTo(map)
    }
    if (places.length > 0) {
        places.forEach((coordinates, index) => {
            const marker = L.marker([coordinates.latitude, coordinates.longitude])
            marker.bindTooltip(`${index + 1}`, {
                permanent: false, direction: 'top', offset: [0, -10], className: 'marker-tooltip'
            })
            marker.addTo(markerLayer)
        })
        map.setView([places[places.length - 1].latitude, places[places.length - 1].longitude], 14)
    } else {
        const locationMarker = L.marker([location_coordinates.latitude, location_coordinates.longitude])
        locationMarker.bindTooltip(`Jesteś tutaj`, {
            permanent: false, direction: 'top', offset: [0, -10], className: 'marker-tooltip'
        })
        locationMarker.addTo(markerLayer)
        map.setView([location_coordinates.latitude, location_coordinates.longitude], 14)
    }
}

function deletePlaces() {
    const selectedPlaces = []
    const table = document.getElementById("places_for_visit")
    const checkboxes = table.querySelectorAll("input[type='checkbox']:checked")
    const user_id = JSON.parse(localStorage.getItem('user_id'))
    const token = getCookie("access_token")

    checkboxes.forEach((checkbox) => {
        selectedPlaces.push(checkbox.id)
    });

    if (selectedPlaces === 0) {
        alert("Nie wybrałeś miejsc do usunięcia.")
        return
    }

    $.ajax({
        url: `http://localhost:8888/rest/map/v1/${user_id}/deleteCoordinates?ids=${selectedPlaces.join(',')}`,
        type: 'GET',
        contentType: 'application/json',
        headers: {
            "Authorization": `Bearer ${token}`
        },
        success: function (data) {
            displayPlacesForVisit(data)
            displayExistingPlacesOnMap(data)
            alert("Pomyślnie udało Ci się usunąć wybrane miejsca.")
        },
        error: function (error) {
            console.error("Error deleting cities:", error);
            alert("Nie udało Ci się usunąć wybranych miejsc. Spróbuj później.");
        }
    })
}

function findRoad() {
    const user_id = JSON.parse(localStorage.getItem('user_id'))
    const token = getCookie("access_token")
    const coordinates = getCoordinatesFromTable()
    showSpinner();

    $.ajax({
        url: `/map/generateRoad/${user_id}`, type: 'GET', contentType: 'application/json', headers: {
            "Authorization": `Bearer ${token}`
        }, success: function (res) {
            const route = res.route
            const map_name = res.map_name
            const trip = {
                duration: route.duration * 1000, distance: route.distance, coordinates: coordinates, map_name: map_name
            }
            localStorage.setItem("trip", JSON.stringify(trip));
            window.location.href = `/map/display_route?map_name=${map_name}`
            hideSpinner()
        }, error: function (e) {
            hideSpinner()
            alert(e.responseJSON.message)
            console.error("Error finding road: ", e)
        }
    })
}

function loadExistingPlaces() {
    const user_id = JSON.parse(localStorage.getItem('user_id'))
    const token = getCookie('access_token')

    $.ajax({
        url: `http://localhost:8888/rest/map/v1/${user_id}/coordinates`, method: 'GET', headers: {
            'Authorization': `Bearer ${token}`
        }, success: function (data) {
            displayPlacesForVisit(data)
            displayExistingPlacesOnMap(data)
        }, error: function (e) {
            console.error("Error loading existing places:", e);
            alert("Nie udało się pobrać współrzędnych. Spróbuj ponownie później.");
        }
    })
}

document.addEventListener('DOMContentLoaded', function () {

    const up_part_of_left_side = document.getElementById("up_part")
    const middle_part_of_left_side = document.getElementById("middle_part")
    const last_part_of_left_side = document.getElementById("down_part")


    const mapColumn = document.getElementById("map_container")

    function adjustTableHeight() {

        const margin = parseInt(window.getComputedStyle(middle_part_of_left_side).marginTop)
        const max_height_table = mapColumn.offsetHeight - last_part_of_left_side.offsetHeight - up_part_of_left_side.offsetHeight - 2 * margin
        middle_part_of_left_side.style.maxHeight = `${max_height_table}px`
        middle_part_of_left_side.style.overflow = "auto"

    }

    adjustTableHeight()

    window.addEventListener('resize', adjustTableHeight);
});

function add_click_event_for_send_coordinates() {
    const iframe = $('#map_frame')

    const contentWindow = iframe[0].contentWindow
    const map = contentWindow[mapName]

    if (!map) {
        console.error('Mapa Leaflet nie jest dostępna!');
        return;
    }

    map.on('click', function (e) {
        const lat = e.latlng.lat.toFixed(4)
        const lng = e.latlng.lng.toFixed(4)

        const user_id = JSON.parse(localStorage.getItem('user_id'))
        const token = getCookie('access_token')

        const data = {
            latitude: lat, longitude: lng
        }

        $.ajax({
            url: `http://localhost:8888/rest/map/v1/${user_id}/coordinatesVerify`,
            method: 'PUT',
            data: JSON.stringify(data),
            contentType: 'application/json',
            headers: {'Authorization': `Bearer ${token}`},
            success: function (data) {
                console.log(data)
            },
            error: function (e) {
                console.error("Error verify coordinates:", e);
                alert("Failed to verify coordinates. Please try again.");
            }

        })
    })
}

$(document).ready(function () {
    $(`#add_location_form`).on('submit', get_places_for_visit);
    $(`#remove_coordinates_btn`).on('click', deletePlaces);
    $(`#generate_road_btn`).on('click', findRoad);
    loadExistingPlaces()
    add_click_event_for_send_coordinates()
})