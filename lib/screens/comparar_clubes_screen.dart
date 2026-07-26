import 'package:flutter/material.dart';
import '../utils/colors.dart';
import '../models/club_model.dart';
import '../data/clubes_ahba.dart';
import '../data/posiciones_data.dart';

class CompararClubesScreen extends StatefulWidget {
  const CompararClubesScreen({super.key});

  @override
  State<CompararClubesScreen> createState() => _CompararClubesScreenState();
}

class _CompararClubesScreenState extends State<CompararClubesScreen> {
  Club? _club1;
  Club? _club2;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColors.background,
      appBar: AppBar(
        title: const Text('Comparar Clubes'),
        backgroundColor: AppColors.primary,
        foregroundColor: Colors.white,
      ),
      body: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          children: [
            // Selectores
            Row(
              children: [
                Expanded(child: _buildClubSelector('Club 1', _club1, (c) => setState(() => _club1 = c))),
                const Padding(padding: EdgeInsets.symmetric(horizontal: 8), child: Text('VS', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 20, color: AppColors.primary))),
                Expanded(child: _buildClubSelector('Club 2', _club2, (c) => setState(() => _club2 = c))),
              ],
            ),
            const SizedBox(height: 24),
            if (_club1 != null && _club2 != null) _buildComparacion(),
          ],
        ),
      ),
    );
  }

  Widget _buildClubSelector(String label, Club? club, Function(Club) onSelect) {
    return GestureDetector(
      onTap: () async {
        final selected = await showModalBottomSheet<Club>(
          context: context,
          builder: (context) => SizedBox(
            height: 400,
            child: ListView.builder(
              itemCount: ClubesAhba.clubes.length,
              itemBuilder: (context, index) {
                final c = ClubesAhba.clubes[index];
                return ListTile(
                  leading: const Icon(Icons.sports_hockey, color: AppColors.primary),
                  title: Text(c.nombreCorto),
                  subtitle: Text(c.nombre, style: const TextStyle(fontSize: 11)),
                  onTap: () => Navigator.pop(context, c),
                );
              },
            ),
          ),
        );
        if (selected != null) onSelect(selected);
      },
      child: Container(
        padding: const EdgeInsets.all(12),
        decoration: BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.circular(12),
          border: Border.all(color: AppColors.primary),
        ),
        child: Column(
          children: [
            const Icon(Icons.sports_hockey, size: 30, color: AppColors.primary),
            const SizedBox(height: 4),
            Text(label, style: const TextStyle(fontSize: 10, color: AppColors.textSecondary)),
            Text(club?.nombreCorto ?? 'Elegir', style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 12), textAlign: TextAlign.center, maxLines: 2, overflow: TextOverflow.ellipsis),
          ],
        ),
      ),
    );
  }

  Widget _buildComparacion() {
    final pos1 = _getPos(_club1!.id);
    final pos2 = _getPos(_club2!.id);

    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(16),
        boxShadow: [BoxShadow(color: Colors.grey.shade200, blurRadius: 8, offset: const Offset(0, 2))],
      ),
      child: Column(
        children: [
          _buildStatRow('Posición', pos1?.posicion.toString() ?? '-', pos2?.posicion.toString() ?? '-'),
          _buildDivider(),
          _buildStatRow('Puntos', pos1?.puntos.toString() ?? '-', pos2?.puntos.toString() ?? '-'),
          _buildDivider(),
          _buildStatRow('PJ', pos1?.partidosJugados.toString() ?? '-', pos2?.partidosJugados.toString() ?? '-'),
          _buildDivider(),
          _buildStatRow('G', pos1?.partidosGanados.toString() ?? '-', pos2?.partidosGanados.toString() ?? '-'),
          _buildDivider(),
          _buildStatRow('E', pos1?.partidosEmpatados.toString() ?? '-', pos2?.partidosEmpatados.toString() ?? '-'),
          _buildDivider(),
          _buildStatRow('P', pos1?.partidosPerdidos.toString() ?? '-', pos2?.partidosPerdidos.toString() ?? '-'),
          _buildDivider(),
          _buildStatRow('GF', pos1?.golesAFavor.toString() ?? '-', pos2?.golesAFavor.toString() ?? '-'),
          _buildDivider(),
          _buildStatRow('GC', pos1?.golesEnContra.toString() ?? '-', pos2?.golesEnContra.toString() ?? '-'),
        ],
      ),
    );
  }

  Widget _buildStatRow(String label, String v1, String v2) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 8),
      child: Row(
        children: [
          Expanded(child: Center(child: Text(v1, style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold, color: AppColors.primary)))),
          SizedBox(width: 80, child: Center(child: Text(label, style: const TextStyle(fontSize: 11, color: AppColors.textSecondary)))),
          Expanded(child: Center(child: Text(v2, style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold, color: AppColors.secondary)))),
        ],
      ),
    );
  }

  Widget _buildDivider() => Divider(height: 1, color: Colors.grey.shade200);

  dynamic _getPos(String clubId) {
    final posiciones = [...PosicionesData.posicionesAperturaDamas1ra, ...PosicionesData.posicionesAperturaCaballeros1ra];
    try { return posiciones.firstWhere((p) => p.clubId == clubId); } catch (e) { return null; }
  }
}
