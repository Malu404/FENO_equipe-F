package com.example.fenoapp.model

data class ClienteResponse(
    val id: Int,
    val nome: String,
    val matricula: String,
    val cpf: String,
    val email: String,
    val ano_ingresso: Int,
    val admin: Boolean
)