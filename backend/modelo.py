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


class Cliente(db.Model):
    __tablename__ = 'cliente'
    cpf = db.Column(db.String(11), unique=True, nullable=False)
    nome = db.Column(db.String(200), nullable=False)
    matricula = db.Column(db.String(6), unique=True, nullable=False)
    email = db.Column(db.String(100), unique=True, nullable=False)
    senha = db.Column(db.String(200), nullable=False)
    id = db.Column(db.Integer, primary_key=True)
    ano_ingresso = db.Column(db.Integer, nullable=False)
    data_criacao = db.Column(db.DateTime(timezone=True), server_default=func.now())
    admin = db.Column(db.Boolean, nullable=False, default=False)

    @property
    def serialize(self):
        return {
            'id': self.id,
            'cpf': self.cpf,
            'nome': self.nome,
            'matricula': self.matricula,
            'email': self.email,
            'ano_ingresso': self.ano_ingresso,
            'data_criacao': self.data_criacao.isoformat() if self.data_criacao else None,
            'admin': self.admin
        }


class Monitoria(db.Model):
    __tablename__ = 'monitoria'
    id = db.Column(db.Integer, primary_key=True)
    data_hora = db.Column(db.DateTime)
    descricao = db.Column(db.String(100))
    disciplina = db.Column(db.Integer, db.ForeignKey('disciplina.id'), nullable=False)
    monitor = db.Column(db.String(100))
    data_criacao = db.Column(db.DateTime(timezone=True), server_default=func.now())

    disciplina_rel = db.relationship('Disciplina', backref='monitorias', lazy=True)

    @property
    def serialize(self):
        return {
            'id': self.id,
            'data_hora': self.data_hora.isoformat() if self.data_hora else None,
            'descricao': self.descricao,
            'disciplina': self.disciplina,
            'disciplina_nome': self.disciplina_rel.nome if self.disciplina_rel else None,
            'monitor': self.monitor,
            'data_criacao': self.data_criacao.isoformat() if self.data_criacao else None
        }


with app.app_context():
    db.create_all()

    # Verifica se já existem disciplinas cadastradas
    if Disciplina.query.count() == 0:
        disciplinas = [
            ('CK0211', 'Fundamentos de Programação'),
            ('CK0169', 'Circuitos Digitais'),
            ('CB0534', 'Cálculo Diferencial e Integral I'),
            ('CB0661', 'Matemática Discreta'),
            ('CK0220', 'Lógica para Ciência da Computação'),
            ('CC0261', 'Introdução à Probabilidade e à Estatística'),
            ('CK0111', 'Algoritmos em Grafos'),
            ('CK0235', 'Técnicas de Programação I'),
            ('CK0195', 'Arquitetura de Computadores'),
            ('CK0203', 'Construção e Análise de Algoritmos'),
            ('CK0047', 'Métodos Numéricos I'),
            ('CK0245', 'Computação Gráfica I'),
            ('CK0443', 'Fundamentos de Bancos de Dados'),
            ('CK0115', 'Linguagens de Programação I'),
            ('CK0247', 'Engenharia de Software I'),
            ('CK0048', 'Métodos Numéricos II'),
            ('CK0249', 'Redes de Computadores I'),
            ('CK0095', 'Sistemas de Gerenciamento de Bancos de Dados'),
            ('CK0248', 'Inteligência Artificial'),
            ('CK0234', 'Sistemas Operacionais'),
            ('CK0192', 'Análise e Projeto de Sistemas I'),
            ('CK0118', 'Autômatos e Linguagens Formais'),
            ('CK0202', 'Construção de Compiladores'),
            ('CK0096', 'Teoria da Computação'),
            ('CK0212', 'Informática e Sociedade'),
            ('CCP0001', 'Atividades Complementares'),
        ]

        for codigo, nome in disciplinas:
            nova_disciplina = Disciplina(codigo=codigo, nome=nome)
            db.session.add(nova_disciplina)

        db.session.commit()
        print("Disciplinas inseridas com sucesso.")
    else:
        print("Disciplinas já existentes. Nenhuma inserção feita.")
