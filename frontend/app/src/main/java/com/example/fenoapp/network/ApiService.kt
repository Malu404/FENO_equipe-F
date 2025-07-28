package com.example.fenoapp.network

import com.example.fenoapp.model.Disciplina
import com.example.fenoapp.model.LoginRequest
import com.example.fenoapp.model.LoginResponse
import com.example.fenoapp.model.MonitoriaRequest
import com.example.fenoapp.model.RegisterRequest
import com.example.fenoapp.model.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @POST("/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @POST("/cadastro")
    suspend fun register(@Body registerRequest: RegisterRequest): Response<RegisterResponse>

    @GET("/disciplinas")
    suspend fun getDisciplinas(): List<Disciplina>

    @POST("/monitorias")
    suspend fun solicitarMonitoria(@Body request: MonitoriaRequest): Response<Void>
}