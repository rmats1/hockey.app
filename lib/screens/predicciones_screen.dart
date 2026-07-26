import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import '../utils/colors.dart';
import '../models/partido_model.dart';
import '../data/partidos_data.dart';
import '../data/predicciones_data.dart';
import '../data/clubes_ahba.dart';

class PrediccionesScreen extends StatefulWidget {
  const PrediccionesScreen({super.key});

  @override
  State<PrediccionesScreen> createState() => _PrediccionesScreenState();
}

class _PrediccionesScreenState extends State<PrediccionesScreen> {
  @override
  Widget build(BuildContext context) {
    final proximos = [
      ...PartidosData.getPartidos('t1'),
      ...PartidosData.getPartidos('t8'),
    ].where((p) => p.estado == 'programado').toList();

    return Scaffold(
      backgroundColor: AppColors.background,
      appBar: AppBar(
        title: Text('Predicciones', style: GoogleFonts.montserrat(fontWeight: FontWeight.bold)),
        backgroundColor: AppColors.primary,
        foregroundColor: Colors.white,
        elevation: 0,
      ),
      body: Column(
        children: [
          // Header Informativo
          Container(
            padding: const EdgeInsets.fromLTRB(20, 10, 20, 20),
            decoration: const BoxDecoration(
              color: AppColors.primary,
              borderRadius: BorderRadius.only(
                bottomLeft: Radius.circular(24),
                bottomRight: Radius.circular(24),
              ),
            ),
            child: Row(
              children: [
                Container(
                  padding: const EdgeInsets.all(12),
                  decoration: BoxDecoration(color: Colors.white.withOpacity(0.1), borderRadius: BorderRadius.circular(16)),
                  child: const Icon(Icons.psychology, color: AppColors.secondary, size: 28),
                ),
                const SizedBox(width: 16),
                const Expanded(
                  child: Text(
                    'Participá en la penca de la comunidad y demostrá cuánto sabés de hockey.',
                    style: TextStyle(color: Colors.white70, fontSize: 12),
                  ),
                ),
              ],
            ),
          ),

          Expanded(
            child: ListView.builder(
              padding: const EdgeInsets.all(20),
              itemCount: proximos.length,
              itemBuilder: (context, index) => _buildPartidoCard(proximos[index]),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildPartidoCard(PartidoModel p) {
    final local = _getClub(p.clubLocalId);
    final visitante = _getClub(p.clubVisitanteId);
    final predicciones = PrediccionesData.getByPartido(p.id);

    return Container(
      margin: const EdgeInsets.only(bottom: 20),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(24),
        boxShadow: [
          BoxShadow(color: Colors.black.withOpacity(0.02), blurRadius: 10, offset: const Offset(0, 4))
        ],
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Header del partido
          Padding(
            padding: const EdgeInsets.fromLTRB(16, 16, 16, 12),
            child: Row(
              children: [
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                  decoration: BoxDecoration(color: AppColors.primary.withOpacity(0.05), borderRadius: BorderRadius.circular(8)),
                  child: Text(p.jornada?.toUpperCase() ?? '', style: const TextStyle(color: AppColors.primary, fontSize: 9, fontWeight: FontWeight.bold)),
                ),
                const Spacer(),
                Text('${_formatearFecha(p.fecha)} • ${p.hora}', style: const TextStyle(color: Colors.grey, fontSize: 11)),
              ],
            ),
          ),

          // Equipos
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 16),
            child: Row(
              children: [
                Expanded(child: _buildClubInfo(local?.nombreCorto ?? 'Local', true)),
                const Padding(
                  padding: EdgeInsets.symmetric(horizontal: 10),
                  child: Text('VS', style: TextStyle(fontWeight: FontWeight.w900, color: AppColors.secondary, fontSize: 14)),
                ),
                Expanded(child: _buildClubInfo(visitante?.nombreCorto ?? 'Vis.', false)),
              ],
            ),
          ),

          const SizedBox(height: 20),

          // Sección de Predicciones
          if (predicciones.isNotEmpty) ...[
            const Divider(height: 1),
            Container(
              padding: const EdgeInsets.all(16),
              color: Colors.grey.shade50,
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text('COMUNIDAD', style: GoogleFonts.montserrat(fontSize: 10, fontWeight: FontWeight.w800, color: Colors.grey, letterSpacing: 1)),
                  const SizedBox(height: 12),
                  ...predicciones.take(2).map((pr) => Padding(
                    padding: const EdgeInsets.only(bottom: 8),
                    child: Row(
                      children: [
                    const CircleAvatar(radius: 10, backgroundColor: AppColors.primary, child: Icon(Icons.person, size: 10, color: Colors.white)),
                        const SizedBox(width: 8),
                        Text(pr.userName, style: const TextStyle(fontSize: 12, fontWeight: FontWeight.w500)),
                        const Spacer(),
                        Text('${pr.golesLocal} - ${pr.golesVisitante}', style: const TextStyle(fontSize: 12, fontWeight: FontWeight.bold, color: AppColors.primary)),
                      ],
                    ),
                  )),
                ],
              ),
            ),
          ],

          // Botón Acción
          InkWell(
            onTap: () => _hacerPrediccion(p),
            borderRadius: const BorderRadius.vertical(bottom: Radius.circular(24)),
            child: Container(
              width: double.infinity,
              padding: const EdgeInsets.symmetric(vertical: 14),
              decoration: const BoxDecoration(
                color: AppColors.primary,
                borderRadius: BorderRadius.vertical(bottom: Radius.circular(24)),
              ),
              child: Center(
                child: Row(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    const Icon(Icons.psychology, color: Colors.white, size: 18),
                    const SizedBox(width: 8),
                    Text('MI PREDICCIÓN', style: GoogleFonts.montserrat(color: Colors.white, fontWeight: FontWeight.bold, fontSize: 12, letterSpacing: 1)),
                  ],
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildClubInfo(String name, bool isLocal) {
    return Column(
      children: [
        Container(
          width: 50, height: 50,
          decoration: BoxDecoration(color: Colors.grey.shade50, shape: BoxShape.circle),
          child: const Icon(Icons.sports_hockey, color: Colors.grey, size: 30),
        ),
        const SizedBox(height: 8),
        Text(name, style: GoogleFonts.montserrat(fontWeight: FontWeight.bold, fontSize: 13), textAlign: TextAlign.center),
      ],
    );
  }

  void _hacerPrediccion(PartidoModel p) {
    int golesL = 0;
    int golesV = 0;

    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      backgroundColor: Colors.transparent,
      builder: (context) => StatefulBuilder(
        builder: (context, setModalState) => Container(
          padding: const EdgeInsets.all(30),
          decoration: const BoxDecoration(color: Colors.white, borderRadius: BorderRadius.vertical(top: Radius.circular(32))),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              Text('TU PREDICCIÓN', style: GoogleFonts.montserrat(fontSize: 18, fontWeight: FontWeight.bold)),
              const SizedBox(height: 30),
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceAround,
                children: [
                  _buildCounter('LOCAL', golesL, (v) => setModalState(() => golesL = v)),
                  const Text('-', style: TextStyle(fontSize: 30, fontWeight: FontWeight.bold, color: Colors.grey)),
                  _buildCounter('VISITANTE', golesV, (v) => setModalState(() => golesV = v)),
                ],
              ),
              const SizedBox(height: 40),
              SizedBox(
                width: double.infinity,
                child: ElevatedButton(
                  onPressed: () {
                    Navigator.pop(context);
                    ScaffoldMessenger.of(context).showSnackBar(
                      SnackBar(
                        content: Text('¡Predicción guardada!', style: GoogleFonts.montserrat()),
                        backgroundColor: AppColors.success,
                        behavior: SnackBarBehavior.floating,
                      ),
                    );
                  },
                  child: const Text('CONFIRMAR'),
                ),
              ),
              const SizedBox(height: 10),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildCounter(String label, int value, Function(int) onChange) {
    return Column(
      children: [
        Text(label, style: const TextStyle(fontSize: 10, fontWeight: FontWeight.bold, color: Colors.grey)),
        const SizedBox(height: 12),
        Row(
          children: [
            _countBtn(Icons.remove, () => value > 0 ? onChange(value - 1) : null),
            const SizedBox(width: 15),
            Text('$value', style: const TextStyle(fontSize: 28, fontWeight: FontWeight.bold)),
            const SizedBox(width: 15),
            _countBtn(Icons.add, () => onChange(value + 1)),
          ],
        ),
      ],
    );
  }

  Widget _countBtn(IconData icon, VoidCallback onTap) {
    return InkWell(
      onTap: onTap,
      child: Container(
        padding: const EdgeInsets.all(8),
        decoration: BoxDecoration(border: Border.all(color: Colors.grey.shade300), shape: BoxShape.circle),
        child: Icon(icon, size: 20),
      ),
    );
  }

  String _formatearFecha(DateTime f) {
    const meses = ['Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun', 'Jul', 'Ago', 'Sep', 'Oct', 'Nov', 'Dic'];
    return '${f.day} ${meses[f.month - 1]}';
  }

  dynamic _getClub(String id) {
    try { return ClubesAhba.clubes.firstWhere((c) => c.id == id); } catch (e) { return null; }
  }
}
