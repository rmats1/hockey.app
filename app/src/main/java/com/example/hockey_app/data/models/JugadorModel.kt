package com.example.hockey_app.data.models

import kotlinx.serialization.Serializable

@Serializable
data class JugadorModel(
    val id: String,
    val nombre: String,
    val club_id: String = "0",
    val club_nombre: String = "Sin Club",
    val club_escudo: String? = null,
    val categoria: String = "Damas",
    val division: String = "1ra",
    val numero_camiseta: Int = 0,
    val posicion: String = "Jugador",
    val goles: Int = 0,
    val partidos: Int = 0,
    val fecha_nacimiento: String? = null,
    val foto_url: String? = null
)
