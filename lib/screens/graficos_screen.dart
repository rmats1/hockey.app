import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import '../utils/colors.dart';
import '../data/posiciones_data.dart';
import '../data/clubes_ahba.dart';

class GraficosScreen extends StatefulWidget {
  const GraficosScreen({super.key});

  @override
  State<GraficosScreen> createState() => _GraficosScreenState();
}

class _GraficosScreenState extends State<GraficosScreen> {
  String _categoria = 'Damas';

  @override
  Widget build(BuildContext context) {
    final posiciones = _categoria == 'Damas' ? PosicionesData.posicionesAperturaDamas1ra : PosicionesData.posicionesAperturaCaballeros1ra;
    final maxGoles = posiciones.map((p) => p.golesAFavor).reduce((a, b) => a > b ? a : b);
    final maxPuntos = posiciones.first.puntos;

    return Scaffold(
      backgroundColor: AppColors.background,
      appBar: AppBar(
        title: Text('Visualización', style: GoogleFonts.montserrat(fontWeight: FontWeight.bold)),
        backgroundColor: AppColors.primary,
        foregroundColor: Colors.white,
        elevation: 0,
      ),
      body: SingleChildScrollView(
        child: Column(
          children: [
            // Selector de Categoría
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
                  _buildSectionCard(
                    'PUNTOS POR CLUB', 
                    Icons.bar_chart_rounded, 
                    posiciones.map((p) {
                      final club = _getClub(p.clubId);
                      return _buildBarItem(club?.nombreCorto ?? 'Club', p.puntos, maxPuntos, AppColors.primary);
                    }).toList()
                  ),
                  
                  const SizedBox(height: 24),
                  
                  _buildSectionCard(
                    'GOLES A FAVOR', 
                    Icons.sports_score_rounded, 
                    posiciones.map((p) {
                      final club = _getClub(p.clubId);
                      return _buildBarItem(club?.nombreCorto ?? 'Club', p.golesAFavor, maxGoles, AppColors.secondary);
                    }).toList()
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

  Widget _buildSectionCard(String title, IconData icon, List<Widget> items) {
    return Container(
      padding: const EdgeInsets.all(20),
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
          Row(
            children: [
              Icon(icon, color: AppColors.secondary, size: 20),
              const SizedBox(width: 10),
              Text(title, style: GoogleFonts.montserrat(fontSize: 13, fontWeight: FontWeight.w800, color: AppColors.textPrimary, letterSpacing: 1)),
            ],
          ),
          const SizedBox(height: 24),
          ...items,
        ],
      ),
    );
  }

  Widget _buildBarItem(String label, int value, int max, Color color) {
    final double percentage = max > 0 ? value / max : 0.0;
    return Padding(
      padding: const EdgeInsets.only(bottom: 16),
      child: Column(
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Text(label, style: GoogleFonts.montserrat(fontSize: 11, fontWeight: FontWeight.w600)),
              Text('$value', style: TextStyle(fontSize: 12, fontWeight: FontWeight.bold, color: color)),
            ],
          ),
          const SizedBox(height: 8),
          Stack(
            children: [
              Container(
                height: 12,
                width: double.infinity,
                decoration: BoxDecoration(color: Colors.grey.shade50, borderRadius: BorderRadius.circular(6)),
              ),
              AnimatedContainer(
                duration: const Duration(seconds: 1),
                curve: Curves.easeOutQuart,
                height: 12,
                width: MediaQuery.of(context).size.width * 0.7 * percentage, // Aproximado
                decoration: BoxDecoration(
                  gradient: LinearGradient(colors: [color, color.withOpacity(0.7)]),
                  borderRadius: BorderRadius.circular(6),
                  boxShadow: [
                    BoxShadow(color: color.withOpacity(0.2), blurRadius: 4, offset: const Offset(0, 2))
                  ],
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }

  dynamic _getClub(String id) {
    try { return ClubesAhba.clubes.firstWhere((c) => c.id == id); } catch (e) { return null; }
  }
}
