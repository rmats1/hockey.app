import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import '../utils/colors.dart';
import '../data/goleadores_data.dart';
import '../data/posiciones_data.dart';
import '../data/jugadores_data.dart';
import '../data/clubes_ahba.dart';

class EstadisticasScreen extends StatefulWidget {
  const EstadisticasScreen({super.key});

  @override
  State<EstadisticasScreen> createState() => _EstadisticasScreenState();
}

class _EstadisticasScreenState extends State<EstadisticasScreen> {
  String _categoria = 'Damas';

  @override
  Widget build(BuildContext context) {
    final torneoId = _categoria == 'Damas' ? 't1' : 't8';
    final goleadores = GoleadoresData.getGoleadores(torneoId);
    final posiciones = PosicionesData.getPosiciones(torneoId);
    final jugadores = JugadoresData.getJugadoresPorCategoria(_categoria);

    // Calcular stats
    int totalGoles = posiciones.fold(0, (sum, p) => sum + p.golesAFavor);
    int totalPartidos = posiciones.fold(0, (sum, p) => sum + p.partidosJugados);
    double promedioGoles = totalPartidos > 0 ? totalGoles / totalPartidos : 0;
    int maxGoleador = goleadores.isNotEmpty ? goleadores.first.goles : 0;

    return Scaffold(
      backgroundColor: AppColors.background,
      appBar: AppBar(
        title: Text('Estadísticas', style: GoogleFonts.montserrat(fontWeight: FontWeight.bold)),
        backgroundColor: AppColors.primary,
        foregroundColor: Colors.white,
        elevation: 0,
      ),
      body: SingleChildScrollView(
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Header Selector
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
                  _buildTabFiltro('DAMAS', 'Damas'),
                  const SizedBox(width: 12),
                  _buildTabFiltro('CABALLEROS', 'Caballeros'),
                ],
              ),
            ),

            Padding(
              padding: const EdgeInsets.all(20),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  // Dashboard de Stats
                  GridView.count(
                    shrinkWrap: true,
                    physics: const NeverScrollableScrollPhysics(),
                    crossAxisCount: 2,
                    mainAxisSpacing: 12,
                    crossAxisSpacing: 12,
                    childAspectRatio: 1.5,
                    children: [
                      _buildStatCard('GOLES', '$totalGoles', Icons.sports_score, AppColors.primary),
                      _buildStatCard('PARTIDOS', '$totalPartidos', Icons.sports, AppColors.info),
                      _buildStatCard('PROMEDIO', promedioGoles.toStringAsFixed(1), Icons.show_chart, AppColors.success),
                      _buildStatCard('MÁX. GOLES', '$maxGoleador', Icons.star_rounded, AppColors.secondary),
                    ],
                  ),

                  const SizedBox(height: 30),

                  // Top Goleadores Section
                  _buildSectionTitle('TOP GOLEADORES'),
                  const SizedBox(height: 16),
                  ...goleadores.take(5).map((g) => _buildGoleadorItem(g)),

                  const SizedBox(height: 30),

                  // Distribución por Club
                  _buildSectionTitle('JUGADORES POR CLUB'),
                  const SizedBox(height: 16),
                  Container(
                    padding: const EdgeInsets.all(20),
                    decoration: BoxDecoration(
                      color: Colors.white,
                      borderRadius: BorderRadius.circular(24),
                      boxShadow: [
                        BoxShadow(color: Colors.black.withOpacity(0.02), blurRadius: 10, offset: const Offset(0, 4))
                      ],
                    ),
                    child: Column(
                      children: _getCantidadPorClub(jugadores).entries.map((entry) {
                        return _buildProgressBar(entry.key, entry.value);
                      }).toList(),
                    ),
                  ),
                  const SizedBox(height: 30),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildTabFiltro(String label, String value) {
    final isSelected = _categoria == value;
    return Expanded(
      child: GestureDetector(
        onTap: () => setState(() => _categoria = value),
        child: AnimatedContainer(
          duration: const Duration(milliseconds: 200),
          padding: const EdgeInsets.symmetric(vertical: 12),
          decoration: BoxDecoration(
            color: isSelected ? Colors.white : Colors.white.withOpacity(0.05),
            borderRadius: BorderRadius.circular(15),
          ),
          child: Center(
            child: Text(
              label,
              style: GoogleFonts.montserrat(
                color: isSelected ? AppColors.primary : Colors.white70,
                fontWeight: FontWeight.bold,
                fontSize: 11,
                letterSpacing: 1,
              ),
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildStatCard(String label, String value, IconData icon, Color color) {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(20),
        boxShadow: [
          BoxShadow(color: Colors.black.withOpacity(0.02), blurRadius: 10, offset: const Offset(0, 4))
        ],
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Row(
            children: [
              Icon(icon, color: color, size: 16),
              const SizedBox(width: 6),
              Text(label, style: GoogleFonts.montserrat(fontSize: 9, fontWeight: FontWeight.w700, color: Colors.grey)),
            ],
          ),
          const SizedBox(height: 8),
          Text(value, style: GoogleFonts.montserrat(fontSize: 24, fontWeight: FontWeight.w800, color: AppColors.textPrimary)),
        ],
      ),
    );
  }

  Widget _buildSectionTitle(String title) {
    return Row(
      children: [
        Container(width: 4, height: 16, decoration: BoxDecoration(color: AppColors.secondary, borderRadius: BorderRadius.circular(2))),
        const SizedBox(width: 10),
        Text(title, style: GoogleFonts.montserrat(fontSize: 14, fontWeight: FontWeight.w800, color: AppColors.textPrimary, letterSpacing: 1)),
      ],
    );
  }

  Widget _buildGoleadorItem(dynamic g) {
    final club = _getClubById(g.clubId);
    return Container(
      margin: const EdgeInsets.only(bottom: 10),
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(16),
        boxShadow: [
          BoxShadow(color: Colors.black.withOpacity(0.02), blurRadius: 5, offset: const Offset(0, 2))
        ],
      ),
      child: Row(
        children: [
          Container(
            width: 32,
            height: 32,
            decoration: BoxDecoration(
              color: g.posicion <= 3 ? AppColors.secondary.withOpacity(0.1) : AppColors.primary.withOpacity(0.1),
              shape: BoxShape.circle,
            ),
            child: Center(
              child: Text('${g.posicion}', style: TextStyle(
                fontWeight: FontWeight.bold, 
                color: g.posicion <= 3 ? AppColors.secondary : AppColors.primary,
                fontSize: 12
              )),
            ),
          ),
          const SizedBox(width: 12),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(g.jugadorNombre, style: GoogleFonts.montserrat(fontWeight: FontWeight.bold, fontSize: 14)),
                Text(club?.nombreCorto ?? 'Club', style: const TextStyle(fontSize: 11, color: Colors.grey)),
              ],
            ),
          ),
          Container(
            padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
            decoration: BoxDecoration(color: AppColors.primary, borderRadius: BorderRadius.circular(10)),
            child: Text('${g.goles} ⚽', style: const TextStyle(color: Colors.white, fontWeight: FontWeight.bold, fontSize: 12)),
          ),
        ],
      ),
    );
  }

  Widget _buildProgressBar(String club, int value) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 16),
      child: Column(
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Text(club, style: GoogleFonts.montserrat(fontSize: 11, fontWeight: FontWeight.w600)),
              Text('$value', style: const TextStyle(fontSize: 11, fontWeight: FontWeight.bold)),
            ],
          ),
          const SizedBox(height: 6),
          ClipRRect(
            borderRadius: BorderRadius.circular(10),
            child: LinearProgressIndicator(
              value: value / 10, // Normalizado para ejemplo
              minHeight: 8,
              backgroundColor: Colors.grey.shade100,
              valueColor: const AlwaysStoppedAnimation<Color>(AppColors.primary),
            ),
          ),
        ],
      ),
    );
  }

  Map<String, int> _getCantidadPorClub(List jugadores) {
    final map = <String, int>{};
    for (final j in jugadores) {
      final key = j.club.nombreCorto;
      map[key] = (map[key] ?? 0) + 1;
    }
    return map;
  }

  dynamic _getClubById(String id) {
    try {
      return ClubesAhba.clubes.firstWhere((c) => c.id == id);
    } catch (e) {
      return null;
    }
  }
}
