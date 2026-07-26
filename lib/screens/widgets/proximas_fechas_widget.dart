import 'package:flutter/material.dart';
import '../../utils/colors.dart';
import '../../models/partido_model.dart';
import '../../data/partidos_data.dart';
import '../../data/clubes_ahba.dart';

class ProximasFechasWidget extends StatelessWidget {
  final String? clubId; // Si es null muestra todos

  const ProximasFechasWidget({super.key, this.clubId});

  @override
  Widget build(BuildContext context) {
    final List<PartidoModel> partidos = clubId != null
        ? PartidosData.getProximosPartidos(clubId!)
        : [
            ...PartidosData.getPartidos('t1'),
            ...PartidosData.getPartidos('t8'),
          ].where((p) => p.estado == 'programado').toList();

    final proximos = (partidos.toList()..sort((a, b) => a.fecha.compareTo(b.fecha)))
        .take(5)
        .toList();

    if (proximos.isEmpty) {
      return const Padding(
        padding: EdgeInsets.all(16),
        child: Text('No hay próximos partidos', style: TextStyle(color: AppColors.textSecondary)),
      );
    }

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Padding(
          padding: EdgeInsets.fromLTRB(16, 16, 16, 8),
          child: Text(
            'Próximas fechas',
            style: TextStyle(
              fontSize: 16,
              fontWeight: FontWeight.bold,
              color: AppColors.textPrimary,
            ),
          ),
        ),
        ...proximos.map((p) => _buildPartidoItem(p)),
      ],
    );
  }

  Widget _buildPartidoItem(PartidoModel p) {
    final clubLocal = _getClubById(p.clubLocalId);
    final clubVisitante = _getClubById(p.clubVisitanteId);
    final dias = p.fecha.difference(DateTime.now()).inDays;

    return Container(
      margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 4),
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(12),
        boxShadow: [
          BoxShadow(
            color: Colors.grey.shade200,
            blurRadius: 4,
            offset: const Offset(0, 2),
          ),
        ],
      ),
      child: Row(
        children: [
          // Fecha
          Container(
            width: 50,
            padding: const EdgeInsets.symmetric(vertical: 6),
            decoration: BoxDecoration(
              color: dias <= 3 ? AppColors.secondary : AppColors.primary,
              borderRadius: BorderRadius.circular(8),
            ),
            child: Column(
              children: [
                Text(
                  dias == 0 ? 'HOY' : (dias == 1 ? 'MAÑ' : '${dias}d'),
                  style: const TextStyle(
                    color: Colors.white,
                    fontSize: 10,
                    fontWeight: FontWeight.bold,
                  ),
                ),
                Text(
                  '${p.fecha.day}',
                  style: const TextStyle(
                    color: Colors.white,
                    fontSize: 18,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ],
            ),
          ),
          const SizedBox(width: 12),
          // Equipos
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  '${clubLocal?.nombreCorto ?? "Local"} vs ${clubVisitante?.nombreCorto ?? "Visitante"}',
                  style: const TextStyle(
                    fontSize: 13,
                    fontWeight: FontWeight.w600,
                  ),
                ),
                const SizedBox(height: 2),
                Text(
                  '${p.hora} • ${p.cancha}',
                  style: const TextStyle(
                    fontSize: 11,
                    color: AppColors.textSecondary,
                  ),
                ),
              ],
            ),
          ),
          const Icon(Icons.chevron_right, color: AppColors.textSecondary),
        ],
      ),
    );
  }

  dynamic _getClubById(String id) {
    try {
      return ClubesAhba.clubes.firstWhere((c) => c.id == id);
    } catch (e) {
      return null;
    }
  }
}
