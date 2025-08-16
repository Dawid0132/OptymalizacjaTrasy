import os

import requests
from flask import Blueprint, request, jsonify, session, redirect, render_template, url_for, current_app
from folium import folium
from folium import Marker, LatLngPopup, GeoJson, Element
from folium.plugins import MousePosition
from folium.template import Template

map_api = Blueprint('map_api', __name__, template_folder='templates', url_prefix='/map')


def getPlacesForVist(user_id, token):
    try:
        response = requests.get(
            f"http://localhost:8888/rest/map/v1/coordinates/{user_id}",
            headers={
                "Content-Type": "application/json",
                "Authorization": f"Bearer {token}"})
        return response.json(), response.status_code
    except requests.RequestException:
        return jsonify({"message": "Nie ma żadnych miejsc dla wygenerowania trasy"}), 404


def getLastClickedCoordinates(user_id, token, data):
    try:
        response = requests.get(
            f"http://localhost:8888/rest/map/v1/coordinatesVerify/{user_id}",
            json=data,
            headers={
                "Content-Type": "application/json",
                "Authorization": f"Bearer {token}"})
        return response.status_code
    except requests.RequestException:
        return jsonify({"message": "Współrzędne się nie zgadzają"}), 400


def generateRoad(user_id, token):
    try:
        response = requests.get(f"http://localhost:8888/rest/map/v1/getRoute/{user_id}",
                                headers={
                                    "Content-Type": "application/json",
                                    "Authorization": f"Bearer {token}"
                                })
        return response
    except requests.RequestException:
        return jsonify({"message": "Nie mogę wygenerować trasy"})


def loadMap():
    m = folium.Map()

    m.add_child(LatLngPopup())

    mouse_position = MousePosition(position='bottomright', separator=' | ', prefix="Lat, Lng: ", num_digits=6)
    m.add_child(mouse_position)

    m_name = m.get_name()

    map_html_path = os.path.join(current_app.root_path, 'static', 'map.html')

    try:
        m.save(map_html_path)
        print(f"Mapa zapisana: {map_html_path}")
        return m_name
    except Exception as e:
        print(f"Błąd podczas zapisywania mapy: {e}")
    return jsonify({"error": "Failed to save the map"}), 500


def loadRouteMap(geometry, distance, duration, legs):
    m = folium.Map(location=[geometry['coordinates'][0][1], geometry['coordinates'][0][0]], zoom_start=18)

    legend_html = '''
        <div style="position: fixed; 
                     bottom: 10px; left: 10px; width: 160px; height: auto; 
                     background-color: white; border:2px solid grey; z-index:9999;
                     font-size: 12px; padding: 10px;">
         <b>Route Legend</b><br>
         -------------------
        '''

    legend_html += f'<br><b>Total Distance:</b> {int(distance // 1000)}km {int(distance % 1000)}m<br>'
    legend_html += f'<b>Total Duration:</b> {int(duration // 3600)}h {int((duration % 3600) // 60)}min<br>'

    legend_html += '</div>'

    m.get_root().html.add_child(Element(legend_html))

    m_name = m.get_name()

    markers_data = []

    for idxLeg, leg in enumerate(legs):
        for idxStep, step in enumerate(leg["steps"]):
            for idx, intersection in enumerate(step["intersections"]):
                if idx == 0:
                    lat, lon = intersection["location"][1], intersection["location"][0]
                    markers_data.append({"lat": lat, "lon": lon, "leg": idxLeg, "step": idxStep, })

    template_string = """
            <script>
            document.addEventListener('DOMContentLoaded', function () {
            let markers = {{ markers_data }};
            let map = {{ m_name }};

            let markersGroup = L.layerGroup().addTo(map);

            function addOrRemoveMarker(lat,lon,legIdx,stepIdx){
                let key = `${lat},${lon}`;

                let markerToRemove = null

                markersGroup.eachLayer(function(layer){
                    let {lat: mLat, lng: mLon} = layer.getLatLng();
                    if(mLat === lat && mLon === lon){
                        markerToRemove = layer
                    }
                })

                if(markerToRemove){
                    markersGroup.removeLayer(markerToRemove);
                    return;
                }


                markers.forEach(marker => {
                    if(marker.lat === lat && marker.lon === lon && marker.leg === legIdx && marker.step === stepIdx){
                        let newMarker = L.marker([marker.lat,marker.lon]);
                        markersGroup.addLayer(newMarker)
                    }
                })
            }

            window.addEventListener("message", function(e){
                if(e.data.action === "toggle_marker"){
                    let {lat,lon,legIdx,stepIdx} = e.data;
                    addOrRemoveMarker(lat,lon,legIdx,stepIdx)
                }
            },false);   
            })
            </script>
            """

    template = Template(template_string)
    map_listener = template.render(m_name=m_name, markers_data=markers_data)

    m.get_root().html.add_child(folium.Element(map_listener))

    GeoJson(
        geometry,
        style_function=lambda feature, color="#6E64FB": {
            'fillColor': color,
            'color': color,
            'weight': 3,
            'opacity': 1
        }
    ).add_to(m)

    map_html_path = os.path.join('FlaskApp', 'static', 'generatedRoad.html')

    try:
        m.save(map_html_path)
        print(f"Mapa zapisana: {map_html_path}")
    except Exception as e:
        print(f"Błąd podczas zapisywania mapy: {e}")
        return jsonify({"error": "Failed to save the map"}), 500


@map_api.route('/')
def get_location():
    return render_template('spinner_location.html')


@map_api.route('/generateMap/<int:id>', methods=['GET'])
def generateMap(id):
    token = request.cookies.get('access_token')

    if not token:
        return jsonify({"message": "Brak tokenu JWT"})

    content, status = getPlacesForVist(id, token)

    places = []
    location = True

    lat = request.args.get("lat", 52.237049)
    lon = request.args.get("lon", 21.017532)

    session['latitude'] = lat
    session['longitude'] = lon

    if content:
        places = content
        location = False
    else:
        places = [{'latitude': lat, 'longitude': lon}]

    loadMap(places, location)

    return redirect('/map/dashboard')


@map_api.route('/coordinates/all/<int:id>', methods=['GET'])
def allCoordinates(id):
    token = request.cookies.get('access_token')
    content, status = getPlacesForVist(id, token)

    print(content)

    if status == 200:
        return content
    else:
        return jsonify({"message": "Nie mogę pobrać wszystkich współrzędnych"}), 400


@map_api.route('/coordinates/add/<int:id>', methods=['POST'])
def addCoordinate(id):
    data = request.json
    token = request.cookies.get('access_token')

    try:
        response = requests.post(
            f"http://localhost:8888/rest/map/v1/coordinatesVerify/{id}",
            json=data,
            headers={"Content-Type": "application/json",
                     "Authorization": f"Bearer {token}"})

        content, status = getPlacesForVist(id, token)
        loadMap(content, False)

        return response.content, response.status_code
    except requests.RequestException:
        return jsonify({"message": "Nie mogę dodać współrzędnych"}), 400


@map_api.route('/coordinates/delete/<int:id>', methods=['DELETE'])
def deleteCoordinate(id):
    token = request.cookies.get('access_token')
    data = request.json

    if not data or not isinstance(data, list):
        return jsonify({"message": "Brak danych do usunięcia"})

    ids = ",".join(map(str, data))

    try:
        response = requests.get(
            f"http://localhost:8888/rest/map/v1/deleteCoordinates/{ids}",
            headers={"Content-Type": "application/json",
                     "Authorization": f"Bearer {token}"})

        content, status = getPlacesForVist(id, token)

        if content:
            loadMap(content, False)
        else:
            content = [{'latitude': session.get('latitude'), 'longitude': session.get('longitude')}]
            loadMap(content, True)

        return response.content, response.status_code
    except requests.RequestException:
        return jsonify({"message": "Nie mogę usunąć współrzędnych"}), 400


@map_api.route('/findroad/<int:id>', methods=['GET'])
def find_road(id):
    token = request.cookies.get('access_token')
    try:
        response = generateRoad(id, token)

        if response.status_code == 400:
            return jsonify({"message": "Nie mogę wygenerować trasy dla tych współrzędnych"}), 400
        elif response.status_code == 406:
            return jsonify({"message": "Musisz wprowadzić co najmniej 2 punkty"}), 406
        elif response.status_code == 500:
            return jsonify({"message": "Aktualnie nie mogę wygenerować trasy spróbuj ponownie poźniej"}), 500

        route = response.json()['trips'][0]
        loadRouteMap(route['geometry'], route['distance'], route['duration'], route['legs'])
        return redirect('/map/display_route')
    except requests.RequestException:
        return jsonify({"message": "Błąd komunikacji pomiędzy serwerem"}), 500


@map_api.route('/findroad/legs/<int:id>', methods=['GET'])
def getLegs(id):
    token = request.cookies.get('access_token')

    try:
        response = requests.get(
            f"http://localhost:8888/rest/map/v1/getRoute/legs/{id}",
            headers={"Content-Type": "application/json",
                     "Authorization": f"Bearer {token}"})

        if response.status_code == 400:
            return jsonify({"message": "Nie mogę wygenerować szczegółów trasy dla tych współrzędnych"}), 400
        elif response.status_code == 406:
            return jsonify({"message": "Trasa nie zawiera co najmniej 2 punktów aby wyświetlić szczegóły"}), 406
        elif response.status_code == 500:
            return jsonify({"message": "Aktualnie nie mogę wygenerować szczegółów trasy spróbuj ponownie poźniej"}), 500

        route = response.json()

        return route['trips'][0]['legs']
    except requests.RequestException:
        return jsonify({"message": "Nie można znaleźć trasy dla tych współrzędnych"}), 400


@map_api.route('/verify', methods=['POST'])
def verify():
    data = request.json
    user = session.get('user')
    try:
        response = requests.put(
            f"http://localhost:8888/rest/map/v1/coordinatesVerify/{user['id']}",
            json=data,
            headers={
                'Content-Type': 'application/json',
                "Authorization": f"Bearer {user['token']}"}
        )
        return response.content, response.status_code
    except requests.RequestException:
        return jsonify({"message": "Nie mogę dodać współrzędnych"}), 400


@map_api.route("/selectCoordinates", methods=['GET'])
def selectCoordinates():
    lat = float(session.get("latitude", 52.237049))
    lon = float(session.get("longitude", 21.017532))

    m_name = loadMap()

    return render_template('/Dashboard/SelectCoordinates/SelectCoordinates.html', m_name=m_name,
                           location={"latitude": lat, "longitude": lon})


@map_api.route('/display_route')
def display_route():
    return render_template('/Map/Result/DisplayRoute.html')
