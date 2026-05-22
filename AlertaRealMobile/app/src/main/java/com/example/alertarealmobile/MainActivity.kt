package com.example.alertarealmobile

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.example.alertarealmobile.dto.AlertaDTO
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.heatmaps.HeatmapTileProvider
import com.google.maps.android.heatmaps.WeightedLatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mapa: GoogleMap
    private lateinit var drawerLayout: DrawerLayout
    private val alertasMap = mutableMapOf<String, AlertaDTO>()
    private var heatmapActivo = false
    private var heatmapOverlay: com.google.android.gms.maps.model.TileOverlay? = null
    private val todasLasAlertas = mutableListOf<AlertaDTO>()

    private val handler = Handler(Looper.getMainLooper())
    private val refrescarRunnable = object : Runnable {
        override fun run() {
            if (::mapa.isInitialized) {
                mapa.clear()
                alertasMap.clear()
                cargarAlertas()
            }
            handler.postDelayed(this, 30_000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawerLayout)
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)

        findViewById<ImageButton>(R.id.btnMenu).setOnClickListener {
            if (drawerLayout.isDrawerOpen(androidx.core.view.GravityCompat.START))
                drawerLayout.closeDrawer(androidx.core.view.GravityCompat.START)
            else
                drawerLayout.openDrawer(androidx.core.view.GravityCompat.START)
        }

        val etBusqueda = findViewById<EditText>(R.id.etBusqueda)

        findViewById<ImageButton>(R.id.btnBuscar).setOnClickListener {
            val query = etBusqueda.text.toString().trim()
            if (query.isNotEmpty()) ejecutarBusqueda(query, etBusqueda)
        }

        etBusqueda.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = etBusqueda.text.toString().trim()
                if (query.isNotEmpty()) ejecutarBusqueda(query, etBusqueda)
                true
            } else false
        }

        findViewById<LinearLayout>(R.id.tabMapa).setOnClickListener {
            setTabActivo("mapa")
            mostrarTab("mapa")
        }

        findViewById<LinearLayout>(R.id.tabFeed).setOnClickListener {
            setTabActivo("feed")
            mostrarTab("feed")
        }

        findViewById<LinearLayout>(R.id.tabSubir).setOnClickListener {
            val prefs = getSharedPreferences("alertareal_prefs", MODE_PRIVATE)
            when (prefs.getString("rol", "")) {
                "CIUDADANO" -> startActivity(Intent(this, ReportarAlertaActivity::class.java))
                "POLICIA" -> startActivity(Intent(this, CrearAlertaActivity::class.java))
                else -> startActivity(Intent(this, LoginActivity::class.java))
            }
        }

        findViewById<LinearLayout>(R.id.tabPendientes).setOnClickListener {
            setTabActivo("pendientes")
            startActivity(Intent(this, AlertasPendientesActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.tabPerfil).setOnClickListener {
            val prefs = getSharedPreferences("alertareal_prefs", MODE_PRIVATE)
            if (prefs.getString("rol", "").isNullOrEmpty()) {
                startActivity(Intent(this, LoginActivity::class.java))
            } else {
                setTabActivo("perfil")
                startActivity(Intent(this, PerfilActivity::class.java))
            }
        }

        // Drawer
        findViewById<LinearLayout>(R.id.navCiudades).setOnClickListener {
            drawerLayout.closeDrawers()
            mostrarFiltrarCiudad()
        }
        findViewById<LinearLayout>(R.id.navMapaCalor).setOnClickListener {
            drawerLayout.closeDrawers()
            toggleMapaCalor()
        }
        findViewById<LinearLayout>(R.id.navEstadisticas).setOnClickListener {
            drawerLayout.closeDrawers()
            mostrarEstadisticas()
        }
        findViewById<LinearLayout>(R.id.navConfiguracion).setOnClickListener {
            drawerLayout.closeDrawers()
            Toast.makeText(this, "Configuración — próximamente", Toast.LENGTH_SHORT).show()
        }
        findViewById<LinearLayout>(R.id.navAcercaDe).setOnClickListener {
            drawerLayout.closeDrawers()
            mostrarAcercaDe()
        }

        // Filtros mapa
        val filtros = listOf(
            R.id.filtroTodos to 0,
            R.id.filtroRobos to 1,
            R.id.filtroAltercados to 2,
            R.id.filtroSospechosos to 3
        )
        filtros.forEach { (viewId, tipoId) ->
            findViewById<TextView>(viewId).setOnClickListener {
                aplicarFiltro(tipoId)
                // Visual: activo/inactivo
                filtros.forEach { (id, _) ->
                    val tv = findViewById<TextView>(id)
                    if (id == viewId) {
                        tv.setBackgroundResource(R.drawable.filtro_activo)
                        tv.setTextColor(android.graphics.Color.WHITE)
                    } else {
                        tv.setBackgroundResource(R.drawable.filtro_inactivo)
                        tv.setTextColor(android.graphics.Color.parseColor("#E63946"))
                    }
                }
            }
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun configurarInfoWindow() {
        mapa.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
            override fun getInfoWindow(marker: com.google.android.gms.maps.model.Marker): android.view.View? = null
            override fun getInfoContents(marker: com.google.android.gms.maps.model.Marker): android.view.View? {
                val alerta = alertasMap[marker.title] ?: return null

                val tipo = when (alerta.tipoDelitoId) {
                    1 -> "Robo con fuerza"
                    2 -> "Altercado público"
                    3 -> "Actividad sospechosa"
                    else -> "Incidente"
                }
                val (nivelTexto, nivelColor) = when (alerta.nivelAlerta) {
                    1 -> Triple("● BAJO", "#FFD166", "#1A1200")
                    2 -> Triple("● MEDIO", "#F4A261", "#1A0E00")
                    3 -> Triple("● ALTO", "#E63946", "#1A0A0C")
                    else -> Triple("● INFO", "#E63946", "#0F1E35")
                }

                val ctx = this@MainActivity
                val dp = ctx.resources.displayMetrics.density

                val root = LinearLayout(ctx).apply {
                    orientation = LinearLayout.VERTICAL
                    setPadding((14*dp).toInt(), (12*dp).toInt(), (14*dp).toInt(), (12*dp).toInt())
                    setBackgroundColor(android.graphics.Color.parseColor("#0F1E35"))
                    minimumWidth = (200*dp).toInt()
                }

                // Badge nivel
                val badge = TextView(ctx).apply {
                    text = nivelTexto
                    textSize = 10f
                    setTextColor(android.graphics.Color.parseColor(nivelColor))
                    setBackgroundColor(android.graphics.Color.parseColor(nivelColor.replace("#", "#22").let {
                        // light bg
                        nivelColor.replace("#E63946","#1A0A0C").replace("#F4A261","#1A0E00").replace("#FFD166","#1A1200").replace("#E63946","#0F1E35")
                    }))
                    setPadding((8*dp).toInt(), (3*dp).toInt(), (8*dp).toInt(), (3*dp).toInt())
                    val lp = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply { bottomMargin = (8*dp).toInt() }
                    layoutParams = lp
                }
                root.addView(badge)

                // Tipo
                val tvTipo = TextView(ctx).apply {
                    text = tipo
                    textSize = 14f
                    setTextColor(android.graphics.Color.parseColor("#E63946"))
                    setTypeface(null, android.graphics.Typeface.BOLD)
                    val lp = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply { bottomMargin = (6*dp).toInt() }
                    layoutParams = lp
                }
                root.addView(tvTipo)

                // Dirección
                val tvDir = TextView(ctx).apply {
                    text = "📍 ${alerta.nombreCalle ?: ""}, ${alerta.ciudad}"
                    textSize = 12f
                    setTextColor(android.graphics.Color.parseColor("#8BA4BE"))
                    val lp = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply { bottomMargin = (4*dp).toInt() }
                    layoutParams = lp
                }
                root.addView(tvDir)

                // Hora
                val hora = alerta.horaIncidente ?: alerta.fechaHora?.substring(11, 16) ?: ""
                if (hora.isNotEmpty()) {
                    val tvHora = TextView(ctx).apply {
                        text = "🕐 $hora"
                        textSize = 11f
                        setTextColor(android.graphics.Color.parseColor("#2A4A6B"))
                        val lp = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply { bottomMargin = (8*dp).toInt() }
                        layoutParams = lp
                    }
                    root.addView(tvHora)
                }

                // Toque para ver detalle
                val tvDetalle = TextView(ctx).apply {
                    text = "Pulsa para ver detalle →"
                    textSize = 11f
                    setTextColor(android.graphics.Color.parseColor("#E63946"))
                    setTypeface(null, android.graphics.Typeface.BOLD)
                }
                root.addView(tvDetalle)

                return root
            }
        })
    }

    override fun onResume() {
        super.onResume()
        actualizarPerfil()
        setTabActivo("mapa")
        handler.post(refrescarRunnable)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(refrescarRunnable)
    }

    private fun setTabActivo(tab: String) {
        val tabs = listOf(
            R.id.tabMapa to R.id.iconTabMapa,
            R.id.tabFeed to R.id.iconTabFeed,
            R.id.tabPendientes to R.id.iconTabPendientes,
            R.id.tabPerfil to R.id.iconTabPerfil
        )

        tabs.forEach { (layoutId, iconId) ->
            try {
                findViewById<LinearLayout>(layoutId)
                    .setBackgroundResource(android.R.color.transparent)
                findViewById<ImageView>(iconId)
                    .setColorFilter(android.graphics.Color.parseColor("#2A4A6B"))
            } catch (e: Exception) {}
        }

        when (tab) {
            "mapa" -> {
                findViewById<LinearLayout>(R.id.tabMapa)
                    .setBackgroundResource(R.drawable.tab_activo)
                findViewById<ImageView>(R.id.iconTabMapa)
                    .setColorFilter(android.graphics.Color.parseColor("#E63946"))
            }
            "feed" -> {
                findViewById<LinearLayout>(R.id.tabFeed)
                    .setBackgroundResource(R.drawable.tab_activo)
                findViewById<ImageView>(R.id.iconTabFeed)
                    .setColorFilter(android.graphics.Color.parseColor("#E63946"))
            }
            "pendientes" -> {
                try {
                    findViewById<LinearLayout>(R.id.tabPendientes)
                        .setBackgroundResource(R.drawable.tab_activo)
                    findViewById<ImageView>(R.id.iconTabPendientes)
                        .setColorFilter(android.graphics.Color.parseColor("#E63946"))
                } catch (e: Exception) {}
            }
            "perfil" -> {
                findViewById<LinearLayout>(R.id.tabPerfil)
                    .setBackgroundResource(R.drawable.tab_activo)
                findViewById<ImageView>(R.id.iconTabPerfil)
                    .setColorFilter(android.graphics.Color.parseColor("#E63946"))
            }
        }
    }

    private fun mostrarTab(tab: String) {
        val mapaView = findViewById<View>(R.id.map)
        val overlay = findViewById<View>(R.id.mapOverlay)
        val feed = findViewById<View>(R.id.feedAlertas)
        when (tab) {
            "mapa" -> {
                mapaView.visibility = View.VISIBLE
                overlay.visibility = View.VISIBLE
                feed.visibility = View.GONE
            }
            "feed" -> {
                mapaView.visibility = View.GONE
                overlay.visibility = View.GONE
                feed.visibility = View.VISIBLE
                cargarFeed()
            }
        }
    }

    private fun actualizarPerfil() {
        val prefs = getSharedPreferences("alertareal_prefs", MODE_PRIVATE)
        val rol = prefs.getString("rol", "")
        val nombre = prefs.getString("nombre", "Invitado") ?: "Invitado"

        val tvNombre = findViewById<TextView>(R.id.tvNombrePerfil)
        val tvRol = findViewById<TextView>(R.id.tvRolPerfil)
        val tvAvatar = findViewById<TextView>(R.id.tvAvatarInicial)
        val tvBadge = findViewById<TextView>(R.id.tvBadgeRol)
        val tabPendientes = findViewById<LinearLayout>(R.id.tabPendientes)

        when (rol) {
            "POLICIA" -> {
                tvNombre.text = nombre
                tvRol.text = prefs.getString("cuerpoSeguridad", "Policía") ?: "Policía"
                tvAvatar.text = nombre.firstOrNull()?.uppercase() ?: "P"
                tvBadge.text = "POLICÍA"
                tabPendientes.visibility = View.VISIBLE
            }
            "CIUDADANO" -> {
                tvNombre.text = nombre
                tvRol.text = "Ciudadano"
                tvAvatar.text = nombre.firstOrNull()?.uppercase() ?: "C"
                tvBadge.text = "CIUDADANO"
                tabPendientes.visibility = View.GONE
            }
            else -> {
                tvNombre.text = "Invitado"
                tvRol.text = "Sin sesión iniciada"
                tvAvatar.text = "?"
                tvBadge.text = "VISITANTE"
                tabPendientes.visibility = View.GONE
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mapa = googleMap

        try {
            mapa.setMapStyle(
                com.google.android.gms.maps.model.MapStyleOptions("""
                [
                  {"elementType":"geometry","stylers":[{"color":"#0d1b2e"}]},
                  {"elementType":"labels.text.fill","stylers":[{"color":"#8ba4be"}]},
                  {"elementType":"labels.text.stroke","stylers":[{"color":"#0a1628"}]},
                  {"featureType":"administrative","elementType":"geometry","stylers":[{"visibility":"off"}]},
                  {"featureType":"poi","stylers":[{"visibility":"off"}]},
                  {"featureType":"road","elementType":"geometry","stylers":[{"color":"#162540"}]},
                  {"featureType":"road","elementType":"geometry.stroke","stylers":[{"color":"#1e3a5f"}]},
                  {"featureType":"road.highway","elementType":"geometry","stylers":[{"color":"#1e3a5f"}]},
                  {"featureType":"road.highway","elementType":"geometry.stroke","stylers":[{"color":"#2a4a6b"}]},
                  {"featureType":"transit","stylers":[{"visibility":"off"}]},
                  {"featureType":"water","elementType":"geometry","stylers":[{"color":"#060e1a"}]},
                  {"featureType":"water","elementType":"labels.text.fill","stylers":[{"color":"#1e3a5f"}]}
                ]
                """.trimIndent())
            )
        } catch (e: Exception) {}

        mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(40.4168, -3.7038), 6f))
        cargarAlertas()
        configurarInfoWindow()
        mapa.setOnInfoWindowClickListener { marker ->
            alertasMap[marker.title]?.let { mostrarDetalleAlerta(it) }
        }
    }

    private fun cargarAlertas() {
        RetrofitClient.apiService.obtenerAlertasActivas().enqueue(object : Callback<List<AlertaDTO>> {
            override fun onResponse(call: Call<List<AlertaDTO>>, response: Response<List<AlertaDTO>>) {
                if (response.isSuccessful) {
                    val alertas = response.body() ?: emptyList()
                    todasLasAlertas.clear()
                    todasLasAlertas.addAll(alertas)
                    pintarAlertasEnMapa(alertas)
                    try {
                        findViewById<TextView>(R.id.tvContadorFeed).text = "${alertas.size} alertas"
                    } catch (e: Exception) {}
                }
            }
            override fun onFailure(call: Call<List<AlertaDTO>>, t: Throwable) {}
        })
    }

    private fun cargarFeed() {
        val listView = findViewById<ListView>(R.id.listFeedAlertas)
        if (todasLasAlertas.isEmpty()) {
            RetrofitClient.apiService.obtenerAlertasActivas().enqueue(object : Callback<List<AlertaDTO>> {
                override fun onResponse(call: Call<List<AlertaDTO>>, response: Response<List<AlertaDTO>>) {
                    if (response.isSuccessful) {
                        todasLasAlertas.addAll(response.body() ?: emptyList())
                        listView.adapter = FeedAdapter(todasLasAlertas)
                    }
                }
                override fun onFailure(call: Call<List<AlertaDTO>>, t: Throwable) {}
            })
        } else {
            listView.adapter = FeedAdapter(todasLasAlertas)
        }
    }

    inner class FeedAdapter(private val alertas: List<AlertaDTO>) :
        ArrayAdapter<AlertaDTO>(this, R.layout.item_feed_alerta, alertas) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: LayoutInflater.from(context)
                .inflate(R.layout.item_feed_alerta, parent, false)
            val alerta = alertas[position]
            val tipo = when (alerta.tipoDelitoId) {
                1 -> "Robo con fuerza"
                2 -> "Altercado público"
                3 -> "Actividad sospechosa"
                else -> "Incidente"
            }
            val color = when (alerta.nivelAlerta) {
                3 -> android.graphics.Color.parseColor("#E63946")
                2 -> android.graphics.Color.parseColor("#F4A261")
                else -> android.graphics.Color.parseColor("#E63946")
            }
            view.findViewById<View>(R.id.indicadorTipo).setBackgroundColor(color)
            view.findViewById<TextView>(R.id.tvTipoFeed).text = tipo
            view.findViewById<TextView>(R.id.tvUbicacionFeed).text =
                "📍 ${alerta.nombreCalle ?: ""}, ${alerta.ciudad}"
            view.findViewById<TextView>(R.id.tvDescripcionFeed).text = alerta.descripcion
            view.findViewById<TextView>(R.id.tvHoraFeed).text =
                alerta.horaIncidente ?: alerta.fechaHora?.substring(11, 16) ?: ""
            view.setOnClickListener { mostrarDetalleAlerta(alerta) }
            return view
        }
    }

    private fun crearMarcadorPersonalizado(colorHex: String, nivelTexto: String): BitmapDescriptor {
        val size = 80
        val bitmap = android.graphics.Bitmap.createBitmap(size, size, android.graphics.Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(bitmap)
        val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG)

        // Sombra
        paint.color = android.graphics.Color.parseColor("#33000000")
        canvas.drawCircle(size / 2f, size / 2f + 3f, size / 2f - 4f, paint)

        // Borde blanco
        paint.color = android.graphics.Color.WHITE
        canvas.drawCircle(size / 2f, size / 2f, size / 2f - 5f, paint)

        // Círculo color
        paint.color = android.graphics.Color.parseColor(colorHex)
        canvas.drawCircle(size / 2f, size / 2f, size / 2f - 10f, paint)

        // Texto
        paint.color = android.graphics.Color.WHITE
        paint.textSize = 22f
        paint.textAlign = android.graphics.Paint.Align.CENTER
        paint.isFakeBoldText = true
        val textY = size / 2f - (paint.descent() + paint.ascent()) / 2f
        canvas.drawText(nivelTexto, size / 2f, textY, paint)

        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private fun pintarAlertasEnMapa(alertas: List<AlertaDTO>) {
        for (alerta in alertas) {
            val (colorHex, nivelTexto) = when (alerta.nivelAlerta) {
                1 -> Pair("#FFD166", "!")
                2 -> Pair("#F4A261", "!!")
                3 -> Pair("#E63946", "!!!")
                else -> Pair("#E63946", "?")
            }
            val icono = crearMarcadorPersonalizado(colorHex, nivelTexto)
            val titulo = "${alerta.tipoDelito} #${alerta.id}"
            mapa.addMarker(
                MarkerOptions()
                    .position(LatLng(alerta.latitud, alerta.longitud))
                    .title(titulo)
                    .snippet("Pulsa para ver detalle")
                    .icon(icono)
            )
            alertasMap[titulo] = alerta
        }
    }

    private fun mostrarDetalleAlerta(alerta: AlertaDTO) {
        val dp = resources.displayMetrics.density

        val tipo = when (alerta.tipoDelitoId) {
            1 -> "Robo con fuerza"
            2 -> "Altercado público"
            3 -> "Actividad sospechosa"
            else -> alerta.tipoDelito ?: "Incidente"
        }
        val (nivelTexto, accentColor) = when (alerta.nivelAlerta) {
            1 -> Pair("Bajo", "#FFD166")
            2 -> Pair("Medio", "#F4A261")
            3 -> Pair("Alto", "#E63946")
            else -> Pair("-", "#E63946")
        }

        val scroll = android.widget.ScrollView(this)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(android.graphics.Color.parseColor("#0A1628"))
        }
        scroll.addView(root)

        // Franja de color según nivel
        val franja = View(this).apply {
            setBackgroundColor(android.graphics.Color.parseColor(accentColor))
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (5*dp).toInt())
        }
        root.addView(franja)

        // Card principal
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(android.graphics.Color.parseColor("#0F1E35"))
            setPadding((20*dp).toInt(), (20*dp).toInt(), (20*dp).toInt(), (20*dp).toInt())
            val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            lp.setMargins((12*dp).toInt(), (12*dp).toInt(), (12*dp).toInt(), (8*dp).toInt())
            layoutParams = lp
        }

        // Tipo + nivel en la misma fila
        val rowTop = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_VERTICAL
            val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            lp.bottomMargin = (14*dp).toInt()
            layoutParams = lp
        }
        val tvTipo = TextView(this).apply {
            text = tipo
            textSize = 17f
            setTextColor(android.graphics.Color.parseColor("#E63946"))
            setTypeface(null, android.graphics.Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        val tvNivel = TextView(this).apply {
            text = nivelTexto
            textSize = 11f
            setTextColor(android.graphics.Color.WHITE)
            setTypeface(null, android.graphics.Typeface.BOLD)
            setBackgroundColor(android.graphics.Color.parseColor(accentColor))
            setPadding((10*dp).toInt(), (4*dp).toInt(), (10*dp).toInt(), (4*dp).toInt())
        }
        rowTop.addView(tvTipo)
        rowTop.addView(tvNivel)
        card.addView(rowTop)

        // Separador
        val sep = View(this).apply {
            setBackgroundColor(android.graphics.Color.parseColor("#1E3A5F"))
            val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (1*dp).toInt())
            lp.bottomMargin = (14*dp).toInt()
            layoutParams = lp
        }
        card.addView(sep)

        // Filas de datos
        fun dato(label: String, valor: String) {
            if (valor.isBlank()) return
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                lp.bottomMargin = (12*dp).toInt()
                layoutParams = lp
            }
            val tvLabel = TextView(this).apply {
                text = label.uppercase()
                textSize = 10f
                setTextColor(android.graphics.Color.parseColor("#2A4A6B"))
                letterSpacing = 0.1f
            }
            val tvValor = TextView(this).apply {
                text = valor
                textSize = 14f
                setTextColor(android.graphics.Color.parseColor("#F0F4F8"))
                val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                lp.topMargin = (2*dp).toInt()
                layoutParams = lp
            }
            row.addView(tvLabel)
            row.addView(tvValor)
            card.addView(row)
        }

        dato("Dirección", "${alerta.nombreCalle ?: ""}, ${alerta.ciudad}")
        if (!alerta.codigoPostal.isNullOrEmpty()) dato("Código postal", alerta.codigoPostal!!)
        if (!alerta.horaIncidente.isNullOrEmpty()) dato("Hora del incidente", alerta.horaIncidente!!)
        if (!alerta.fechaHora.isNullOrEmpty()) dato("Registrada el", alerta.fechaHora!!.substring(0,16).replace("T"," "))
        dato("Descripción", alerta.descripcion ?: "")

        root.addView(card)

        // Botón cerrar
        val btnCerrar = TextView(this).apply {
            text = "Cerrar"
            textSize = 14f
            setTextColor(android.graphics.Color.parseColor("#E63946"))
            setTypeface(null, android.graphics.Typeface.BOLD)
            gravity = android.view.Gravity.CENTER
            setPadding(0, (14*dp).toInt(), 0, (14*dp).toInt())
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        }
        root.addView(btnCerrar)

        val dialog = AlertDialog.Builder(this)
            .setView(scroll)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        btnCerrar.setOnClickListener { dialog.dismiss() }
        dialog.show()
        dialog.window?.setLayout((300*dp).toInt(), android.view.WindowManager.LayoutParams.WRAP_CONTENT)
    }

    private fun toggleMapaCalor() {
        if (!::mapa.isInitialized || todasLasAlertas.isEmpty()) {
            Toast.makeText(this, "No hay alertas para mostrar el mapa de calor", Toast.LENGTH_SHORT).show()
            return
        }
        if (heatmapActivo) {
            heatmapOverlay?.remove()
            heatmapOverlay = null
            heatmapActivo = false
            Toast.makeText(this, "Mapa de calor desactivado", Toast.LENGTH_SHORT).show()
        } else {
            val datos = todasLasAlertas.map { alerta ->
                val peso = when (alerta.nivelAlerta) { 3 -> 3.0; 2 -> 2.0; else -> 1.0 }
                WeightedLatLng(com.google.android.gms.maps.model.LatLng(alerta.latitud, alerta.longitud), peso)
            }
            val provider = HeatmapTileProvider.Builder()
                .weightedData(datos)
                .radius(50)
                .build()
            heatmapOverlay = mapa.addTileOverlay(
                com.google.android.gms.maps.model.TileOverlayOptions().tileProvider(provider)
            )
            heatmapActivo = true
            Toast.makeText(this, "🔥 Mapa de calor activado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun aplicarFiltro(tipoId: Int) {
        mapa.clear()
        alertasMap.clear()
        val filtradas = if (tipoId == 0) todasLasAlertas
        else todasLasAlertas.filter { it.tipoDelitoId == tipoId }
        pintarAlertasEnMapa(filtradas)
        if (filtradas.isEmpty()) {
            Toast.makeText(this, "No hay alertas de este tipo", Toast.LENGTH_SHORT).show()
        }
    }

    private fun ejecutarBusqueda(query: String, etBusqueda: EditText) {
        // Ocultar teclado
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(etBusqueda.windowToken, 0)

        // 1. Filtrar alertas por ciudad
        val filtradas = todasLasAlertas.filter {
            it.ciudad.contains(query, ignoreCase = true) ||
                    (it.nombreCalle?.contains(query, ignoreCase = true) == true)
        }

        mapa.clear()
        alertasMap.clear()

        if (filtradas.isNotEmpty()) {
            pintarAlertasEnMapa(filtradas)
            mapa.animateCamera(CameraUpdateFactory.newLatLngZoom(
                LatLng(filtradas[0].latitud, filtradas[0].longitud), 13f))
            Toast.makeText(this, "${filtradas.size} alerta(s) en '$query'", Toast.LENGTH_SHORT).show()
        } else {
            // 2. Si no hay alertas, mover el mapa con Geocoding
            pintarAlertasEnMapa(todasLasAlertas)
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val urlStr = "https://maps.googleapis.com/maps/api/geocode/json?address=${java.net.URLEncoder.encode("$query, España", "UTF-8")}&key=AIzaSyCZfJpKJKWGldR2Hg9TqLqD3HG6F-5xX98"
                    val result = java.net.URL(urlStr).readText()
                    val json = org.json.JSONObject(result)
                    if (json.getString("status") == "OK") {
                        val location = json.getJSONArray("results")
                            .getJSONObject(0)
                            .getJSONObject("geometry")
                            .getJSONObject("location")
                        val lat = location.getDouble("lat")
                        val lng = location.getDouble("lng")
                        withContext(Dispatchers.Main) {
                            mapa.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat, lng), 13f))
                            Toast.makeText(this@MainActivity, "Sin alertas en '$query'", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@MainActivity, "Ubicación no encontrada", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity, "Error al buscar", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun mostrarFiltrarCiudad() {
        if (todasLasAlertas.isEmpty()) { Toast.makeText(this, "No hay alertas activas", Toast.LENGTH_SHORT).show(); return }
        val ciudades = todasLasAlertas.map { it.ciudad }.distinct().sorted().toTypedArray()
        AlertDialog.Builder(this)
            .setTitle("Filtrar por ciudad")
            .setItems(ciudades) { _, i ->
                val filtradas = todasLasAlertas.filter { it.ciudad == ciudades[i] }
                mapa.clear(); alertasMap.clear(); pintarAlertasEnMapa(filtradas)
                mostrarTab("mapa")
                if (filtradas.isNotEmpty()) mapa.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(filtradas[0].latitud, filtradas[0].longitud), 12f))
            }
            .setNegativeButton("Ver todas") { _, _ ->
                mapa.clear(); alertasMap.clear(); pintarAlertasEnMapa(todasLasAlertas)
                mapa.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(40.4168, -3.7038), 6f))
            }
            .setPositiveButton("Cerrar", null).show()
    }

    private fun mostrarEstadisticas() {
        AlertDialog.Builder(this)
            .setTitle("📊 Estadísticas")
            .setMessage("Total alertas: ${todasLasAlertas.size}\n\n🔴 Robos: ${todasLasAlertas.count{it.tipoDelitoId==1}}\n🟠 Altercados: ${todasLasAlertas.count{it.tipoDelitoId==2}}\n🟡 Sospechosos: ${todasLasAlertas.count{it.tipoDelitoId==3}}\n🏙️ Ciudades: ${todasLasAlertas.map{it.ciudad}.distinct().size}")
            .setPositiveButton("Cerrar", null).show()
    }

    private fun mostrarAcercaDe() {
        AlertDialog.Builder(this)
            .setTitle("AlertaReal")
            .setMessage("Sistema de alertas policiales en tiempo real.\n\nDesarrollado como TFG en ESIC Business & Marketing School.\n\nVersión 1.0")
            .setPositiveButton("Cerrar", null).show()
    }
}