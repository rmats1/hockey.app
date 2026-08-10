package com.example.hockey_app.data.models

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
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
    val nombreLocal: String,
    val nombreVisitante: String,
    val escudoLocal: String? = null,
    val escudoVisitante: String? = null,
    val golesLocal: Int? = null,
    val golesVisitante: Int? = null,
    val horario: String? = null,
    val numeroFecha: String,
    val jugado: Boolean
)

@Immutable
@Serializable
data class PosicionAHBA(
    val puesto: Int,
    val clubNombre: String,
    val escudoUrl: String? = null,
    val puntos: Int,
    val partidosJugados: Int = 0,
    val partidosGanados: Int = 0,
    val partidosEmpatados: Int = 0,
    val partidosPerdidos: Int = 0,
    val golesAFavor: Int = 0,
    val golesEnContra: Int = 0
)

@Immutable
@Serializable
data class GoleadorAHBA(
    val nombreCompleto: String,
    val clubNombre: String,
    val fotoUrl: String? = null,
    val goles: Int
)

@Immutable
@Serializable
data class TorneoCompleto(
    val id: String,
    val todosLosPartidos: ImmutableList<PartidoAHBA>,
    val tablaGeneral: ImmutableList<PosicionAHBA>,
    val goleadores: ImmutableList<GoleadorAHBA>
)
