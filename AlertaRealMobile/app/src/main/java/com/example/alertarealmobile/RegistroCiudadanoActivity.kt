package com.example.alertarealmobile

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegistroCiudadanoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_ciudadano)

        val etNombre = findViewById<TextInputEditText>(R.id.etNombreciudadano)
        val etEmail = findViewById<TextInputEditText>(R.id.etEmailCiudadano)
        val etPassword = findViewById<TextInputEditText>(R.id.etPasswordCiudadano)
        val etConfirm = findViewById<TextInputEditText>(R.id.etConfirmPassword)
        val btnRegistrar = findViewById<TextView>(R.id.btnConfirmarRegistroCiudadano)

        btnRegistrar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirm = etConfirm.text.toString().trim()

            if (nombre.isEmpty() || email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirm) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnRegistrar.isEnabled = false

            val datos = mapOf(
                "nombre" to nombre,
                "email" to email,
                "password" to password
            )

            RetrofitClient.apiService.registroCiudadano(datos).enqueue(object : Callback<Map<String, Any>> {
                override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
                    btnRegistrar.isEnabled = true
                    when (response.code()) {
                        201 -> {
                            Toast.makeText(this@RegistroCiudadanoActivity,
                                "✅ Cuenta creada. Ya puedes iniciar sesión.",
                                Toast.LENGTH_LONG).show()
                            finish()
                        }
                        409 -> Toast.makeText(this@RegistroCiudadanoActivity,
                            "Ese email ya está registrado",
                            Toast.LENGTH_SHORT).show()
                        else -> Toast.makeText(this@RegistroCiudadanoActivity,
                            "Error al registrarse",
                            Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                    btnRegistrar.isEnabled = true
                    Toast.makeText(this@RegistroCiudadanoActivity,
                        "Error de conexión: ${t.message}",
                        Toast.LENGTH_LONG).show()
                }
            })
        }
    }
}