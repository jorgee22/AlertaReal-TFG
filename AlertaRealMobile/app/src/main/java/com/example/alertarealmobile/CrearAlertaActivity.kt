package com.example.alertarealmobile

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.alertarealmobile.dto.AlertaDTO
import kotlinx.coroutines.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URL
import java.net.URLEncoder

class CrearAlertaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_alerta)

        Log.d("CrearAlerta", "=== ACTIVIDAD CREADA ===")

        val etDescripcion = findViewById<EditText>(R.id.etDescripcion)
        val etCalle = findViewById<EditText>(R.id.etCalle)
        val etCiudad = findViewById<EditText>(R.id.etCiudad)
        val etCodigoPostal = findViewById<EditText>(R.id.etCodigoPostal)
        val etHoraIncidente = findViewById<EditText>(R.id.etHoraIncidente)
        val etTipoDelitoId = findViewById<EditText>(R.id.etTipoDelitoId)
        val btnEnviar = findViewById<TextView>(R.id.btnEnviarAlerta)

        val prefs = getSharedPreferences("alertareal_prefs", MODE_PRIVATE)
        val policiaId = prefs.getString("policiaId", "0")?.toIntOrNull() ?: 0
        Log.d("CrearAlerta", "policiaId=$policiaId")

        btnEnviar.setOnClickListener {
            Log.d("CrearAlerta", "=== BOTON PULSADO ===")

            val descripcion = etDescripcion.text.toString().trim()
            val calle = etCalle.text.toString().trim()
            val ciudad = etCiudad.text.toString().trim()
            val cp = etCodigoPostal.text.toString().trim()
            val hora = etHoraIncidente.text.toString().trim()
            val tipoId = etTipoDelitoId.text.toString().trim().toIntOrNull()

            if (descripcion.isEmpty() || calle.isEmpty() || ciudad.isEmpty() || cp.isEmpty() || hora.isEmpty() || tipoId == null) {
                Toast.makeText(this, "Rellena todos los campos correctamente", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (policiaId == 0) {
                Toast.makeText(this, "Error: no hay sesión activa", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            btnEnviar.isEnabled = false
            Toast.makeText(this, "Buscando ubicación...", Toast.LENGTH_SHORT).show()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val direccion = "$calle, $cp, $ciudad, España"
                    val urlStr = "https://maps.googleapis.com/maps/api/geocode/json?address=${URLEncoder.encode(direccion, "UTF-8")}&key=AIzaSyCZfJpKJKWGldR2Hg9TqLqD3HG6F-5xX98"
                    val result = URL(urlStr).readText()
                    val json = JSONObject(result)
                    val status = json.getString("status")

                    if (status != "OK") {
                        withContext(Dispatchers.Main) {
                            btnEnviar.isEnabled = true
                            Toast.makeText(this@CrearAlertaActivity, "Dirección no encontrada, verifica los datos", Toast.LENGTH_LONG).show()
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

                    // Verificar que está en España
                    val enEspana = lat in 36.0..43.8 && lng in -9.3..4.3
                    if (!enEspana) {
                        withContext(Dispatchers.Main) {
                            btnEnviar.isEnabled = true
                            Toast.makeText(this@CrearAlertaActivity, "La dirección no está en España", Toast.LENGTH_LONG).show()
                        }
                        return@launch
                    }

                    Log.d("CrearAlerta", "Geocodificación OK: $lat, $lng")

                    val datos = linkedMapOf<String, Any>(
                        "policiaId" to policiaId,
                        "tipoDelitoId" to tipoId,
                        "nombreCalle" to calle,
                        "codigoPostal" to cp,
                        "ciudad" to ciudad,
                        "latitud" to lat,
                        "longitud" to lng,
                        "descripcion" to descripcion,
                        "horaIncidente" to hora,
                        "duracionEstimada" to 60
                    )

                    Log.d("CrearAlerta", "Enviando: $datos")

                    withContext(Dispatchers.Main) {
                        RetrofitClient.apiService.crearAlerta(datos).enqueue(object : Callback<AlertaDTO> {
                            override fun onResponse(call: Call<AlertaDTO>, response: Response<AlertaDTO>) {
                                btnEnviar.isEnabled = true
                                if (response.isSuccessful) {
                                    Toast.makeText(this@CrearAlertaActivity, "✅ Alerta publicada", Toast.LENGTH_SHORT).show()
                                    finish()
                                } else {
                                    val errorBody = response.errorBody()?.string()
                                    Log.e("CrearAlerta", "Error: $errorBody")
                                    Toast.makeText(this@CrearAlertaActivity, "Error ${response.code()}: $errorBody", Toast.LENGTH_LONG).show()
                                }
                            }
                            override fun onFailure(call: Call<AlertaDTO>, t: Throwable) {
                                btnEnviar.isEnabled = true
                                Log.e("CrearAlerta", "Fallo conexión: ${t.message}")
                                Toast.makeText(this@CrearAlertaActivity, "Error de conexión: ${t.message}", Toast.LENGTH_LONG).show()
                            }
                        })
                    }
                } catch (e: Exception) {
                    Log.e("CrearAlerta", "Error geocodificación: ${e.message}")
                    withContext(Dispatchers.Main) {
                        btnEnviar.isEnabled = true
                        Toast.makeText(this@CrearAlertaActivity, "Error al buscar la dirección", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}