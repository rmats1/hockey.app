package com.example.hockey_app.data.models

import kotlinx.serialization.Serializable

@Serializable
data class FavoritoModel(
    val user_id: String,
    val club_id: String,
    val fecha_agregado: String
)
