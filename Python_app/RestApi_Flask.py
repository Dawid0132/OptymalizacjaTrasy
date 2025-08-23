from flask import Blueprint, Flask, render_template

from auth_utils import token_hash_match_not_required

bp = Blueprint('RestApi_Flask', __name__, template_folder='templates', url_prefix='/smartroute')


@bp.route('/homePage')
@token_hash_match_not_required
def home_page():
    return render_template('HomePage/HomePage.html')


from user_api import user_api
from map_api import map_api
from dashboard_api import dashboard_api
from trip_api import trip_api

app = Flask(__name__)
app.secret_key = 'secret_key'
app.register_blueprint(bp)
app.register_blueprint(user_api)
app.register_blueprint(map_api)
app.register_blueprint(dashboard_api)
app.register_blueprint(trip_api)

if __name__ == '__main__':
    app.run(debug=True)
