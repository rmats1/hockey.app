package com.example.hockey_app.data.models

import kotlinx.serialization.Serializable

@Serializable
data class PrediccionModel(
    val id: String? = null,
    val partido_id: String,
    val user_id: String,
    val user_name: String,
    val goles_local: Int,
    val goles_visitante: Int,
    val fecha: String? = null
)
