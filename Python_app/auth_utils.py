import hashlib
from functools import wraps
from flask import request, session, redirect, url_for

SECRET_KEY = 'secret_key'


def token_hash_match_required(func):
    @wraps(func)
    def wrapper(*args, **kwargs):
        token_cookie = request.cookies.get('access_token')
        token_hash_session = session.get('jwt_token')

        if not token_cookie or not token_hash_session:
            return redirect(url_for('user_api.login'))

        if token_cookie.startswith('Bearer '):
            token_cookie = token_cookie[7:]

        computed_hash = hashlib.sha256(token_cookie.encode()).hexdigest()

        if computed_hash != token_hash_session:
            return redirect(url_for('user_api.login'))

        return func(*args, **kwargs)

    return wrapper


def token_hash_match_not_required(func):
    @wraps(func)
    def wrapper(*args, **kwargs):

        token_cookie = request.cookies.get('access_token')

        token_hash_session = session.get('jwt_token')

        if token_cookie and token_cookie.startswith('Bearer '):
            token_cookie = token_cookie[7:]

        if token_cookie:
            computed_hash = hashlib.sha256(token_cookie.encode()).hexdigest()
        else:
            computed_hash = None

        if computed_hash and computed_hash == token_hash_session:
            return redirect(url_for('dashboard_api.index'))

        return func(*args, **kwargs)

    return wrapper