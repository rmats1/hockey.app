package com.example.hockey_app.data.models

import kotlinx.serialization.Serializable

@Serializable
data class CallUpModel(
    val id: String? = null,
    val user_id: String,
    val club_id: String,
    val torneo_id: String? = null,
    val fecha_partido: String? = null,
    val lugar: String? = null,
    val horario_citacion: String? = null,
    val categoria: String? = null,
    val rival_nombre: String? = null
)
