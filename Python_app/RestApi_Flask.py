from flask import Blueprint, Flask, jsonify
from dotenv import load_dotenv
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


@bp.route('/')
def index():
    return jsonify({'message': 'Hello World!'}, 200)


app = Flask(__name__)
app.register_blueprint(bp, url_prefix='/api_blueprint')

if __name__ == '__main__':
    app.run(debug=True)
