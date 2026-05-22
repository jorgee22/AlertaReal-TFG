package com.example.alertarealmobile.dto

// DTO para enviar los datos de login al backend
data class LoginRequestDTO(
    val username: String,
    val pass: String
)