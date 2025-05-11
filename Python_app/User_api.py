from flask import Blueprint, render_template

user_api = Blueprint('user_api', __name__, template_folder='templates', url_prefix='/user')


@user_api.route('/login', methods=['GET'])
def login():
    return render_template('Credentials/Login/Login.html')


@user_api.route('/register', methods=['GET'])
def register():
    return render_template('Credentials/Register/Register.html')