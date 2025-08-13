from flask import Blueprint, render_template

trip_api = Blueprint('trip_api', __name__, template_folder='templates', url_prefix='/trip')


@trip_api.route('/history', methods=['GET'])
def history():
    return render_template('/Dashboard/History/History.html')


@trip_api.route('/upcoming', methods=['GET'])
def upcoming():
    return render_template('/Dashboard/UpComing/UpComing.html')
