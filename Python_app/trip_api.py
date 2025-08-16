from flask import Blueprint, render_template, request

trip_api = Blueprint('trip_api', __name__, template_folder='templates', url_prefix='/trip')


@trip_api.route('/history', methods=['GET'])
def history():
    return render_template('/Dashboard/History/History.html')


@trip_api.route('/upcoming', methods=['GET'])
def upcoming():
    return render_template('/Dashboard/UpComing/UpComing.html')


@trip_api.route('/upcoming/preview', methods=['GET'])
def display_preview():
    map_name = request.args.get("map_name")
    id_map = request.args.get("map_id")
    path = f"/Dashboard/UpComing/Maps/{map_name}.html"
    return render_template('/Dashboard/UpComing/Details/DetailsForTrip.html', path_to_map=path, map_name=map_name,
                           id_map=id_map)
