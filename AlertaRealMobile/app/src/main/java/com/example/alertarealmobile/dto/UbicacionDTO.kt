package com.example.alertarealmobile.dto

data class UbicacionDTO(
    val id: Int,
    val nombreCalle: String,
    val codigoPostal: String,
    val ciudad: String,
    val latitud: Double,
    val longitud: Double
)