import {getCookie} from "../../cookieUtils.js";

function showSpinner() {
    document.getElementById('body').style.display = 'none'
    document.getElementById('spinner').style.display = 'block'
}

function hideSpinner() {
    document.getElementById('spinner').style.display = 'none'
    document.getElementById('body').style.display = 'block'
}

function get_legs() {

    const user_id = JSON.parse(localStorage.getItem('user_id'))
    const token = getCookie("access_token")

    $.ajax({
        url: `/api/rest/map/v1/${user_id}/getRoute/legs?map_name=${map_name}`,
        type: 'GET',
        contentType: 'application/json',
        headers: {
            "Authorization": `Bearer ${token}`
        },
        success: function (data) {
            help_user_get_correct_road(data)
            hideSpinner()
            adjustTableHeight()
        },
        error: function (e) {
            console.error(e)
        }
    })
}

function generateIntersectionHTML(step, intersection, legIdx, stepIdx, interIdx) {

    let instruction = ``


    const manuever = step.maneuver
    const instruction_manuever = getManeuverInstruction(manuever.type, manuever.modifier, manuever.exit)
    instruction = addInstruction(instruction_manuever.type_instruction, instruction_manuever.modifier_instruction, instruction_manuever.exit)


    return `
      <div>
                <div class="d-flex flex-row justify-content-start align-items-center mt-1">

<div class="col-sm-3 fw-bold">Manewr</div>
<div class="col-sm-9">
${interIdx === 0 ? `<a data-coordinates="${intersection.location[0]},${intersection.location[1]}" id="btn_location_${legIdx}_${stepIdx}_0" href="#">(${intersection.location[0]},${intersection.location[1]})</a>` : ""}
</div>

</div>
<div id="${legIdx}_leg_${stepIdx}_step_${interIdx}_intersection">
${instruction}

</div>
</div>`
}

function generateStepHTML(index, step, idx) {

    const intersectionsHTML = generateIntersectionHTML(step, step.intersections[0], index, idx, 0)


    return `
<div id="${index}_leg_${idx}_step">
<div class="d-flex flex-row align-items-center mt-1">
<div class="col-10">
<div class="d-flex flex-row justify-content-between">
<div>[${idx + 1}] ${step.name} </div>
<div>${step.distance}m ${Math.round(step.duration / 60).toFixed(1)}min</div>
</div>
</div>
<div class="col-sm-2">
<div class="d-flex flex-row justify-content-end">
<div>
</div>
<div style="border-color: #f6d8ae!important;" id="tgl_container_${index}_step_${idx}" class="border-3 border-start border-opacity-25">
<button id="tgl_btn_${index}_nested_${idx}" type="button" class="btn btn-sm" data-toggle="button" aria-pressed="false">
<img class="" id="tgl_btn_img_${index}_nested_${idx}" src="/static/Btn_status/caret-down-square.svg" alt="togglee"/>
</button>
</div>
</div>
</div>
</div>
<div style="display: none" id="${index}_leg_${idx}_step_${idx}_toggle">
${intersectionsHTML}
</div>
</div>
      `
}

function generateLegHTML(index, item) {
    const distance = Math.round(item.distance / 1000)
    const duration = Math.round(item.duration / 60)
    const hours = Math.floor(duration / 60);
    const minutes = duration % 60;
    const stepsHTML = item.steps.map((step, idx) => generateStepHTML(index, step, idx)).join("");


    return `
        <div class="pb-3 hover-effect" id="${index}_leg">
<div class="d-flex flex-row align-items-center">
<div class="h5 col-10">
<div class="d-flex flex-row">
<div class="col-6">
[${index + 1}] ${item.summary}
</div>
<div class="col-6 text-end d-flex align-items-center justify-content-center">
<div class="col">
${distance} km ${duration > 60 ? hours + "h" : ""} ${duration > 60 ? minutes : duration} min
</div>



</div>
</div>
</div>
<div class="col-sm-2">
<div class="d-flex flex-row justify-content-end">
<div style="border-color: #f6d8ae!important;" id="tgl_container_${index}" class="border-3 border-start border-opacity-25">
<button id="tgl_btn_${index}" type="button" class="btn btn-sm" data-toggle="button" aria-pressed="false">
<img class="" id="tgl_btn_img_${index}" src="/static/Btn_status/caret-down-square.svg" alt="togglee"/>
</button>
</div>
</div>
</div>
</div>
<div id="${index}_toggle" style="display:none;">
${stepsHTML}
</div>
</div>
        `
}

function toggleElement(btnID, tglID, imgID, containerID) {
    let toggleElement = $(`#${tglID}`)
    let imgElement = $(`#${imgID}`)
    let containerElement = $(`#${containerID}`)

    toggleElement.toggle();
    containerElement.toggleClass("border-opacity-100");
    imgElement.attr("src", toggleElement.is(":visible") ? "/static/Btn_status/caret-down-square-fill.svg" : "/static/Btn_status/caret-down-square.svg");

}

function zooming_for_coordinates(latitude, longitude) {
    const iframe = $('#map_frame')

    const contentWindow = iframe[0].contentWindow
    const map = contentWindow[id_map]


    if (!map) {
        console.error('Mapa Leaflet nie jest dostępna!');
        return;
    }

    map.setView([latitude, longitude], 20);

}

function add_legs(item, index) {
    const legs_html = $('#legs')
    const row = $(generateLegHTML(index, item))

    row.find(`#tgl_btn_${index}`).on('click', function () {
        toggleElement(`tgl_btn_${index}`, `${index}_toggle`, `tgl_btn_img_${index}`, `tgl_container_${index}`)
    });

    item.steps.forEach((step, idx) => {
        $(document).on('click', `#tgl_btn_${index}_nested_${idx}`, function () {
            toggleElement(`tgl_btn_${index}_nested_${idx}`, `${index}_leg_${idx}_step_${idx}_toggle`, `tgl_btn_img_${index}_nested_${idx}`, `tgl_container_${index}_step_${idx}`)

        })

        $(document).on('click', `#btn_location_${index}_${idx}_0`, function (e) {
            e.preventDefault()
            const coordinates = $(e.target).data('coordinates');
            const [longitude, latitude] = coordinates.split(',').map(Number);
            zooming_for_coordinates(latitude, longitude)
        })

    })


    legs_html.append(row)


}

function getManeuverInstruction(type, modifier, exit = null) {
    const type_check = {
        "turn": {text: "Skręć", icon: "/static/Road_directions/default.svg"},
        "new name": {text: "Jedź dalej, droga zmienia nazwę", icon: "/static/Road_directions/default.svg"},
        "depart": {text: "Rozpocznij trasę", icon: "/static/Road_directions/default.svg"},
        "arrive": {text: "Dotarłeś do celu", icon: "/static/Road_directions/default.svg"},
        "merge": {text: "Włącz się do ruchu", icon: "/static/Road_directions/type/merge.svg"},
        "ramp": {text: "Zjedź na pas włączenia", icon: "/static/Road_directions/type/merge.svg"},
        "on ramp": {text: "Wjedź na autostradę", icon: "/static/Road_directions/type/merge.svg"},
        "off ramp": {text: "Zjedź z autostrady", icon: "/static/Road_directions/type/merge.svg"},
        "fork": {text: "Na rozwidleniu wybierz odpowiedni kierunek", icon: "/static/Road_directions/type/fork.svg"},
        "end of road": {text: "Na końcu drogi", icon: "/static/Road_directions/default.svg"},
        "use lane": {text: "Korzystaj z odpowiedniego pasa", icon: "/static/Road_directions/default.svg"},
        "continue": {text: "Jedź dalej, ", icon: "/static/Road_directions/default.svg"},
        "roundabout": {text: "Wjedź na rondo", icon: "/static/Road_directions/type/rotary.svg"},
        "exit roundabout": {text: "Zjedź z ronda", icon: "/static/Road_directions/type/rotary.svg"},
        "rotary": {text: "Wjedź na rondo", icon: "/static/Road_directions/type/rotary.svg"},
        "exit rotary": {text: "Zjedź z ronda", icon: "/static/Road_directions/type/rotary.svg"},
        "roundabout turn": {text: "Na rondzie ", icon: "/static/Road_directions/type/rotary.svg"},
        "notification": {text: "Uwaga: zmiana kierunków jazdy", icon: "/static/Road_directions/default.svg"},
    }

    const modifier_check = {
        "uturn": {text: "Wykonaj zawracanie", icon: "/static/Road_directions/modifiers/uturn.svg"},
        "sharp right": {text: "Skręć ostro w prawo", icon: "/static/Road_directions/modifiers/right.svg"},
        "right": {text: "Skręć w prawo", icon: "/static/Road_directions/modifiers/right.svg"},
        "slight right": {text: "Skręć lekko w prawo", icon: "/static/Road_directions/modifiers/slight-right.svg"},
        "straight": {text: "Jedź prosto", icon: "/static/Road_directions/modifiers/straight.svg"},
        "slight left": {text: "Skręć lekko w lewo", icon: "/static/Road_directions/modifiers/slight-left.svg"},
        "left": {text: "Skręć w lewo", icon: "/static/Road_directions/modifiers/left.svg"},
        "sharp left": {text: "Skręc ostro w lewo", icon: "/static/Road_directions/modifiers/left.svg"}
    }

    if (!type_check[`${type}`]) {
        console.error(`Nieznany typ manewru -> ${type}`)
    }

    if (!modifier_check[`${modifier}`]) console.error(`Nieznany modifikator manewru -> ${modifier}`)


    return {
        type_instruction: type_check[`${type}`] || {
            text: "Nieznany manewr", icon: "/static/Road_directions/default.svg"
        }, modifier_instruction: modifier_check[`${modifier}`] || {
            text: "Nieznany manewr", icon: "/static/Road_directions/default.svg"
        }, exit: exit
    }
}

function addInstruction(type_instruction, modifier_instruction, exit) {

    return `
      <div class="d-flex flex-row">
<div class="col col-sm-4">
 <img src="${type_instruction.icon}" /> ${type_instruction.text}
</div>
<div class="col col-sm-4">
<img src="${modifier_instruction.icon}" /> ${modifier_instruction.text}
</div>
${exit ? `<div class="col col-sm-4"> ${exit} zjazd </div> ` : ""}

</div>
      `
}

function help_user_get_correct_road(legs) {


    legs.forEach((item, index) => {
        add_legs(item, index)

    })
}

function adjustTableHeight() {

    const up_part = $(`#up_part`)
    const middle_part = $(`#legs`)
    const down_part = $(`#startContainer`)
    const map = $(`#map_container`)

    const margin = parseInt(window.getComputedStyle(down_part.get(0)).marginTop)
    const max_height_body = map.get(0).offsetHeight - up_part.get(0).offsetHeight - down_part.get(0).offsetHeight - 2 * margin

    middle_part.css({
        "max-height": `${max_height_body}px`, "overflow": "auto"
    })
}

function measuringTime(e) {
    e.preventDefault();

    const user_id = localStorage.getItem("user_id")
    const token = getCookie("access_token")

    $.ajax({
        url: `/api/rest/map/v1/${user_id}/trips/startDriving?map_name=${map_name}`,
        type: 'GET',
        contentType: 'application/json',
        headers: {
            "Authorization": `Bearer ${token}`
        },
        success: function (data) {
            displayStatusIsDriving(data)
        },
        error: function (e) {
            console.error(e)
        }
    })
}

function displayStatusIsDriving(status) {
    const timeBtn = $('#startBtn')

    if (status) {
        timeBtn.removeClass('btn-success').addClass('btn-danger')
        timeBtn.text('Stop')
    } else {
        timeBtn.removeClass('btn-danger').addClass('btn-success')
        timeBtn.text('Start')
    }
}

function getStatusIsDriving() {
    const user_id = localStorage.getItem("user_id")
    const token = getCookie("access_token")

    $.ajax({
        url: `/api/rest/map/v1/${user_id}/trips/measuringTime/status?map_name=${map_name}`,
        type: 'GET',
        contentType: 'application/json',
        headers: {
            "Authorization": `Bearer ${token}`
        },
        success: function (data) {
            displayStatusIsDriving(data)
        },
        error: function (e) {
            console.error(e)
        }
    })
}

$(document).ready(function () {
    get_legs()
    getStatusIsDriving()
    $(`#startForm`).on('submit', measuringTime);
    window.addEventListener('resize', adjustTableHeight)
})