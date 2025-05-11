import requests
from flask import Blueprint, render_template, session, jsonify, request, redirect, url_for

user_api = Blueprint('user_api', __name__, template_folder='templates', url_prefix='/user')


@user_api.route('/login', methods=['GET'])
def login():
    return render_template('Credentials/Login/Login.html')


@user_api.route('/register', methods=['GET'])
def register():
    return render_template('Credentials/Register/Register.html')


@user_api.route('/auth/login', methods=['POST'])
def auth_login():
    data = request.json

    try:
        response = requests.post(
            'http://localhost:8888/auth/login',
            json=data,
            headers={'Content-Type': 'application/json'}
        )
        session['authenticated'] = True
        session['user'] = response.json()
        return response.content, response.status_code
    except requests.RequestException:
        return jsonify({"message": "Not authenticated"}), 401


@user_api.route('/profile', methods=['GET'])
def profile():
    return render_template('/Dashboard/Profile/Profile.html')


@user_api.route('/password/<int:id>')
def verifyPassword(id):
    data = request.json
    token = request.cookies.get('access_token')

    try:
        response = requests.get(
            f'http://localhost:8888/rest/user/v1/getPassword/{id}',
            json=data,
            headers={'Content-Type': 'application/json',
                     'Authorization': f'Bearer {token}'}
        )
    except requests.RequestException:
        return jsonify({"message": "Not authenticated"}), 401


@user_api.route('/logout', methods=['POST'])
def logout():
    session.clear()
    return redirect(url_for('direction_api.home_page'))