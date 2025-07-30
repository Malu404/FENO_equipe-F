package com.example.fenoapp.model

data class MonitoriaRequest(
    val data_hora: String?,
    val descricao: String,
    val disciplina: Int,
    val monitor: String?
)
