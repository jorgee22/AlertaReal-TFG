package com.example.alertarealmobile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginPoliciaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_policia)

        val etNombre = findViewById<TextInputEditText>(R.id.etNombre)
        val etNumLicencia = findViewById<TextInputEditText>(R.id.etNumLicencia)
        val btnEntrar = findViewById<TextView>(R.id.btnEntrarPolicia)
        val btnRegistrarse = findViewById<TextView>(R.id.btnRegistrarse)

        btnEntrar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val numLicencia = etNumLicencia.text.toString().trim()

            if (nombre.isEmpty() || numLicencia.isEmpty()) {
                Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val datos = mapOf("nombre" to nombre, "numLicencia" to numLicencia)

            RetrofitClient.apiService.loginPolicia(datos).enqueue(object : Callback<Map<String, Any>> {
                override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
                    if (response.isSuccessful) {
                        val body = response.body()!!
                        Log.d("LoginPolicia", "Respuesta: $body")

                        val idRaw = body["id"]
                        val policiaId = when (idRaw) {
                            is Double -> idRaw.toInt().toString()
                            is Int -> idRaw.toString()
                            else -> idRaw?.toString() ?: "0"
                        }

                        val prefs = getSharedPreferences("alertareal_prefs", MODE_PRIVATE)
                        prefs.edit()
                            .putString("rol", "POLICIA")
                            .putString("nombre", body["nombre"]?.toString())
                            .putString("policiaId", policiaId)
                            .putString("cuerpoSeguridad", body["cuerpoSeguridad"]?.toString())
                            .apply()

                        Toast.makeText(this@LoginPoliciaActivity,
                            "Bienvenido, ${body["nombre"]}",
                            Toast.LENGTH_SHORT).show()

                        val intent = Intent(this@LoginPoliciaActivity, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@LoginPoliciaActivity,
                            "Nombre o licencia incorrectos",
                            Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                    Log.e("LoginPolicia", "Error: ${t.message}")
                    Toast.makeText(this@LoginPoliciaActivity,
                        "Error de conexión: ${t.message}",
                        Toast.LENGTH_LONG).show()
                }
            })
        }

        btnRegistrarse.setOnClickListener {
            startActivity(Intent(this, RegistroPoliciaActivity::class.java))
        }
    }
}