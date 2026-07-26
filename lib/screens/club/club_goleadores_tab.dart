import 'package:flutter/material.dart';
import '../../utils/colors.dart';
import '../../models/club_model.dart';
import '../../models/goleador_model.dart';
import '../../data/goleadores_data.dart';
import '../../data/torneos_data.dart';

class ClubGoleadoresTab extends StatelessWidget {
  final Club club;

  const ClubGoleadoresTab({super.key, required this.club});

  @override
  Widget build(BuildContext context) {
    // Buscar goleadores del club en todos los torneos
    final torneosConGoleadores = ['t1', 't8']; // Torneos con datos de goleadores
    final List<MapEntry<String, GoleadorModel>> goleadoresClub = [];

    for (final torneoId in torneosConGoleadores) {
      final goleadores = GoleadoresData.getGoleadores(torneoId);
      for (final g in goleadores) {
        if (g.clubId == club.id) {
          goleadoresClub.add(MapEntry(torneoId, g));
        }
      }
    }

    return SingleChildScrollView(
      padding: const EdgeInsets.all(16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Text(
            'Goleadores del club',
            style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold, color: AppColors.textPrimary),
          ),
          const SizedBox(height: 16),

          if (goleadoresClub.isEmpty)
            Container(
              padding: const EdgeInsets.all(20),
              decoration: BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.circular(12),
              ),
              child: const Center(
                child: Text(
                  'No hay goleadores registrados para este club',
                  style: TextStyle(color: AppColors.textSecondary),
                ),
              ),
            )
          else
            ...goleadoresClub.map((entry) {
              final torneoNombre = _getTorneoNombre(entry.key);
              return _buildGoleadorCard(entry.value, torneoNombre);
            }),
        ],
      ),
    );
  }

  Widget _buildGoleadorCard(GoleadorModel g, String torneoNombre) {
    return Card(
      margin: const EdgeInsets.only(bottom: 8),
      elevation: 1,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
      child: Padding(
        padding: const EdgeInsets.all(12),
        child: Row(
          children: [
            Container(
              width: 40,
              height: 40,
              decoration: BoxDecoration(
                color: g.posicion <= 3 ? AppColors.secondary : AppColors.primary,
                shape: BoxShape.circle,
              ),
              child: Center(
                child: Text(
                  '${g.posicion}',
                  style: const TextStyle(color: Colors.white, fontWeight: FontWeight.bold),
                ),
              ),
            ),
            const SizedBox(width: 12),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    g.jugadorNombre,
                    style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 14),
                  ),
                  Text(
                    torneoNombre,
                    style: const TextStyle(fontSize: 11, color: AppColors.textSecondary),
                    maxLines: 1,
                    overflow: TextOverflow.ellipsis,
                  ),
                ],
              ),
            ),
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
              decoration: BoxDecoration(
                color: AppColors.primary,
                borderRadius: BorderRadius.circular(20),
              ),
              child: Row(
                children: [
                  const Icon(Icons.sports_score, color: Colors.white, size: 12),
                  const SizedBox(width: 4),
                  Text(
                    '${g.goles}',
                    style: const TextStyle(color: Colors.white, fontWeight: FontWeight.bold, fontSize: 12),
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  String _getTorneoNombre(String torneoId) {
    try {
      return TorneosData.torneos.firstWhere((t) => t.id == torneoId).nombre;
    } catch (e) {
      return 'Torneo';
    }
  }
}
