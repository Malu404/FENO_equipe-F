package com.example.fenoapp.model

data class Monitoria(
    val id: Int,
    val data_hora: String,
    val descricao: String,
    val disciplina: Int,
    val monitor_id: Int?,
    val data_criacao: String
)