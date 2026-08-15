package com.example.hockey_app.features.tournaments.domain.usecases

import com.example.hockey_app.data.models.PartidoAHBA
import javax.inject.Inject

class CalculatePerformanceUseCase @Inject constructor() {
    
    /**
     * Calcula los puntos y etiquetas para el gráfico de rendimiento.
     * @param team Nombre del equipo a analizar.
     * @param partidos Lista completa de partidos del torneo.
     * @return Pair de (puntos, etiquetas) para los últimos 5 partidos jugados.
     */
    operator fun invoke(team: String, partidos: List<PartidoAHBA>): Pair<List<Float>, List<String>> {
        if (team.isEmpty()) return emptyList<Float>() to emptyList()
        
        val playedGames = partidos.filter { 
            it.jugado && (it.nombreLocal.contains(team, true) || it.nombreVisitante.contains(team, true))
        }.sortedBy { it.numeroFecha.toIntOrNull() ?: 0 }
        
        val last5 = playedGames.takeLast(5)
        val points = last5.map { 
            val isLocal = it.nombreLocal.contains(team, true)
            val myG = if(isLocal) it.golesLocal ?: 0 else it.golesVisitante ?: 0
            val opG = if(isLocal) it.golesVisitante ?: 0 else it.golesLocal ?: 0
            
            when {
                myG > opG -> 3f
                myG == opG -> 1f
                else -> 0f
            }
        }
        val labels = last5.map { "F${it.numeroFecha}" }
        
        return points to labels
    }
}
