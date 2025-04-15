from flask import Blueprint, Flask, jsonify, render_template, redirect, request, url_for
import folium
from folium.plugins import MousePosition
import os
from dotenv import load_dotenv
import logging
import requests
from jinja2 import Template

bp = Blueprint('RestApi_Flask', __name__, template_folder='templates')

load_dotenv()

logging.basicConfig(level=logging.INFO)

places_for_visit = []
legs = []


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


def update_map(places):
    if places:
        last_element = len(places) - 1
        m = folium.Map(location=[float(places[last_element]['latitude']),
                                 float(places[last_element]['longitude'])],
                       zoom_start=12)
        for idx, place in enumerate(places, start=1):
            folium.Marker([float(place['latitude']), float(place['longitude'])],
                          popup=f"{place['latitude']},{place['longitude']}",
                          tooltip=f"{idx}").add_to(m)

        m.add_child(folium.LatLngPopup())

        mouse_position = MousePosition(position='bottomright', separator=' | ', prefix="Lat, Lng: ", num_digits=6)

        m.add_child(mouse_position)

        m_name = m.get_name()

        template_string = '''
            <script>
            document.addEventListener('DOMContentLoaded', function () {
                const name = {{ m_name | tojson }}
                
                const map = window[name]
                
                map.on('click',function(e){
                    const lat = e.latlng.lat.toFixed(4)
                    const lng = e.latlng.lng.toFixed(4)
                    
                    const data = {
                        latitude: lat,
                        longitude: lng
                    }
                    
                    fetch('/api_blueprint/verify',{
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        body: JSON.stringify(data)
                    })
                    .then(response => response.json())
                    .then(data => {})
                    .catch((e) => {
                        console.error(e)
                    })      
                })
            })
            </script>
        '''

        template = Template(template_string)
        click_js = template.render(m_name=m_name)

        m.get_root().html.add_child(folium.Element(click_js))

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

        m_name = m.get_name()

        template_string = '''
                    <script>
                    document.addEventListener('DOMContentLoaded', function () {
                        const name = {{ m_name | tojson }}

                        const map = window[name]

                        map.on('click',function(e){
                            const lat = e.latlng.lat.toFixed(4)
                            const lng = e.latlng.lng.toFixed(4)

                            const data = {
                                latitude: lat,
                                longitude: lng
                            }

                            fetch('/api_blueprint/verify',{
                                method: 'POST',
                                headers: {
                                    'Content-Type': 'application/json'
                                },
                                body: JSON.stringify(data)
                            })
                            .then(response => response.json())
                            .then(data => {})
                            .catch((e) => {
                                console.error(e)
                            })      
                        })
                    })
                    </script>
                '''

        template = Template(template_string)
        click_js = template.render(m_name=m_name)

        m.get_root().html.add_child(folium.Element(click_js))

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


@bp.route('/legs', methods=['GET'])
def get_legs():
    return jsonify({legs}, 200)


@bp.route('/verify', methods=['POST'])
def verify():
    data = request.get_json()
    latitude = data.get('latitude', None)
    longitude = data.get('longitude', None)

    if not latitude or not longitude:
        return jsonify({"error": "Missing parameters"}), 400

    global location

    new_location = {
        "latitude": latitude,
        "longitude": longitude
    }

    location = new_location

    return jsonify({"message": "verify completed"}, 200)


@bp.route('/findRoad', methods=['POST'])
def find_road():
    data = request.get_json()
    places = data.get("places", {})

    if len(places) < 2:
        return jsonify({"error": "At least two places are required"})

    global legs

    geometry, duration, distance, legs = get_route_osrm(places_for_visit)

    m = folium.Map(location=(places_for_visit[0]['latitude'], places_for_visit[0]['longitude']), zoom_start=12)

    legend_html = f'''
    <div style="position: fixed; 
                 bottom: 10px; left: 10px; width: 160px; height: auto; 
                 background-color: white; border:2px solid grey; z-index:1;
                 font-size: 12px; padding: 10px;">
     <b>Route Legend</b><br>
     -------------------
     <br><b>Total Distance:</b> {int(distance // 1000)}km {int(distance % 1000)}m<br>
     <b>Total Duration:</b> {int(duration // 3600)}h {int((duration % 3600) // 60)}min<br>
     </div>
    '''

    m.get_root().html.add_child(folium.Element(legend_html))

    folium.GeoJson(
        geometry,
        style_function=lambda feature, color="#6E64FB": {
            'fillColor': color,
            'color': color,
            'weight': 3,
            'opacity': 1
        }
    ).add_to(m)

    map_html_path = os.path.join('static', 'generatedMap.html')

    try:
        m.save(map_html_path)
        print(f"Mapa zapisana: {map_html_path}")
    except Exception as e:
        print(f"Błąd podczas zapisywania mapy: {e}")
        return jsonify({"error": "Failed to save the map"}), 500

    return redirect(url_for('RestApi_Flask.display_route'))


@bp.route("/displayRoute", methods=['GET'])
def display_route():
    return render_template('display_route.html', map_file="generatedMap.html")


app = Flask(__name__)
app.register_blueprint(bp, url_prefix='/api_blueprint')

if __name__ == '__main__':
    app.run(debug=True)
