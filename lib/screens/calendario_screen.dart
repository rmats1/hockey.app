import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import '../utils/colors.dart';
import '../data/partidos_data.dart';
import '../data/clubes_ahba.dart';

class CalendarioScreen extends StatefulWidget {
  const CalendarioScreen({super.key});

  @override
  State<CalendarioScreen> createState() => _CalendarioScreenState();
}

class _CalendarioScreenState extends State<CalendarioScreen> {
  late DateTime _mesActual;
  DateTime? _fechaSeleccionada;

  @override
  void initState() {
    super.initState();
    _mesActual = DateTime(DateTime.now().year, DateTime.now().month);
    _fechaSeleccionada = DateTime.now();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColors.background,
      appBar: AppBar(
        title: Text('Calendario', style: GoogleFonts.montserrat(fontWeight: FontWeight.bold)),
        backgroundColor: AppColors.primary,
        foregroundColor: Colors.white,
        elevation: 0,
      ),
      body: Column(
        children: [
          // Calendario Card
          Container(
            margin: const EdgeInsets.all(20),
            padding: const EdgeInsets.all(16),
            decoration: BoxDecoration(
              color: Colors.white,
              borderRadius: BorderRadius.circular(24),
              boxShadow: [
                BoxShadow(
                  color: Colors.black.withOpacity(0.05),
                  blurRadius: 20,
                  offset: const Offset(0, 10),
                )
              ],
            ),
            child: Column(
              children: [
                // Header del mes
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    _buildNavButton(Icons.chevron_left, () {
                      setState(() => _mesActual = DateTime(_mesActual.year, _mesActual.month - 1));
                    }),
                    Text(
                      _nombreMes(_mesActual).toUpperCase(),
                      style: GoogleFonts.montserrat(
                        fontSize: 16, 
                        fontWeight: FontWeight.w800, 
                        color: AppColors.primary,
                        letterSpacing: 1,
                      ),
                    ),
                    _buildNavButton(Icons.chevron_right, () {
                      setState(() => _mesActual = DateTime(_mesActual.year, _mesActual.month + 1));
                    }),
                  ],
                ),
                const SizedBox(height: 20),
                // Días de la semana
                Row(
                  children: ['L', 'M', 'M', 'J', 'V', 'S', 'D'].map((d) => Expanded(
                    child: Center(
                      child: Text(d, style: GoogleFonts.montserrat(
                        fontWeight: FontWeight.bold, 
                        color: Colors.grey.shade400, 
                        fontSize: 12
                      )),
                    ),
                  )).toList(),
                ),
                const SizedBox(height: 12),
                // Días del mes
                _buildDiasGrid(),
              ],
            ),
          ),
          
          // Header de la lista
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 8),
            child: Row(
              children: [
                const Icon(Icons.sports_hockey, color: AppColors.primary, size: 20),
                const SizedBox(width: 10),
                Text(
                  'PARTIDOS DEL DÍA',
                  style: GoogleFonts.montserrat(
                    fontSize: 14, 
                    fontWeight: FontWeight.bold, 
                    color: AppColors.textSecondary,
                    letterSpacing: 1,
                  ),
                ),
              ],
            ),
          ),

          // Partidos del día seleccionado
          Expanded(
            child: _buildPartidosDelDia(),
          ),
        ],
      ),
    );
  }

  Widget _buildNavButton(IconData icon, VoidCallback onPressed) {
    return InkWell(
      onTap: onPressed,
      borderRadius: BorderRadius.circular(12),
      child: Container(
        padding: const EdgeInsets.all(8),
        decoration: BoxDecoration(
          color: AppColors.primary.withOpacity(0.05),
          borderRadius: BorderRadius.circular(12),
        ),
        child: Icon(icon, color: AppColors.primary, size: 20),
      ),
    );
  }

  Widget _buildDiasGrid() {
    final primerDia = DateTime(_mesActual.year, _mesActual.month, 1);
    final diasEnMes = DateTime(_mesActual.year, _mesActual.month + 1, 0).day;
    final offset = (primerDia.weekday - 1) % 7;

    final todosPartidos = [
      ...PartidosData.getPartidos('t1'),
      ...PartidosData.getPartidos('t8'),
    ];
    final fechasConPartidos = todosPartidos.map((p) => p.fecha).toSet();

    return GridView.builder(
      shrinkWrap: true,
      physics: const NeverScrollableScrollPhysics(),
      gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
        crossAxisCount: 7, childAspectRatio: 1, mainAxisSpacing: 8, crossAxisSpacing: 8,
      ),
      itemCount: offset + diasEnMes,
      itemBuilder: (context, index) {
        if (index < offset) return const SizedBox();
        final dia = index - offset + 1;
        final fecha = DateTime(_mesActual.year, _mesActual.month, dia);
        final tienePartidos = fechasConPartidos.any((f) => _mismaFecha(f, fecha));
        final esSeleccionado = _fechaSeleccionada != null && _mismaFecha(_fechaSeleccionada!, fecha);
        final esHoy = _mismaFecha(fecha, DateTime.now());

        return InkWell(
          onTap: () => setState(() => _fechaSeleccionada = fecha),
          borderRadius: BorderRadius.circular(15),
          child: AnimatedContainer(
            duration: const Duration(milliseconds: 200),
            decoration: BoxDecoration(
              color: esSeleccionado ? AppColors.primary : (tienePartidos ? AppColors.primary.withOpacity(0.1) : null),
              borderRadius: BorderRadius.circular(15),
              border: esHoy && !esSeleccionado ? Border.all(color: AppColors.secondary, width: 2) : null,
              boxShadow: esSeleccionado ? [
                BoxShadow(color: AppColors.primary.withOpacity(0.3), blurRadius: 8, offset: const Offset(0, 4))
              ] : null,
            ),
            child: Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Text(
                    '$dia',
                    style: GoogleFonts.montserrat(
                      color: esSeleccionado ? Colors.white : AppColors.textPrimary,
                      fontWeight: esSeleccionado || tienePartidos ? FontWeight.bold : FontWeight.normal,
                      fontSize: 14,
                    ),
                  ),
                  if (tienePartidos && !esSeleccionado)
                    Container(
                      margin: const EdgeInsets.only(top: 2),
                      width: 4, height: 4,
                      decoration: const BoxDecoration(color: AppColors.secondary, shape: BoxShape.circle),
                    ),
                ],
              ),
            ),
          ),
        );
      },
    );
  }

  Widget _buildPartidosDelDia() {
    if (_fechaSeleccionada == null) {
      return Center(
        child: Text('SELECCIONÁ UNA FECHA', 
          style: GoogleFonts.montserrat(color: Colors.grey.shade400, fontWeight: FontWeight.bold, fontSize: 12))
      );
    }

    final todosPartidos = [
      ...PartidosData.getPartidos('t1'),
      ...PartidosData.getPartidos('t8'),
    ];
    final partidos = todosPartidos.where((p) => _mismaFecha(p.fecha, _fechaSeleccionada!)).toList();

    if (partidos.isEmpty) {
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(Icons.event_busy_rounded, size: 60, color: Colors.grey.shade300),
            const SizedBox(height: 12),
            Text('NO HAY PARTIDOS', 
              style: GoogleFonts.montserrat(color: Colors.grey.shade400, fontWeight: FontWeight.bold, fontSize: 12)),
          ],
        )
      );
    }

    return ListView.builder(
      padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 8),
      itemCount: partidos.length,
      itemBuilder: (context, index) {
        final p = partidos[index];
        final local = _getClub(p.clubLocalId);
        final visitante = _getClub(p.clubVisitanteId);
        return Container(
          margin: const EdgeInsets.only(bottom: 12),
          decoration: BoxDecoration(
            color: Colors.white,
            borderRadius: BorderRadius.circular(16),
            boxShadow: [
              BoxShadow(color: Colors.black.withOpacity(0.02), blurRadius: 10, offset: const Offset(0, 4))
            ],
          ),
          child: ListTile(
            contentPadding: const EdgeInsets.all(12),
            leading: CircleAvatar(
              backgroundColor: AppColors.primary.withOpacity(0.1),
              child: const Icon(Icons.sports_hockey, color: AppColors.primary, size: 20),
            ),
            title: Row(
              children: [
                Expanded(child: Text(local?.nombreCorto ?? 'Local', style: GoogleFonts.montserrat(fontWeight: FontWeight.bold, fontSize: 14))),
                Padding(
                  padding: const EdgeInsets.symmetric(horizontal: 8),
                  child: Text('VS', style: GoogleFonts.montserrat(fontSize: 10, fontWeight: FontWeight.w900, color: AppColors.secondary)),
                ),
                Expanded(child: Text(visitante?.nombreCorto ?? 'Visitante', style: GoogleFonts.montserrat(fontWeight: FontWeight.bold, fontSize: 14), textAlign: TextAlign.right)),
              ],
            ),
            subtitle: Padding(
              padding: const EdgeInsets.only(top: 8),
              child: Row(
                children: [
                  const Icon(Icons.access_time, size: 12, color: Colors.grey),
                  const SizedBox(width: 4),
                  Text(p.hora, style: const TextStyle(fontSize: 11)),
                  const SizedBox(width: 12),
                  const Icon(Icons.location_on_outlined, size: 12, color: Colors.grey),
                  const SizedBox(width: 4),
                  Expanded(child: Text(p.cancha, style: const TextStyle(fontSize: 11), overflow: TextOverflow.ellipsis)),
                ],
              ),
            ),
            trailing: Container(
              padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
              decoration: BoxDecoration(
                color: AppColors.secondary.withOpacity(0.1),
                borderRadius: BorderRadius.circular(8),
              ),
              child: Text(p.jornada?.toUpperCase() ?? '', style: const TextStyle(color: AppColors.secondary, fontSize: 9, fontWeight: FontWeight.bold)),
            ),
          ),
        );
      },
    );
  }

  bool _mismaFecha(DateTime a, DateTime b) => a.year == b.year && a.month == b.month && a.day == b.day;

  String _nombreMes(DateTime fecha) {
    const meses = ['Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio', 'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'];
    return '${meses[fecha.month - 1]} ${fecha.year}';
  }

  dynamic _getClub(String id) {
    try { return ClubesAhba.clubes.firstWhere((c) => c.id == id); } catch (e) { return null; }
  }
}
