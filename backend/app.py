from . import app, db
from flask import request, make_response
from .modelo import Cliente
from werkzeug.security import generate_password_hash, check_password_hash
import jwt
from datetime import datetime, timedelta


@app.route('/cadastro', methods=['POST'])
def cadastro():
    data = request.json

    cpf = data.get('cpf')
    nome = data.get('nome')
    matricula = data.get('matricula')
    email = data.get('email')
    senha = data.get('senha')
    ano_ingresso = data.get('ano_ingresso')

    if cpf and nome and matricula and email and senha and ano_ingresso:
        cliente = Cliente.query.filter_by(email=email).first()
        if cliente:
            return make_response(
                'Usuário já cadastrado.', 200
            )
        cliente = Cliente(
            cpf=cpf,
            nome=nome,
            matricula=matricula,
            email=email,
            senha=generate_password_hash(senha),
            ano_ingresso=ano_ingresso
        )
        db.session.add(cliente)
        db.session.commit()
        return make_response(
            'Usuário cadastrado.', 201
        )
    return make_response(
        'Não foi possível criar o usuário.', 500
    )


@app.route('/login', method=['POST'])
def entrar():
    auth = request.json()
    if not auth or not auth.get('email') or not auth.get('senha'):
        return make_response(
            'Credenciais não foram fornecidas', 401
        )
    cliente = Cliente.query.filter_by(email=auth.get('email')).first()
    if not cliente:
        return make_response(
            'Esse usuário não existe, crie um usuário', 401
        )
    if check_password_hash(cliente.senha, auth.get('senha')):
        token = jwt.decode({
            'id': cliente.id,
            'exp': datetime.utcnow() + timedelta(minutes=60)
        },
        # TODO: gerar algo mais seguro
        'segredo',
        'HS256'
        )
        return make_response({'token': token}, 201)
    return make_response(
        'Verifique suas credenciais',
        401
    )
