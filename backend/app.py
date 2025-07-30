from . import app, db
from flask import request, make_response
from .modelo import Cliente, Monitoria, Disciplina
from werkzeug.security import generate_password_hash, check_password_hash
import jwt
from datetime import datetime, timedelta
from functools import wraps


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


@app.route('/login', methods=['POST'])
def entrar():
    auth = request.json
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
        token = jwt.encode({
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


def requer_token(func):
    @wraps(func)
    def decorado(*args, **kwargs):
        token = None
        auth_header = request.headers.get('Authorization')

        if auth_header and auth_header.startswith('Bearer '):
            token = auth_header.split(' ')[1]

        if not token:
            return make_response('Token faltante', 402)

        try:
            data = jwt.decode(token, 'segredo', algorithms=['HS256'])
            usuario = Cliente.query.filter_by(id=data['id']).first()
            if not usuario:
                return make_response('Usuário não encontrado', 403)
        except Exception as e:
            print("Erro ao decodificar token:", e)
            return make_response('Token inválido', 405)

        return func(usuario, *args, **kwargs)
    return decorado


@app.route('/clientes/<int:id>', methods=['GET'])
@requer_token
def obter_cliente(usuario, id):
    try:
        cliente = Cliente.query.filter_by(id=id).first()
        if not cliente:
            return make_response({'mensagem': 'Cliente não encontrado'}, 404)
        return make_response({'dados': cliente.serialize}, 200)
    except Exception as e:
        print(e)
        return make_response({'mensagem': 'Erro ao buscar cliente'}, 500)


# Rotas das monitorias


@app.route('/monitorias', methods=['GET'])
@requer_token
def obter_todas_monitorias(usuario):
    data_str = request.args.get('data')
    query = Monitoria.query
    if data_str:
        try:
            data = datetime.strptime(data_str, "%Y-%m-%d")
            prox_dia = data + timedelta(days=1)
            query = query.filter(
                Monitoria.data_hora >= data,
                Monitoria.data_hora < prox_dia
            )
        except ValueError:
            return make_response(
                {'erro': 'Formato inválido. Use: YYYY-MM-DD'}, 400
            )
    monitorias = query.all()
    return make_response(
        {'data': [tupla.serialize for tupla in monitorias]}, 200
    )


@app.route('/monitorias/<int:id>', methods=['GET'])
@requer_token
def obter_monitoria(usuario, id):
    monitoria = Monitoria.query.filter_by(id=id).first()
    if not monitoria:
        return make_response({'erro': 'Monitoria não encontrada'}, 404)
    return make_response({'data': monitoria.serialize}, 200)


@app.route('/monitorias', methods=['POST'])
@requer_token
def criar_monitoria(usuario):
    monitoria = request.json
    monitor_id = monitoria.get('monitor_id')
    if not monitoria.get('disciplina'):
        return make_response({'Obrigatório o campo disciplina', 400})
    if monitor_id and not Cliente.query.filter_by(id=monitor_id).first():
        return make_response({'Monitor não encontrado', 404})
    monitoria = Monitoria(
        data_hora=monitoria.get('data_hora'),
        descricao=monitoria.get('descricao'),
        disciplina=monitoria.get('disciplina'),
        monitor_id=monitor_id
    )
    db.session.add(monitoria)
    db.session.commit()
    return monitoria.serialize


@app.route('/monitorias/<id>', methods=['PATCH'])
@requer_token
def alterar_monitoria(usuario, id):
    try:
        monitoria = Monitoria.query.filter_by(id=id).first()
        if not monitoria:
            return make_response({'Monitoria não encontrada'}, 409)
        data = request.json
        for campo in ['data_hora', 'descricao', 'disciplina', 'monitor_id']:
            if campo in data:
                setattr(monitoria, campo, data[campo])
        db.session.commit()
        return make_response({'data': monitoria.serialize}, 200)
    except Exception as e:
        print(e)
        return make_response({'Não foi possível processar'}, 409)


@app.route('/monitorias/<id>', methods=['DELETE'])
@requer_token
def deletar_monitoria(usuario, id):
    try:
        monitoria = Monitoria.query.filter_by(id=id).first()
        if not monitoria:
            return make_response({'Monitoria não encontrada'}, 409)
        db.session.delete(monitoria)
        db.session.commit()
        return make_response({'Monitoria deletada', 200})
    except Exception as e:
        print(e)
        return make_response({'Não foi possível processar', 409})


# Rotas para disciplinas


@app.route('/disciplinas', methods=['GET'])
@requer_token
def obter_disciplinas(usuario):
    disciplinas = Disciplina.query.all()
    return make_response(
        {'data': [tupla.serialize for tupla in disciplinas]}, 200
    )


@app.route('/disciplinas/<id>', methods=['GET'])
@requer_token
def obter_disciplina(usuario, id):
    try:
        disciplina = Disciplina.query.filter_by(id=id).first()
        if not disciplina:
            return make_response(
                {'mensagem': 'Disciplina nao encontrada'}, 404
            )
        return make_response({'dados': disciplina.serialize}, 200)
    except Exception as e:
        print(e)
        return make_response({'mensagem': 'Não foi possível processar'}, 404)


