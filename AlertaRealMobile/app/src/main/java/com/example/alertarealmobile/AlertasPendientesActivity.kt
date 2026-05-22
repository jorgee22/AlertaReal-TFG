package com.example.alertarealmobile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.alertarealmobile.dto.AlertaCiudadanoDTO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AlertasPendientesActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var tvSinPendientes: TextView
    private var pendientes = mutableListOf<AlertaCiudadanoDTO>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alertas_pendientes)

        listView = findViewById(R.id.listViewPendientes)
        tvSinPendientes = findViewById(R.id.tvSinPendientes)

        cargarPendientes()
    }

    private fun cargarPendientes() {
        RetrofitClient.apiService.getAlertasPendientes().enqueue(object : Callback<List<AlertaCiudadanoDTO>> {
            override fun onResponse(call: Call<List<AlertaCiudadanoDTO>>, response: Response<List<AlertaCiudadanoDTO>>) {
                pendientes = (response.body() ?: emptyList()).toMutableList()
                if (pendientes.isEmpty()) {
                    listView.visibility = View.GONE
                    tvSinPendientes.visibility = View.VISIBLE
                } else {
                    listView.visibility = View.VISIBLE
                    tvSinPendientes.visibility = View.GONE
                    listView.adapter = AlertaPendienteAdapter(pendientes)
                }
            }
            override fun onFailure(call: Call<List<AlertaCiudadanoDTO>>, t: Throwable) {
                Toast.makeText(this@AlertasPendientesActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }

    inner class AlertaPendienteAdapter(private val alertas: MutableList<AlertaCiudadanoDTO>) :
        ArrayAdapter<AlertaCiudadanoDTO>(this, R.layout.item_alerta_pendiente, alertas) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: LayoutInflater.from(context)
                .inflate(R.layout.item_alerta_pendiente, parent, false)

            val alerta = alertas[position]
            val prefs = getSharedPreferences("alertareal_prefs", MODE_PRIVATE)
            val policiaId = prefs.getString("policiaId", "0")?.toIntOrNull() ?: 0

            val tipo = when (alerta.tipoDelitoId) {
                1 -> "🔴 Robo con fuerza"
                2 -> "🟠 Altercado público"
                3 -> "🟡 Actividad sospechosa"
                else -> "🚨 Incidente"
            }

            view.findViewById<TextView>(R.id.tvTipoPendiente).text = tipo
            view.findViewById<TextView>(R.id.tvCiudadanoPendiente).text = "👤 ${alerta.nombreCiudadano}"
            view.findViewById<TextView>(R.id.tvUbicacionPendiente).text = "📍 ${alerta.nombreCalle}, ${alerta.ciudad}"
            view.findViewById<TextView>(R.id.tvDescripcionPendiente).text = alerta.descripcion

            view.findViewById<TextView>(R.id.btnAprobar).setOnClickListener {
                val datos = linkedMapOf<String, Any>("policiaId" to policiaId)
                RetrofitClient.apiService.aprobarAlerta(alerta.id, datos).enqueue(object : Callback<Map<String, Any>> {
                    override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@AlertasPendientesActivity,
                                "✅ Alerta aprobada y publicada", Toast.LENGTH_SHORT).show()
                            alertas.removeAt(position)
                            notifyDataSetChanged()
                            if (alertas.isEmpty()) {
                                listView.visibility = View.GONE
                                tvSinPendientes.visibility = View.VISIBLE
                            }
                        } else {
                            Toast.makeText(this@AlertasPendientesActivity,
                                "Error al aprobar: ${response.code()}", Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                        Toast.makeText(this@AlertasPendientesActivity,
                            "Error de conexión", Toast.LENGTH_SHORT).show()
                    }
                })
            }

            view.findViewById<TextView>(R.id.btnRechazar).setOnClickListener {
                RetrofitClient.apiService.rechazarAlerta(alerta.id).enqueue(object : Callback<Map<String, Any>> {
                    override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
                        Toast.makeText(this@AlertasPendientesActivity,
                            "Alerta rechazada", Toast.LENGTH_SHORT).show()
                        alertas.removeAt(position)
                        notifyDataSetChanged()
                        if (alertas.isEmpty()) {
                            listView.visibility = View.GONE
                            tvSinPendientes.visibility = View.VISIBLE
                        }
                    }
                    override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                        Toast.makeText(this@AlertasPendientesActivity,
                            "Error de conexión", Toast.LENGTH_SHORT).show()
                    }
                })
            }

            return view
        }
    }
}