package com.example.alertarealmobile.dto

data class AlertaCiudadanoDTO(
    val id: Int,
    val descripcion: String,
    val nombreCalle: String,
    val codigoPostal: String,
    val ciudad: String,
    val latitud: Double,
    val longitud: Double,
    val horaIncidente: String?,
    val tipoDelitoId: Int,
    val nombreCiudadano: String,
    val fechaHora: String?,
    val estado: String
)