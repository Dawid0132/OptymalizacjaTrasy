import os
import uuid

import requests
from flask import Blueprint, request, jsonify, session, redirect, render_template, url_for, make_response, current_app
from folium import folium
from folium import LatLngPopup, GeoJson, Element
from folium.plugins import MousePosition
from auth_utils import token_hash_match_required

map_api = Blueprint('map_api', __name__, template_folder='templates', url_prefix='/map')


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


def previewRoadGenerate(geometry, distance, duration, map_name):
    m = folium.Map(location=[geometry['coordinates'][0][1], geometry['coordinates'][0][0]], zoom_start=11)

    mouse_position = MousePosition(position='bottomright', separator=' | ', prefix="Lat, Lng: ", num_digits=6)
    m.add_child(mouse_position)

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

    GeoJson(
        geometry,
        style_function=lambda feature, color="#6E64FB": {
            'fillColor': color,
            'color': color,
            'weight': 3,
            'opacity': 1
        }
    ).add_to(m)

    m_name = m.get_name()

    map_html_path = os.path.join(current_app.root_path, 'static', map_name)

    try:
        m.save(map_html_path)
        print(f"Mapa zapisana: {map_html_path}")
        return m_name
    except Exception as e:
        print(f"Błąd podczas zapisywania mapy: {e}")
        return jsonify({"error": "Failed to save the map"}), 500


def if_file_exists(filename):
    path = os.path.join(current_app.static_folder, filename)
    return os.path.isfile(path)


def clear_folder(folder_path):
    folder_path = os.path.join(current_app.static_folder, folder_path)

    if not os.path.exists(folder_path):
        return

    for filename in os.listdir(folder_path):
        file_path = os.path.join(folder_path, filename)
        try:
            if os.path.isfile(file_path):
                os.remove(file_path)
        except Exception as e:
            print(f"Nie udało się usunąć {file_path}: {e}")


@map_api.route("/selectCoordinates", methods=['GET'])
@token_hash_match_required
def selectCoordinates():
    lat = float(session.get("latitude", 52.237049))
    lon = float(session.get("longitude", 21.017532))

    m_name = loadMap()

    return render_template('/Dashboard/SelectCoordinates/SelectCoordinates.html', m_name=m_name,
                           location={"latitude": lat, "longitude": lon})


@map_api.route("/selectCoordinates/save", methods=['DELETE'])
@token_hash_match_required
def saveMap():
    map_name = request.args.get('map_name')
    path_to_map = f"Dashboard/Result/Maps/{map_name}.html"

    file_path = os.path.join(current_app.static_folder, path_to_map)

    try:
        if if_file_exists(file_path):
            os.remove(file_path)
            return jsonify({"message": f"Plik {map_name} został usunięty."}), 200
        else:
            return jsonify({"error": f"Plik {map_name} nie istnieje"}), 404
    except Exception as e:
        return jsonify({"error": str(e)}), 500


@map_api.route('/display_route', methods=['GET'])
@token_hash_match_required
def display_route():
    map_name = request.args.get("map_name")
    path = f"Dashboard/Result/Maps/{map_name}.html"

    if if_file_exists(path):
        return render_template('/Dashboard/Result/DisplayRoute.html', map_name=map_name, path_to_map=path)
    else:
        return redirect(url_for('map_api.selectCoordinates'))


@map_api.route('/generateRoad/<int:id>', methods=['GET'])
@token_hash_match_required
def generateRoad(id):
    token = request.cookies.get('access_token')
    try:
        response = requests.get(f"http://atsps:8888/rest/map/v1/{id}/getRoute",
                                headers={
                                    "Content-Type": "application/json",
                                    "Authorization": f"Bearer {token}"
                                })

        if response.status_code == 200:
            route = response.json()['trips'][0]
            lucky_name = uuid.uuid4()
            directory_path = "Dashboard/Result/Maps"
            path = f"Dashboard/Result/Maps/{lucky_name}.html"
            clear_folder(directory_path)
            previewRoadGenerate(route['geometry'], route['distance'], route['duration'], path)
            return jsonify({"route": route, "map_name": lucky_name}), 200
        elif response.status_code == 404:
            return jsonify({"message": "Aktualnie nie możemy wygenerować trasy"}), 404
        elif response.status_code == 406:
            return jsonify({"message": "Musisz wprowadzić co najmniej dwie współrzędne"}), 406
        else:
            return jsonify({"message": "Aktualnie nie możemy wygenerować trasy"}), 400

    except requests.RequestException:
        return jsonify({"message": "Błąd komunikacji pomiędzy serwerem"}), 500
