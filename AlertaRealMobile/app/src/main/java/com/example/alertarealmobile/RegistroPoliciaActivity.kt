package com.example.alertarealmobile

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegistroPoliciaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_policia)

        val etNombre = findViewById<TextInputEditText>(R.id.etNombreRegistro)
        val etNumLicencia = findViewById<TextInputEditText>(R.id.etNumLicenciaRegistro)
        val etCuerpo = findViewById<TextInputEditText>(R.id.etCuerpoSeguridad)
        val etDni = findViewById<TextInputEditText>(R.id.etDniRegistro)
        val btnConfirmar = findViewById<TextView>(R.id.btnConfirmarRegistro)

        btnConfirmar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val numLicencia = etNumLicencia.text.toString().trim()
            val cuerpo = etCuerpo.text.toString().trim()
            val dni = etDni.text.toString().trim().uppercase()

            if (nombre.isEmpty() || numLicencia.isEmpty() || cuerpo.isEmpty() || dni.isEmpty()) {
                Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (dni.length != 9) {
                Toast.makeText(this, "El DNI debe tener 8 números y 1 letra", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnConfirmar.isEnabled = false

            val datos = mapOf(
                "nombre" to nombre,
                "numLicencia" to numLicencia,
                "cuerpoSeguridad" to cuerpo,
                "dni" to dni
            )

            RetrofitClient.apiService.registrarPolicia(datos).enqueue(object : Callback<Map<String, Any>> {
                override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
                    btnConfirmar.isEnabled = true
                    when (response.code()) {
                        201 -> {
                            Toast.makeText(this@RegistroPoliciaActivity,
                                "✅ Registro completado. Ya puedes iniciar sesión.",
                                Toast.LENGTH_LONG).show()
                            finish()
                        }
                        409 -> Toast.makeText(this@RegistroPoliciaActivity,
                            "Ese número de licencia ya está registrado",
                            Toast.LENGTH_SHORT).show()
                        400 -> Toast.makeText(this@RegistroPoliciaActivity,
                            "DNI inválido",
                            Toast.LENGTH_SHORT).show()
                        else -> Toast.makeText(this@RegistroPoliciaActivity,
                            "Error al registrarse",
                            Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                    btnConfirmar.isEnabled = true
                    Toast.makeText(this@RegistroPoliciaActivity,
                        "Error de conexión: ${t.message}",
                        Toast.LENGTH_LONG).show()
                }
            })
        }
    }
}