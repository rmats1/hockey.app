package com.example.hockey_app.data.models

import kotlinx.serialization.Serializable

@Serializable
data class ComentarioModel(
    val id: String? = null,
    val partido_id: String,
    val user_id: String,
    val user_name: String,
    val texto: String,
    val fecha: String? = null,
    val likes: Int = 0
)
