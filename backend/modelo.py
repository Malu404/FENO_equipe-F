from . import db, app
from sqlalchemy.sql import func


class Disciplina(db.Model):
    __tablename__ = 'disciplina'
    id = db.Column(db.Integer, primary_key=True)
    nome = db.Column(db.String(50), nullable=False)
    codigo = db.Column(db.String(50), nullable=False)
    data_criacao = db.Column(db.DateTime(timezone=True), server_default=func.now())

    @property
    def serialize(self):
        return {
            'id': self.id,
            'codigo': self.codigo,
            'nome': self.nome
        }


class Usuario():
    cpf = db.Column(db.String(11), unique=True, nullable=False)
    nome = db.Column(db.String(200), nullable=False)
    matricula = db.Column(db.String(6), unique=True, nullable=False)
    email = db.Column(db.String(100), unique=True, nullable=False)
    senha = db.Column(db.String(200), nullable=False)


class Administrador(db.Model, Usuario):
    __tablename__ = 'administrador'
    id = db.Column(db.Integer, primary_key=True)
    data_criacao = db.Column(db.DateTime(timezone=True), server_default=func.now())


class Cliente(db.Model, Usuario):
    __tablename__ = 'cliente'
    id = db.Column(db.Integer, primary_key=True)
    ano_ingresso = db.Column(db.Integer, nullable=False)
    # disciplina???
    data_criacao = db.Column(db.DateTime(timezone=True), server_default=func.now())

    monitorias = db.relationship('Monitoria', backref='monitor', lazy=True)


class Monitoria(db.Model):
    __tablename__ = 'monitoria'
    id = db.Column(db.Integer, primary_key=True)
    data_hora = db.Column(db.DateTime)
    descricao = db.Column(db.String(100))
    disciplina = db.Column(db.Integer, db.ForeignKey('disciplina.id'), nullable=False)
    monitor_id = db.Column(db.Integer, db.ForeignKey('cliente.id'))
    data_criacao = db.Column(db.DateTime(timezone=True), server_default=func.now())

    certificado = db.relationship('Certificado', backref='monitoria', uselist=False)
    disciplina_rel = db.relationship('Disciplina', backref='monitorias', lazy=True)

    @property
    def serialize(self):
        return {
            'id': self.id,
            'data_hora': self.data_hora.isoformat() if self.data_hora else None,
            'descricao': self.descricao,
            'disciplina': self.disciplina,
            'disciplina_nome': self.disciplina_rel.nome if self.disciplina_rel else None,
            'monitor_id': self.monitor_id,
            'data_criacao': self.data_criacao.isoformat() if self.data_criacao else None
        }


class Certificado(db.Model):
    __tablename__ = 'certificado'
    id = db.Column(db.Integer, primary_key=True)
    monitoria_id = db.Column(db.Integer, db.ForeignKey('monitoria.id'), unique=True, nullable=False)
    data_criacao = db.Column(db.DateTime(timezone=True), server_default=func.now())


with app.app_context():
    db.create_all()
