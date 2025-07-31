package com.example.fenoapp.network

import com.example.fenoapp.model.ClienteResponse
import com.example.fenoapp.model.Disciplina
import com.example.fenoapp.model.DisciplinaResponse
import com.example.fenoapp.model.LoginRequest
import com.example.fenoapp.model.LoginResponse
import com.example.fenoapp.model.Monitoria
import com.example.fenoapp.model.MonitoriaRequest
import com.example.fenoapp.model.MonitoriaResponse
import com.example.fenoapp.model.RegisterRequest
import com.example.fenoapp.model.RegisterResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @POST("/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @POST("/cadastro")
    suspend fun register(@Body registerRequest: RegisterRequest): Response<RegisterResponse>

    @GET("disciplinas")
    suspend fun getDisciplinas(@Header("Authorization") token: String): Response<DisciplinaResponse>

    @POST("/monitorias")
    suspend fun criarMonitoria(
        @Header("Authorization") authHeader: String,
        @Body request: MonitoriaRequest
    ): Response<Any>

    @GET("monitorias")
    suspend fun getTodasMonitorias(
        @Header("Authorization") token: String
    ): List<Monitoria>

    @GET("monitorias")
    suspend fun obterMonitoriasPorData(
        @Header("Authorization") token: String,
        @Query("data") data: String
    ): Response<MonitoriaResponse>

    @GET("/cliente")
    suspend fun obterClienteAtual(
        @Header("Authorization") token: String
    ): Response<ClienteResponse>
}