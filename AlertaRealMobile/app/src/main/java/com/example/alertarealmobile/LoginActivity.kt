package com.example.alertarealmobile

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("alertareal_prefs", MODE_PRIVATE)
        val rol = prefs.getString("rol", "")
        if (!rol.isNullOrEmpty()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_login)

        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val btnLogin = findViewById<TextView>(R.id.btnLoginCiudadano)
        val btnRegistro = findViewById<TextView>(R.id.btnRegistroCiudadano)
        val btnSoyPolicia = findViewById<TextView>(R.id.btnSoyPolicia)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            RetrofitClient.apiService.loginCiudadano(mapOf("email" to email, "password" to password))
                .enqueue(object : Callback<Map<String, Any>> {
                    override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
                        if (response.isSuccessful) {
                            val body = response.body()!!
                            prefs.edit()
                                .putString("rol", "CIUDADANO")
                                .putString("nombre", body["nombre"]?.toString())
                                .putString("usuarioId", body["id"]?.toString())
                                .apply()
                            Toast.makeText(this@LoginActivity, "Bienvenido, ${body["nombre"]}", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this@LoginActivity, "Email o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                        Toast.makeText(this@LoginActivity, "Error de conexión", Toast.LENGTH_LONG).show()
                    }
                })
        }

        btnRegistro.setOnClickListener {
            startActivity(Intent(this, RegistroCiudadanoActivity::class.java))
        }

        btnSoyPolicia.setOnClickListener {
            startActivity(Intent(this, LoginPoliciaActivity::class.java))
        }
    }
}