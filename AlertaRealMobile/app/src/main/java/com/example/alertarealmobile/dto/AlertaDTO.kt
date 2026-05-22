package com.example.alertarealmobile.dto

data class AlertaDTO(
    val id: Int,
    val descripcion: String,
    val latitud: Double,
    val longitud: Double,
    val nivelAlerta: Int,
    val tipoDelito: String,
    val ciudad: String,
    val nombreCalle: String?,
    val codigoPostal: String?,
    val horaIncidente: String?,
    val fechaHora: String?,
    val duracionEstimada: Int?,
    val tipoDelitoId: Int,
    val activa: Boolean
)