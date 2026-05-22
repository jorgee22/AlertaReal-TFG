package com.example.alertarealmobile

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URL
import java.net.URLEncoder

class ReportarAlertaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reportar_alerta)

        val etDescripcion = findViewById<TextInputEditText>(R.id.etDescripcionReporte)
        val etCalle = findViewById<TextInputEditText>(R.id.etCalleReporte)
        val etCiudad = findViewById<TextInputEditText>(R.id.etCiudadReporte)
        val etCodigoPostal = findViewById<TextInputEditText>(R.id.etCodigoPostalReporte)
        val etHora = findViewById<TextInputEditText>(R.id.etHoraReporte)
        val etTipo = findViewById<TextInputEditText>(R.id.etTipoReporte)
        val btnEnviar = findViewById<TextView>(R.id.btnEnviarReporte)

        val prefs = getSharedPreferences("alertareal_prefs", MODE_PRIVATE)
        val nombreCiudadano = prefs.getString("nombre", "Ciudadano") ?: "Ciudadano"

        btnEnviar.setOnClickListener {
            val descripcion = etDescripcion.text.toString().trim()
            val calle = etCalle.text.toString().trim()
            val ciudad = etCiudad.text.toString().trim()
            val cp = etCodigoPostal.text.toString().trim()
            val hora = etHora.text.toString().trim()
            val tipoId = etTipo.text.toString().trim().toIntOrNull()

            if (descripcion.isEmpty() || calle.isEmpty() || ciudad.isEmpty() ||
                cp.isEmpty() || hora.isEmpty() || tipoId == null) {
                Toast.makeText(this, "Rellena todos los campos correctamente", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnEnviar.isEnabled = false
            Toast.makeText(this, "Buscando ubicación...", Toast.LENGTH_SHORT).show()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val direccion = "$calle, $cp, $ciudad, España"
                    val urlStr = "https://maps.googleapis.com/maps/api/geocode/json?address=${
                        URLEncoder.encode(direccion, "UTF-8")
                    }&key=AIzaSyCZfJpKJKWGldR2Hg9TqLqD3HG6F-5xX98"
                    val result = URL(urlStr).readText()
                    val json = JSONObject(result)
                    val status = json.getString("status")

                    if (status != "OK") {
                        withContext(Dispatchers.Main) {
                            btnEnviar.isEnabled = true
                            Toast.makeText(this@ReportarAlertaActivity,
                                "Dirección no encontrada, verifica los datos",
                                Toast.LENGTH_LONG).show()
                        }
                        return@launch
                    }

                    val location = json
                        .getJSONArray("results")
                        .getJSONObject(0)
                        .getJSONObject("geometry")
                        .getJSONObject("location")

                    val lat = location.getDouble("lat")
                    val lng = location.getDouble("lng")

                    val enEspana = lat in 36.0..43.8 && lng in -9.3..4.3
                    if (!enEspana) {
                        withContext(Dispatchers.Main) {
                            btnEnviar.isEnabled = true
                            Toast.makeText(this@ReportarAlertaActivity,
                                "La dirección no está en España",
                                Toast.LENGTH_LONG).show()
                        }
                        return@launch
                    }

                    val datos = linkedMapOf<String, Any>(
                        "nombreCiudadano" to nombreCiudadano,
                        "descripcion" to descripcion,
                        "nombreCalle" to calle,
                        "codigoPostal" to cp,
                        "ciudad" to ciudad,
                        "latitud" to lat,
                        "longitud" to lng,
                        "horaIncidente" to hora,
                        "tipoDelitoId" to tipoId
                    )

                    withContext(Dispatchers.Main) {
                        RetrofitClient.apiService.reportarAlertaCiudadano(datos)
                            .enqueue(object : Callback<Map<String, Any>> {
                                override fun onResponse(
                                    call: Call<Map<String, Any>>,
                                    response: Response<Map<String, Any>>
                                ) {
                                    btnEnviar.isEnabled = true
                                    if (response.isSuccessful) {
                                        Toast.makeText(this@ReportarAlertaActivity,
                                            "✅ Alerta reportada. Pendiente de aprobación.",
                                            Toast.LENGTH_LONG).show()
                                        finish()
                                    } else {
                                        Toast.makeText(this@ReportarAlertaActivity,
                                            "Error al reportar la alerta",
                                            Toast.LENGTH_SHORT).show()
                                    }
                                }
                                override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                                    btnEnviar.isEnabled = true
                                    Toast.makeText(this@ReportarAlertaActivity,
                                        "Error de conexión: ${t.message}",
                                        Toast.LENGTH_LONG).show()
                                }
                            })
                    }
                } catch (e: Exception) {
                    Log.e("ReportarAlerta", "Error: ${e.message}")
                    withContext(Dispatchers.Main) {
                        btnEnviar.isEnabled = true
                        Toast.makeText(this@ReportarAlertaActivity,
                            "Error al buscar la dirección",
                            Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}