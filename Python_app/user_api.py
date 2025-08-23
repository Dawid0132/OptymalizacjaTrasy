import hashlib

import requests
from flask import Blueprint, request, render_template, session, jsonify
from auth_utils import token_hash_match_required, token_hash_match_not_required

user_api = Blueprint('user_api', __name__, template_folder='templates', url_prefix='/user')


@user_api.route('/login', methods=['GET'])
@token_hash_match_not_required
def login():
    return render_template('Credentials/Login/Login.html')


@user_api.route('/register', methods=['GET'])
@token_hash_match_not_required
def register():
    return render_template('Credentials/Register/Register.html')


@user_api.route('/signin', methods=['POST'])
@token_hash_match_not_required
def signin():
    data = request.get_json()
    try:
        response = requests.post(
            'http://atsps:8888/auth/login',
            json=data,
            headers={'Content-Type': 'application/json'}
        )

        if response.status_code == 200:
            response_data = response.json()
            token = response_data.get('token')

            if not token:
                return jsonify({"message": "Token not found in response"}), 500

            if token.startswith("Bearer "):
                raw_token = token[7:]
            else:
                raw_token = token

            hashed_token = hashlib.sha256(raw_token.encode()).hexdigest()
            session['jwt_token'] = hashed_token
            return response_data
        else:
            return jsonify({"message": "Not authenticated"}), 401

    except requests.RequestException:
        return jsonify({"message": "Bad request"}), 400

@user_api.route('/profile', methods=['GET'])
@token_hash_match_required
def profile():
    return render_template('/Dashboard/Profile/Profile.html')


@user_api.route('/location', methods=['POST'])
@token_hash_match_required
def location():
    data = request.get_json()
    session['latitude'] = data.get("latitude")
    session['longitude'] = data.get("longitude")
    return jsonify({"status": "OK"}), 200


@user_api.route('/signout/<int:id>', methods=['GET'])
@token_hash_match_required
def signout(id):
    token = request.cookies.get('access_token')

    try:
        response = requests.get(
            f'http://atsps:8888/rest/user/v1/{id}/logout',
            headers={'Content-Type': 'application/json',
                     "Authorization": f"Bearer {token}"}
        )

        if response.status_code == 200:
            session.clear()
            return jsonify({"message": "You have been sign out"}), 200
        else:
            return jsonify({"message": "Not authenticated"}), 401
    except requests.RequestException:
        return jsonify({"message": "Bad request"}), 400