import {getCookie} from "../../cookieUtils.js";

$(`#saveForm`).on("submit", function (e) {
    e.preventDefault()
    const startdate = $('#startdate').val();

    const user_id = JSON.parse(localStorage.getItem('user_id'))
    const trip = JSON.parse(localStorage.getItem('trip'))
    const token = getCookie("access_token")

    const iframeId = $('#map_container').find('iframe').attr('id');

    if (trip.map_name !== iframeId) {
        window.location.href = `/map/display_route?map_name=${trip.map_name}`
        return
    }

    $.ajax({
        url: `/map/selectCoordinates/save?map_name=${iframeId}`,
        type: 'DELETE',
        contentType: 'application/json',
        success: function () {
            $.ajax({
                url: `/api/rest/map/v1/${user_id}/trips`,
                type: 'POST',
                contentType: 'application/json',
                headers: {
                    "Authorization": `Bearer ${token}`
                },
                data: JSON.stringify({
                    trip: trip, startDate: startdate,
                }),
                success: function () {
                    localStorage.removeItem('trip');
                    alert('Pomyślnie udało Ci się zapisać trasę')
                    window.location.href = '/dashboard'
                },
                error: function (xhr, status, error) {
                    console.error("Nie udało się zapisać trasy", error);
                    if (xhr.status === 404) {
                        alert("Aktualnie nie możesz zapisać trasy")
                    } else if (xhr.status === 409) {
                        alert("Możesz zapisać podróż maksymalnie jeden miesiąc z wyprzedzeniem")
                    } else if (xhr.status === 406) {
                        alert("Spróbuj wygenerować trasę jeszcze raz.")
                    }
                }
            })
        }, error: function (xhr, status, error) {
            console.error("Nie udało się usunąć mapy", error);
            if (xhr.status === 404) {
                window.location.href = "/map/selectCoordinates"
            }
        }
    })
})