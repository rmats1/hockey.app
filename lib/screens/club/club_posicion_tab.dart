import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import '../../utils/colors.dart';
import '../../models/club_model.dart';
import '../../models/ahba_models.dart';
import '../../services/data_service.dart';

class ClubPosicionTab extends StatefulWidget {
  final Club club;
  final String? categoriaUsuario;

  const ClubPosicionTab({
    super.key,
    required this.club,
    this.categoriaUsuario,
  });

  @override
  State<ClubPosicionTab> createState() => _ClubPosicionTabState();
}

class _ClubPosicionTabState extends State<ClubPosicionTab> {
  bool _isLoading = true;
  List<Map<String, dynamic>> _posicionesPorTorneo = [];

  @override
  void initState() {
    super.initState();
    _loadPosiciones();
  }

  Future<void> _loadPosiciones() async {
    setState(() => _isLoading = true);
    
    final allTorneos = await DataService.instance.getTorneosResumen();
    final rama = widget.categoriaUsuario == 'Damas' ? 'F' : 'M';
    
    final torneosInteres = allTorneos.where((t) => t.rama == rama).toList();
    
    List<Map<String, dynamic>> resultados = [];

    for (var tResumen in torneosInteres) {
      final detalle = await DataService.instance.getTorneoCompleto(tResumen.id);
      if (detalle != null) {
        try {
          final pos = detalle.tablaGeneral.firstWhere((p) => 
            p.clubNombre.toLowerCase().contains(widget.club.nombreCorto.toLowerCase())
          );
          resultados.add({
            'torneo': tResumen.nombre,
            'posicion': pos,
            'totalClubes': detalle.tablaGeneral.length
          });
        } catch (_) {}
      }
    }

    if (mounted) {
      setState(() {
        _posicionesPorTorneo = resultados;
        _isLoading = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    if (_isLoading) {
      return const Center(child: CircularProgressIndicator(color: AppColors.primary));
    }

    if (_posicionesPorTorneo.isEmpty) {
      return _buildEmptyState();
    }

    return ListView.builder(
      padding: const EdgeInsets.all(20),
      itemCount: _posicionesPorTorneo.length,
      itemBuilder: (context, index) {
        final item = _posicionesPorTorneo[index];
        return _buildTorneoCard(item['torneo'], item['posicion'], item['totalClubes']);
      },
    );
  }

  Widget _buildEmptyState() {
    return Center(
      child: Padding(
        padding: const EdgeInsets.all(40),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const Icon(Icons.leaderboard_outlined, size: 64, color: Colors.grey),
            const SizedBox(height: 16),
            Text('No se encontraron tablas de posiciones para este club.', textAlign: TextAlign.center, style: GoogleFonts.montserrat(color: Colors.grey, fontSize: 14)),
          ],
        ),
      ),
    );
  }

  Widget _buildTorneoCard(String nombre, PosicionAHBA pos, int total) {
    return Container(
      margin: const EdgeInsets.only(bottom: 20),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(24),
        boxShadow: [BoxShadow(color: Colors.black.withOpacity(0.02), blurRadius: 10, offset: const Offset(0, 4))],
      ),
      child: Column(
        children: [
          Padding(
            padding: const EdgeInsets.all(20),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(nombre.toUpperCase(), style: GoogleFonts.montserrat(fontSize: 10, fontWeight: FontWeight.w800, color: AppColors.primary, letterSpacing: 1)),
                const SizedBox(height: 16),
                Row(
                  children: [
                    Container(
                      width: 50, height: 50,
                      decoration: const BoxDecoration(color: AppColors.secondary, shape: BoxShape.circle),
                      child: Center(child: Text('${pos.puesto}°', style: GoogleFonts.montserrat(color: Colors.white, fontSize: 18, fontWeight: FontWeight.w900))),
                    ),
                    const SizedBox(width: 16),
                    Expanded(
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text('${pos.puntos} Puntos', style: GoogleFonts.montserrat(fontSize: 16, fontWeight: FontWeight.bold)),
                          Text('de $total clubes participantes', style: const TextStyle(fontSize: 11, color: Colors.grey)),
                        ],
                      ),
                    ),
                  ],
                ),
                const SizedBox(height: 20),
                const Divider(height: 1),
                const SizedBox(height: 20),
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceAround,
                  children: [
                    _stat('PJ', pos.partidosJugados.toString()),
                    _stat('PG', pos.partidosGanados.toString()),
                    _stat('PE', pos.partidosEmpatados.toString()),
                    _stat('PP', pos.partidosPerdidos.toString()),
                    _stat('DG', pos.diferenciaGoles >= 0 ? '+${pos.diferenciaGoles}' : '${pos.diferenciaGoles}', color: pos.diferenciaGoles >= 0 ? Colors.green : Colors.red),
                  ],
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _stat(String label, String val, {Color? color}) {
    return Column(
      children: [
        Text(val, style: GoogleFonts.montserrat(fontWeight: FontWeight.w900, fontSize: 15, color: color ?? AppColors.textPrimary)),
        const SizedBox(height: 2),
        Text(label, style: const TextStyle(fontSize: 9, fontWeight: FontWeight.bold, color: Colors.grey)),
      ],
    );
  }
}
