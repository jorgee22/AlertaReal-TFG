# 🚨 AlertaReal

**Sistema de gestión de alertas policiales geolocalizadas**  
Trabajo de Fin de Grado · 2025

---

## 📋 Descripción

AlertaReal es una plataforma compuesta por:

- **Backend REST** (`/alertaReal`) desarrollado en Spring Boot (Java)
- **Aplicación Android** (`/AlertaRealMobile`) desarrollada en Kotlin con Google Maps

### Funcionalidades principales

- 🔐 Autenticación de agentes policiales
- 📍 Creación de alertas con geolocalización (Google Geocoding API)
- 🗺️ Visualización de alertas activas sobre mapa (Google Maps SDK)
- 🔔 Filtrado por ciudad y tipo de alerta
- 📊 Estadísticas del sistema
- ⏱️ Desactivación automática de alertas pasadas 6 horas
- 🔄 Actualización automática del mapa cada 30 segundos

---

## 🚀 Puesta en marcha

### Backend (alertaReal)

**Requisitos:** Java 17+, Maven 3.8+, MariaDB 10+

1. Crear la base de datos en MariaDB:
```sql
CREATE DATABASE alertareal_db;
```

2. Las credenciales por defecto en `alertaReal/src/main/resources/application.properties` son:
    spring.datasource.url=jdbc:mariadb://localhost:3306/alertareal_db
    spring.datasource.username=root
    spring.datasource.password=root
3. Ejecutar:
```bash
cd alertaReal
mvn spring-boot:run
```

La API quedará disponible en `http://localhost:8080`.

---

### App Android (AlertaRealMobile)

**Requisitos:** Android Studio, API mínima 26 (Android 8.0)

1. Abre la carpeta `AlertaRealMobile` con Android Studio
2. En `AndroidManifest.xml` añade tu clave de Google Maps:
```xml
<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="TU_API_KEY"/>
```
3. En `RetrofitClient.kt` configura la IP de tu servidor:
```kotlin
private const val BASE_URL = "http://TU_IP:8080/"
```
4. Pulsa **Run ▶️**

---

## 🛠️ Tecnologías

| Capa | Tecnología |
|---|---|
| Backend | Java 17, Spring Boot 3, Spring Data JPA |
| Base de datos | MariaDB 10 |
| App móvil | Kotlin, Android SDK |
| Mapas | Google Maps SDK para Android |
| Geocodificación | Google Geocoding API |
| Comunicación | Retrofit 2, REST/JSON |
| UI | Material Design 3, CardView |

---

## 👤 Autor

**Jorge** · Trabajo de Fin de Grado · 2026
