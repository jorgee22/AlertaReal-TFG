package com.example.alertarealmobile

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // ATENCIÓN: 10.0.2.2 es el "localhost" del ordenador visto desde el emulador Android
    private const val BASE_URL = "http://10.0.2.2:8080/"

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // Convierte el JSON a tus DTOs
            .build()
            .create(ApiService::class.java)
    }
}