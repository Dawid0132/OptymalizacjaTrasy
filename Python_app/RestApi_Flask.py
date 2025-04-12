from flask import Blueprint, Flask, jsonify, render_template, redirect, request, url_for
import folium
from folium.plugins import MousePosition
import os
from dotenv import load_dotenv
from bs4 import BeautifulSoup
import logging
import requests

bp = Blueprint('RestApi_Flask', __name__, template_folder='templates')

load_dotenv()

logging.basicConfig(level=logging.INFO)

places_for_visit = []


def get_route_osrm(route):
    url = "http://router.project-osrm.org/trip/v1/driving/"
    coordinates_str = ";".join([f"{place['longitude']},{place['latitude']}" for place in places_for_visit])

    params = {
        "geometries": "geojson",
        "overview": "full",
        "steps": "true",
        "annotations": "true"
    }

    try:
        logging.info(f"Requesting route from OSRM with coords: {route}")
        response = requests.get(url + coordinates_str, params=params)
        response.raise_for_status()
        data = response.json()
        if data.get('trips'):
            logging.info("Route successfully retrieved")
            route = data['trips'][0]
            return route['geometry'], route['duration'], route['distance'], route['legs']
        else:
            logging.warning("No routes found in OSRM response")
            return None
    except requests.exceptions.RequestException as e:
        logging.error(f"Error fetching route from OSRM: {e}")
        return None


def update_map(places_for_visit):
    if places_for_visit:
        last_element = len(places_for_visit) - 1
        m = folium.Map(location=[float(places_for_visit[last_element]['latitude']),
                                 float(places_for_visit[last_element]['longitude'])],
                       zoom_start=12)
        for idx, place in enumerate(places_for_visit, start=1):
            folium.Marker([float(place['latitude']), float(place['longitude'])],
                          popup=f"{place['latitude']},{place['longitude']}",
                          tooltip=f"{idx}").add_to(m)

        m.add_child(folium.LatLngPopup())

        mouse_position = MousePosition(position='bottomright', separator=' | ', prefix="Lat, Lng: ", num_digits=6)

        m.add_child(mouse_position)

        map_html_path = os.path.join('static', 'map_help.html')

        try:
            m.save(map_html_path)
            print(f"Mapa zapisana: {map_html_path}")
        except Exception as e:
            print(f"Błąd podczas zapisywania mapy: {e}")
        return jsonify({"error": "Failed to save the map"}), 500


@bp.route('/')
def index():
    return jsonify({'message': 'Hello World!'}, 200)


@bp.route('/map', methods=['GET'])
def show_map():
    global places_for_visit

    if places_for_visit:
        update_map(places_for_visit)
    else:
        lat = request.args.get("lat", 52.237049)
        lon = request.args.get("lon", 21.017532)

        m = folium.Map(location=[lat, lon], zoom_start=12)
        folium.Marker([lat, lon], popup="Twoja lokalizacja", tooltip="Jesteś tutaj").add_to(m)

        m.add_child(folium.LatLngPopup())

        mouse_position = MousePosition(position='bottomright', separator=' | ', prefix="Lat, Lng: ", num_digits=6)
        m.add_child(mouse_position)

        map_html_path = os.path.join('static', f'map_help.html')

        try:
            m.save(map_html_path)
            print(f"Mapa zapisana: {map_html_path}")
        except Exception as e:
            print(f"Błąd podczas zapisywania mapy: {e}")
            return jsonify({"error": "Failed to save the map"}), 500


    return redirect(url_for('RestApi_Flask.home_page'))


@bp.route("/homePage", methods=['GET'])
def home_page():
    return render_template('index.html', map_file="map_help.html")


@bp.route("/displayRoute", methods=['GET'])
def display_route():
    return render_template('display_route.html')


app = Flask(__name__)
app.register_blueprint(bp, url_prefix='/api_blueprint')

if __name__ == '__main__':
    app.run(debug=True)
