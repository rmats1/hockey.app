import 'package:flutter/material.dart';
import '../../utils/colors.dart';
import '../../models/club_model.dart';
import '../../models/partido_model.dart';
import '../../data/partidos_data.dart';
import '../../data/clubes_ahba.dart';

class ClubPartidosTab extends StatelessWidget {
  final Club club;

  const ClubPartidosTab({super.key, required this.club});

  @override
  Widget build(BuildContext context) {
    final proximos = PartidosData.getProximosPartidos(club.id);
    final ultimos = PartidosData.getUltimosPartidos(club.id);

    return SingleChildScrollView(
      padding: const EdgeInsets.all(16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Próximos partidos
          const Text(
            'Próximos partidos',
            style: TextStyle(
              fontSize: 18,
              fontWeight: FontWeight.bold,
              color: AppColors.textPrimary,
            ),
          ),
          const SizedBox(height: 12),
          if (proximos.isEmpty)
            _buildEmpty('No hay próximos partidos')
          else
            ...proximos.map((p) => _buildPartidoCard(p, esProximo: true)),

          const SizedBox(height: 24),

          // Últimos resultados
          const Text(
            'Últimos resultados',
            style: TextStyle(
              fontSize: 18,
              fontWeight: FontWeight.bold,
              color: AppColors.textPrimary,
            ),
          ),
          const SizedBox(height: 12),
          if (ultimos.isEmpty)
            _buildEmpty('No hay resultados aún')
          else
            ...ultimos.map((p) => _buildPartidoCard(p, esProximo: false)),
        ],
      ),
    );
  }

  Widget _buildEmpty(String mensaje) {
    return Container(
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(12),
      ),
      child: Center(
        child: Text(
          mensaje,
          style: const TextStyle(color: AppColors.textSecondary),
        ),
      ),
    );
  }

  Widget _buildPartidoCard(PartidoModel partido, {required bool esProximo}) {
    final clubLocal = _getClubById(partido.clubLocalId);
    final clubVisitante = _getClubById(partido.clubVisitanteId);
    final esLocal = partido.clubLocalId == club.id;

    return Card(
      margin: const EdgeInsets.only(bottom: 8),
      elevation: 1,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
      child: Padding(
        padding: const EdgeInsets.all(12),
        child: Column(
          children: [
            // Jornada y fecha
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text(
                  partido.jornada ?? '',
                  style: const TextStyle(fontSize: 11, color: AppColors.textSecondary, fontWeight: FontWeight.w600),
                ),
                Text(
                  _formatearFecha(partido.fecha),
                  style: const TextStyle(fontSize: 11, color: AppColors.textSecondary),
                ),
              ],
            ),
            const SizedBox(height: 8),

            // Equipos
            Row(
              children: [
                Expanded(
                  child: _buildEquipoRow(
                    clubLocal?.nombreCorto ?? 'Local',
                    esLocal,
                    !esProximo && !partido.esEmpate && partido.ganadorId == partido.clubLocalId,
                  ),
                ),
                if (esProximo)
                  Column(
                    children: [
                      const Text('vs', style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold, color: AppColors.textSecondary)),
                      Text(partido.hora, style: const TextStyle(fontSize: 12, fontWeight: FontWeight.w600)),
                    ],
                  )
                else
                  Text(
                    '${partido.golesLocal} - ${partido.golesVisitante}',
                    style: const TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
                  ),
                Expanded(
                  child: _buildEquipoRow(
                    clubVisitante?.nombreCorto ?? 'Visitante',
                    !esLocal,
                    !esProximo && !partido.esEmpate && partido.ganadorId == partido.clubVisitanteId,
                  ),
                ),
              ],
            ),

            const SizedBox(height: 8),

            // Cancha
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                const Icon(Icons.location_on, size: 12, color: AppColors.textSecondary),
                const SizedBox(width: 4),
                Text(
                  partido.cancha,
                  style: const TextStyle(fontSize: 11, color: AppColors.textSecondary),
                ),
                const SizedBox(width: 8),
                Text(
                  '• ${esLocal ? "Local" : "Visitante"}',
                  style: TextStyle(
                    fontSize: 11,
                    color: esLocal ? AppColors.primary : AppColors.textSecondary,
                    fontWeight: FontWeight.w600,
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildEquipoRow(String nombre, bool esMiClub, bool esGanador) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.center,
      children: [
        Flexible(
          child: Text(
            nombre,
            textAlign: TextAlign.center,
            style: TextStyle(
              fontSize: 13,
              fontWeight: (esMiClub || esGanador) ? FontWeight.bold : FontWeight.w500,
              color: esMiClub ? AppColors.primary : AppColors.textPrimary,
            ),
          ),
        ),
        if (esGanador) ...[
          const SizedBox(width: 4),
          const Icon(Icons.emoji_events, size: 14, color: AppColors.secondary),
        ],
      ],
    );
  }

  String _formatearFecha(DateTime fecha) {
    const meses = ['Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun', 'Jul', 'Ago', 'Sep', 'Oct', 'Nov', 'Dic'];
    return '${fecha.day} ${meses[fecha.month - 1]}';
  }

  Club? _getClubById(String id) {
    try {
      return ClubesAhba.clubes.firstWhere((c) => c.id == id);
    } catch (e) {
      return null;
    }
  }
}
