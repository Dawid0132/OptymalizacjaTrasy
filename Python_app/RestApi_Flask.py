from flask import Blueprint, Flask, jsonify, render_template, redirect, request, url_for, session
import folium
from folium.plugins import MousePosition
import os
from dotenv import load_dotenv
import logging
import requests
from jinja2 import Template

bp = Blueprint('RestApi_Flask', __name__, template_folder='templates', url_prefix='/smartroute')

load_dotenv()

logging.basicConfig(level=logging.INFO)

places_for_visit = []
legs = []

UNPROTECTED_ENDPOINTS = ['RestApi_Flask.home_page', 'User_api.login', 'User_api.register',
                         'User_api.auth_login']

@bp.before_request
def check_auth():
    if request.endpoint in UNPROTECTED_ENDPOINTS:
        if session.get('authenticated') is True:
            return redirect(url_for('dashboard_api.index'))
        return

    if session.get('authenticated') is not True:
        return redirect(url_for('user_api.login'))
    return


@bp.before_request
def check_auth():
    if request.endpoint in UNPROTECTED_ENDPOINTS:
        if session.get('authenticated') is True:
            return redirect(url_for('dashboard_api.index'))
        return

    if session.get('authenticated') is not True:
        return redirect(url_for('user_api.login'))
    return


@bp.route('/homePage')
def home_page():
    return render_template('HomePage/HomePage.html')


from user_api import user_api
from map_api import map_api
from dashboard_api import dashboard_api

app = Flask(__name__)
app.secret_key = 'secret_key'
app.register_blueprint(bp)
app.register_blueprint(user_api)
app.register_blueprint(map_api)
app.register_blueprint(dashboard_api)

if __name__ == '__main__':
    app.run(debug=True)
