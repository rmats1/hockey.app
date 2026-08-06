package com.example.hockey_app.data.models

import kotlinx.serialization.Serializable

@Serializable
data class UserModel(
    val id: String,
    val email: String,
    val nombre: String,
    val user_type: String = "jugador",
    val rama: String = "Damas",
    val categoria: String = "1ra",
    val division: String? = null,
    val club_id: String = "0",
    val club_nombre: String = "Sin Club",
    val club_escudo: String? = null,
    val numero_camiseta: Int? = null,
    val posicion: String? = null,
    val rol_cuerpo_tecnico: String? = null,
    val fecha_nacimiento: String? = null,
    val fecha_registro: String? = null,
    val foto_url: String? = null
) {
    fun toClubModel() = ClubModel(
        id = club_id,
        nombre = club_nombre,
        escudoUrl = club_escudo
    )
}
