package com.example.hockey_app.features.tournaments.domain.repositories

import com.example.hockey_app.data.models.GoleadorAHBA
import com.example.hockey_app.data.models.PartidoAHBA
import com.example.hockey_app.data.models.PosicionAHBA
import com.example.hockey_app.data.models.TorneoResumen

interface TournamentRepository {
    suspend fun getTorneos(): List<TorneoResumen>
    suspend fun getPosiciones(torneoId: String): List<PosicionAHBA>
    suspend fun getPartidos(torneoId: String): List<PartidoAHBA>
    suspend fun getGoleadores(torneoId: String): List<GoleadorAHBA>
    suspend fun getPartido(partidoId: String): PartidoAHBA?
}
