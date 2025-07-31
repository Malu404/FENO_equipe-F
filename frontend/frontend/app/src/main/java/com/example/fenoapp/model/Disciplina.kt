package com.example.fenoapp.model

data class Disciplina(
    val id: Int,
    val codigo: String,
    val nome: String
) {
    override fun toString(): String = "$codigo - $nome"
}
