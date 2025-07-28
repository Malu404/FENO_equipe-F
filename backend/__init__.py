from flask import Flask
from flask_sqlalchemy import SQLAlchemy
from os import environ

app = Flask(__name__)
db = SQLAlchemy()

# Configuração de conexão
# TODO: Esconder configurações de usuário e senha
app.config['SQLALCHEMY_DATABASE_URI'] = environ.get('DB_URL')
db.init_app(app)
