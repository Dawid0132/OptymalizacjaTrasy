from flask import Blueprint, render_template, session, redirect, url_for, request

dashboard_api = Blueprint('Dashboard_api', __name__, template_folder='templates', url_prefix='/dashboard')


@dashboard_api.before_request
def check_auth():
    if request.endpoint in UNPROTECTED_ENDPOINTS:
        if session.get('authenticated') is True:
            return redirect(url_for('dashboard_api.index'))
        return

    if session.get('authenticated') is not True:
        return redirect(url_for('user_api.login'))
    return


@dashboard_api.route("/")
def index():
    return render_template('Dashboard/Dashboard.html')
