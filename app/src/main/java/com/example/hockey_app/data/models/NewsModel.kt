package com.example.hockey_app.data.models

import kotlinx.serialization.Serializable

@Serializable
data class NewsModel(
    val id: Int? = null,
    val titulo: String? = null,
    val resumen: String? = null,
    val imagen_url: String? = null,
    val url: String? = null,
    val fuente: String? = null,
    val fecha_publicacion: String? = null
)
