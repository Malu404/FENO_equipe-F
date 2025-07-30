package com.example.fenoapp

import android.content.Context
import com.example.fenoapp.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun isUsuarioAdmin(context: Context): Boolean {
    return withContext(Dispatchers.IO) {
        val tokenManager = TokenManager(context)
        val token = tokenManager.getToken() ?: return@withContext false

        val api = RetrofitClient.criarApiServiceComToken("Bearer $token")
        val response = api.obterClienteAtual("Bearer $token")

        if (response.isSuccessful) {
            val cliente = response.body()
            return@withContext cliente?.admin == true
        }

        return@withContext false
    }
}
