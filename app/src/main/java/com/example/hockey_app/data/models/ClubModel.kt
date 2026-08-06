package com.example.hockey_app.data.models

import kotlinx.serialization.Serializable

@Serializable
data class ClubModel(
    val id: String,
    val nombre: String,
    val escudoUrl: String? = null
)
