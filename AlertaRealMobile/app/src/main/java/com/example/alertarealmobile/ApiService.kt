package com.example.alertarealmobile

import com.example.alertarealmobile.dto.AlertaDTO
import com.example.alertarealmobile.dto.AlertaCiudadanoDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @GET("alerta/principal/activas")
    fun obtenerAlertasActivas(): Call<List<AlertaDTO>>

    @POST("alerta/policias/login")
    fun loginPolicia(@Body datos: Map<String, String>): Call<Map<String, Any>>

    @POST("alerta/policias")
    fun registrarPolicia(@Body datos: Map<String, String>): Call<Map<String, Any>>

    @POST("alerta/principal/crear-completa")
    fun crearAlerta(@Body datos: LinkedHashMap<String, Any>): Call<AlertaDTO>

    @POST("alerta/ciudadanos/reportar")
    fun reportarAlertaCiudadano(@Body datos: LinkedHashMap<String, Any>): Call<Map<String, Any>>

    @GET("alerta/ciudadanos/pendientes")
    fun getAlertasPendientes(): Call<List<AlertaCiudadanoDTO>>

    @POST("alerta/ciudadanos/aprobar/{id}")
    fun aprobarAlerta(@Path("id") id: Int, @Body datos: LinkedHashMap<String, Any>): Call<Map<String, Any>>

    @POST("alerta/ciudadanos/rechazar/{id}")
    fun rechazarAlerta(@Path("id") id: Int): Call<Map<String, Any>>

    @POST("alerta/usuarios/registro")
    fun registroCiudadano(@Body datos: Map<String, String>): Call<Map<String, Any>>

    @POST("alerta/usuarios/login")
    fun loginCiudadano(@Body datos: Map<String, String>): Call<Map<String, Any>>
}