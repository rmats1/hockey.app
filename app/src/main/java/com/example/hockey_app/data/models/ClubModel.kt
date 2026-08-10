package com.example.hockey_app.data.models

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ClubModel(
    val id: String,
    val nombre: String,
    val escudoUrl: String? = null
)
