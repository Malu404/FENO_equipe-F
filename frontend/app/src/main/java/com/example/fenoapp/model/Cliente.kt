package com.example.fenoapp.model

data class Cliente(
    val id: Int,
    val nome: String,
    val email: String,
    val matricula: String,
    val cpf: String,
    val ano_ingresso: Int,
    val admin: Boolean
)