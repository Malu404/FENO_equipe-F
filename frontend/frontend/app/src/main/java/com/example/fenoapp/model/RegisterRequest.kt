package com.example.fenoapp.model

data class RegisterRequest(
    val cpf: String,
    val nome: String,
    val matricula: String,
    val email: String,
    val senha: String,
    val ano_ingresso: Int
)