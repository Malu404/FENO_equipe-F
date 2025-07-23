from flask import Flask
from flask_sqlalchemy import SQLAlchemy

app = Flask(__name__)
db = SQLAlchemy()

# Configuração de conexão
# TODO: Esconder configurações de usuário e senha
app.config['SQLALCHEMY_DATABASE_URI'] = 'postgresql://pep:123@localhost:5432/postgres'

db.init_app(app)
