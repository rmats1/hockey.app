import 'package:flutter/material.dart';
import '../../utils/colors.dart';
import '../../models/partido_model.dart';
import '../../data/clubes_ahba.dart';

class PartidoCard extends StatelessWidget {
  final PartidoModel partido;
  final bool showTorneo;

  const PartidoCard({
    super.key,
    required this.partido,
    this.showTorneo = false,
  });

  @override
  Widget build(BuildContext context) {
    final clubLocal = _getClubById(partido.clubLocalId);
    final clubVisitante = _getClubById(partido.clubVisitanteId);
    final isFinalizado = partido.estado == 'finalizado';
    final isProgramado = partido.estado == 'programado';

    return Card(
      elevation: 1,
      margin: EdgeInsets.zero,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
      child: Padding(
        padding: const EdgeInsets.all(14),
        child: Column(
          children: [
            // Estado badge
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                _buildEstadoBadge(),
                if (partido.jornada != null)
                  Text(
                    partido.jornada!,
                    style: const TextStyle(
                      fontSize: 11,
                      color: AppColors.textSecondary,
                      fontWeight: FontWeight.w600,
                    ),
                  ),
              ],
            ),
            const SizedBox(height: 10),

            // Equipos
            Row(
              children: [
                Expanded(child: _buildEquipoColumn(clubLocal?.nombreCorto ?? 'Local', isFinalizado && partido.ganadorId == partido.clubLocalId)),
                Column(
                  children: [
                    if (isFinalizado) ...[
                      Text(
                        '${partido.golesLocal} - ${partido.golesVisitante}',
                        style: const TextStyle(
                          fontSize: 24,
                          fontWeight: FontWeight.bold,
                          color: AppColors.textPrimary,
                        ),
                      ),
                      const Text('FINAL', style: TextStyle(fontSize: 9, color: AppColors.textSecondary)),
                    ] else if (isProgramado) ...[
                      const Text('vs', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold, color: AppColors.textSecondary)),
                      const SizedBox(height: 2),
                      Text(partido.hora, style: const TextStyle(fontSize: 13, fontWeight: FontWeight.w600, color: AppColors.textPrimary)),
                    ],
                  ],
                ),
                Expanded(child: _buildEquipoColumn(clubVisitante?.nombreCorto ?? 'Visitante', isFinalizado && partido.ganadorId == partido.clubVisitanteId)),
              ],
            ),

            const SizedBox(height: 10),
            const Divider(height: 1),
            const SizedBox(height: 8),

            // Info
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                const Icon(Icons.location_on, size: 12, color: AppColors.textSecondary),
                const SizedBox(width: 4),
                Flexible(
                  child: Text(
                    partido.cancha,
                    style: const TextStyle(fontSize: 11, color: AppColors.textSecondary),
                    overflow: TextOverflow.ellipsis,
                  ),
                ),
                const SizedBox(width: 12),
                const Icon(Icons.calendar_today, size: 12, color: AppColors.textSecondary),
                const SizedBox(width: 4),
                Text(
                  _formatearFecha(partido.fecha),
                  style: const TextStyle(fontSize: 11, color: AppColors.textSecondary),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildEstadoBadge() {
    Color color;
    String texto;
    switch (partido.estado) {
      case 'en_curso':
        color = AppColors.success;
        texto = 'EN VIVO';
        break;
      case 'programado':
        color = AppColors.info;
        texto = 'PROGRAMADO';
        break;
      case 'finalizado':
        color = AppColors.textSecondary;
        texto = 'FINALIZADO';
        break;
      default:
        color = AppColors.textSecondary;
        texto = partido.estado.toUpperCase();
    }

    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 3),
      decoration: BoxDecoration(
        color: color,
        borderRadius: BorderRadius.circular(12),
      ),
      child: Text(
        texto,
        style: const TextStyle(
          color: Colors.white,
          fontSize: 9,
          fontWeight: FontWeight.bold,
        ),
      ),
    );
  }

  Widget _buildEquipoColumn(String nombre, bool esGanador) {
    return Column(
      children: [
        Text(
          nombre,
          textAlign: TextAlign.center,
          style: TextStyle(
            fontSize: 13,
            fontWeight: esGanador ? FontWeight.bold : FontWeight.w500,
            color: esGanador ? AppColors.primary : AppColors.textPrimary,
          ),
        ),
        if (esGanador)
          const Padding(
            padding: EdgeInsets.only(top: 2),
            child: Icon(Icons.emoji_events, size: 14, color: AppColors.secondary),
          ),
      ],
    );
  }

  String _formatearFecha(DateTime fecha) {
    const meses = ['Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun', 'Jul', 'Ago', 'Sep', 'Oct', 'Nov', 'Dic'];
    return '${fecha.day} ${meses[fecha.month - 1]}';
  }

  dynamic _getClubById(String id) {
    try {
      return ClubesAhba.clubes.firstWhere((c) => c.id == id);
    } catch (e) {
      return null;
    }
  }
}
