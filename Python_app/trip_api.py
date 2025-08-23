import os.path

import requests
from flask import Blueprint, request, jsonify, render_template, current_app

from map_api import previewRoadGenerate, if_file_exists
from auth_utils import token_hash_match_required

trip_api = Blueprint('trip_api', __name__, template_folder='templates', url_prefix='/trip')


@trip_api.route('/history', methods=['GET'])
@token_hash_match_required
def history():
    return render_template('/Dashboard/History/History.html')


@trip_api.route('/upcoming', methods=['GET'])
@token_hash_match_required
def upcoming():
    return render_template('/Dashboard/UpComing/UpComing.html')


@trip_api.route('/upcoming/delete/<int:id>', methods=['DELETE'])
def delete(id):
    trip_id = request.args.get('trip_id')
    map_name = request.args.get('map_name')


    token = request.cookies.get('access_token')

    try:
        response = requests.get(f"http://atsps:8888/rest/map/v1/{id}/trips/delete",
                                headers={
                                    "Content-Type": "application/json",
                                    "Authorization": f"Bearer {token}"
                                },
                                params={'trip_id': trip_id})

        if response.status_code == 200:
            response_data = response.json()

            path = f"Dashboard/UpComing/Maps/{map_name}.html"

            if if_file_exists(path):
                path = os.path.join(current_app.static_folder, path)
                os.remove(path)
            else:
                print(f"Nie znaleziono mapy {path}")

            return jsonify(response_data), 200
        else:
            return jsonify({"message": "Aktualnie nie możesz usunąć trasy. Spróbuj ponownie później."}), 400
    except requests.RequestException:
        return jsonify({"message": "Aktualnie nie możesz usunąć trasy. Spróbuj ponownie później."}), 400


@trip_api.route('/upcoming/preview', methods=['GET'])
def display_preview():
    map_name = request.args.get("map_name")
    id_map = request.args.get("map_id")
    path = f"/Dashboard/UpComing/Maps/{map_name}.html"
    return render_template('/Dashboard/UpComing/Details/DetailsForTrip.html', path_to_map=path, map_name=map_name,
                           id_map=id_map)


@trip_api.route('/upcoming/preview/<int:id>', methods=['GET'])
@token_hash_match_required
def preview(id):
    global m_name
    trip_id = request.args.get('trip_id')
    token = request.cookies.get('access_token')

    if trip_id is None:
        return {"error": "Brak parametru id"}, 400

    try:
        response = requests.get(f"http://atsps:8888/rest/map/v1/{id}/getRoute/trip?trip_id={trip_id}",
                                headers={
                                    "Content-Type": "application/json",
                                    "Authorization": f"Bearer {token}"
                                })

        response_data = response.json()
        route = response_data['osrm']['trips'][0]
        map_name = response_data['map_name']

        path = f"Dashboard/UpComing/Maps/{map_name}.html"

        m_name = previewRoadGenerate(route['geometry'], route['distance'], route['duration'], path)

        return jsonify({"map_name": map_name, "frame_name": m_name}), 200
    except requests.RequestException:
        return jsonify({"message": "Błąd komunikacji pomiędzy serwerem"}), 500
