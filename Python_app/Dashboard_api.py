from flask import Blueprint, render_template, session, redirect, url_for, request

dashboard_api = Blueprint('dashboard_api', __name__, template_folder='templates', url_prefix='/dashboard')


@dashboard_api.route("/")
def index():
    return render_template('Dashboard/Dashboard.html')
