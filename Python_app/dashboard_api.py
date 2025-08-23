from flask import Blueprint, render_template
from auth_utils import token_hash_match_required

dashboard_api = Blueprint('dashboard_api', __name__, template_folder='templates', url_prefix='/dashboard')




@dashboard_api.route("/")
@token_hash_match_required
def index():
    return render_template('Dashboard/Dashboard.html')
