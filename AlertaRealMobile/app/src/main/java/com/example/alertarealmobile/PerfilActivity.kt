package com.example.alertarealmobile

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class PerfilActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        val prefs = getSharedPreferences("alertareal_prefs", MODE_PRIVATE)
        val rol = prefs.getString("rol", "") ?: ""
        val nombre = prefs.getString("nombre", "Invitado") ?: "Invitado"
        val cuerpo = prefs.getString("cuerpoSeguridad", "") ?: ""

        val tvAvatar = findViewById<TextView>(R.id.tvAvatarPerfil)
        val tvNombre = findViewById<TextView>(R.id.tvNombrePerfilPantalla)
        val tvRol = findViewById<TextView>(R.id.tvRolPerfilPantalla)
        val tvBadge = findViewById<TextView>(R.id.tvBadgePerfilPantalla)
        val tvDatoNombre = findViewById<TextView>(R.id.tvDatoNombre)
        val tvDatoRol = findViewById<TextView>(R.id.tvDatoRol)
        val tvDatoCuerpo = findViewById<TextView>(R.id.tvDatoCuerpo)
        val filaCuerpo = findViewById<LinearLayout>(R.id.filaCuerpo)
        val btnCerrar = findViewById<TextView>(R.id.btnCerrarSesionPerfil)

        when (rol) {
            "POLICIA" -> {
                tvAvatar.text = nombre.firstOrNull()?.uppercase() ?: "P"
                tvNombre.text = nombre
                tvRol.text = cuerpo.ifEmpty { "Policía" }
                tvBadge.text = "🚔 POLICÍA"
                tvDatoNombre.text = nombre
                tvDatoRol.text = "Policía"
                tvDatoCuerpo.text = cuerpo
                filaCuerpo.visibility = View.VISIBLE
                btnCerrar.visibility = View.VISIBLE
            }
            "CIUDADANO" -> {
                tvAvatar.text = nombre.firstOrNull()?.uppercase() ?: "C"
                tvNombre.text = nombre
                tvRol.text = "Ciudadano"
                tvBadge.text = "👤 CIUDADANO"
                tvDatoNombre.text = nombre
                tvDatoRol.text = "Ciudadano"
                filaCuerpo.visibility = View.GONE
                btnCerrar.visibility = View.VISIBLE
            }
            else -> {
                tvAvatar.text = "?"
                tvNombre.text = "Invitado"
                tvRol.text = "Sin sesión iniciada"
                tvBadge.text = "VISITANTE"
                tvDatoNombre.text = "—"
                tvDatoRol.text = "—"
                filaCuerpo.visibility = View.GONE
                btnCerrar.visibility = View.GONE
            }
        }

        btnCerrar.setOnClickListener {
            prefs.edit().clear().apply()
            Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }
    }
}