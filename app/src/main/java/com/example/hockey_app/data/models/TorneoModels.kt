package com.example.hockey_app.data.models

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class TorneoResumen(
    val id: String,
    val nombre: String,
    val rama: String,
    val categoria: String,
    val division: String,
    val temporada: String
)

@Immutable
@Serializable
data class PartidoAHBA(
    val id: String,
    @SerialName("torneo_id") val torneoId: String,
    @SerialName("equipo_local") val nombreLocal: String,
    @SerialName("equipo_visita") val nombreVisitante: String,
    @SerialName("escudo_local") val escudoLocal: String? = null,
    @SerialName("escudo_visita") val escudoVisitante: String? = null,
    @SerialName("goles_local") val golesLocal: Int? = null,
    @SerialName("goles_visita") val golesVisitante: Int? = null,
    @SerialName("fecha") val horario: String? = null, // Renamed back to match UI
    @SerialName("numero_fecha") val numeroFecha: String,
    val jugado: Boolean
)

@Immutable
@Serializable
data class PosicionAHBA(
    @SerialName("torneo_id") val torneoId: String = "",
    @SerialName("equipo") val clubNombre: String,
    @SerialName("posicion") val puesto: Int,
    val puntos: Int,
    @SerialName("pj") val partidosJugados: Int = 0,
    @SerialName("pg") val partidosGanados: Int = 0,
    @SerialName("pe") val partidosEmpatados: Int = 0,
    @SerialName("pp") val partidosPerdidos: Int = 0,
    @SerialName("gf") val golesAFavor: Int = 0,
    @SerialName("gc") val golesEnContra: Int = 0,
    @SerialName("escudo") val escudoUrl: String? = null
)

@Immutable
@Serializable
data class GoleadorAHBA(
    @SerialName("torneo_id") val torneoId: String = "",
    @SerialName("jugador_nombre") val nombreCompleto: String,
    @SerialName("club_nombre") val clubNombre: String,
    val goles: Int,
    @SerialName("foto_url") val fotoUrl: String? = null
)

@Immutable
@Serializable
data class TorneoCompleto(
    val id: String,
    val todosLosPartidos: ImmutableList<PartidoAHBA>,
    val tablaGeneral: ImmutableList<PosicionAHBA>,
    val goleadores: ImmutableList<GoleadorAHBA>
)
