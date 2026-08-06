package com.example.hockey_app.data.models

import kotlinx.serialization.Serializable

@Serializable
data class TrainingPlanModel(
    val id: String? = null,
    val club_id: String,
    val categoria: String,
    val division: String? = null,
    val plan_detalle: String,
    val fecha_creacion: String? = null,
    val activo: Boolean = true
)
