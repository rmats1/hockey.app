import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import '../../utils/colors.dart';
import '../../models/user_model.dart';
import '../../services/supabase_service.dart';

class DashboardWidget extends StatefulWidget {
  final UserModel user;

  const DashboardWidget({super.key, required this.user});

  @override
  State<DashboardWidget> createState() => _DashboardWidgetState();
}

class _DashboardWidgetState extends State<DashboardWidget> {
  bool _isLoading = true;
  Map<String, dynamic>? _miPosicion;
  List<dynamic>? _misPartidos;
  String? _torneoNombre;
  dynamic _ultimoResultado;
  dynamic _proximoPartido;

  @override
  void initState() {
    super.initState();
    _loadDashboardData();
  }

  Future<void> _loadDashboardData() async {
    if (!mounted) return;
    setState(() => _isLoading = true);
    
    final resumen = await SupabaseService.instance.getMiResumen(
      widget.user.club.nombreCorto,
      widget.user.rama,
      widget.user.categoria,
      widget.user.division
    );

    if (mounted) {
      if (resumen != null) {
        setState(() {
          _miPosicion = resumen['posicion'];
          _misPartidos = resumen['partidos'];
          _torneoNombre = resumen['torneo_nombre'];
          
          if (_misPartidos != null && _misPartidos!.isNotEmpty) {
            final jugados = _misPartidos!.where((p) => p['jugado'] == true).toList();
            final porJugar = _misPartidos!.where((p) => p['jugado'] != true).toList();
            
            if (jugados.isNotEmpty) _ultimoResultado = jugados.last;
            if (porJugar.isNotEmpty) _proximoPartido = porJugar.first;
          }
          _isLoading = false;
        });
      } else {
        setState(() => _isLoading = false);
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    if (_isLoading) {
      return Container(
        height: 150, width: double.infinity,
        margin: const EdgeInsets.all(20),
        decoration: BoxDecoration(color: Colors.white, borderRadius: BorderRadius.circular(24)),
        child: const Center(child: CircularProgressIndicator(color: AppColors.primary)),
      );
    }

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Padding(
          padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 10),
          child: Text('RESUMEN DE TEMPORADA', 
            style: GoogleFonts.montserrat(fontSize: 10, fontWeight: FontWeight.w900, color: Colors.grey.shade600, letterSpacing: 1.5)),
        ),

        if (_miPosicion != null) _buildPosicionCard()
        else _buildNoDataCard('Sin datos oficiales registrados aún.'),

        const SizedBox(height: 12),

        Padding(
          padding: const EdgeInsets.symmetric(horizontal: 20),
          child: Row(
            children: [
              Expanded(child: _buildMatchSummaryCard('ÚLTIMO', _ultimoResultado)),
              const SizedBox(width: 12),
              Expanded(child: _buildMatchSummaryCard('PRÓXIMO', _proximoPartido)),
            ],
          ),
        ),
      ],
    );
  }

  Widget _buildNoDataCard(String msg) {
    return Container(
      margin: const EdgeInsets.symmetric(horizontal: 20),
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        color: Colors.white, 
        borderRadius: BorderRadius.circular(24),
        border: Border.all(color: Colors.grey.shade100)
      ),
      child: Row(
        children: [
          const Text('🏑', style: TextStyle(fontSize: 24)),
          const SizedBox(width: 16),
          Expanded(child: Text(msg, style: const TextStyle(fontSize: 12, color: Colors.grey, fontWeight: FontWeight.w600))),
        ],
      ),
    );
  }

  Widget _buildPosicionCard() {
    return Container(
      margin: const EdgeInsets.symmetric(horizontal: 20),
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(24),
        boxShadow: [BoxShadow(color: Colors.black.withOpacity(0.04), blurRadius: 15, offset: const Offset(0, 4))],
      ),
      child: Row(
        children: [
          Container(
            width: 50, height: 50,
            decoration: const BoxDecoration(color: AppColors.primary, shape: BoxShape.circle),
            child: Center(child: Text('${_miPosicion!['posicion']}°', style: GoogleFonts.montserrat(color: Colors.white, fontSize: 18, fontWeight: FontWeight.w900))),
          ),
          const SizedBox(width: 16),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(_torneoNombre?.toUpperCase() ?? 'TORNEO', 
                  style: const TextStyle(fontSize: 9, fontWeight: FontWeight.w900, color: AppColors.secondary, letterSpacing: 0.5), 
                  maxLines: 1, overflow: TextOverflow.ellipsis),
                Text('${_miPosicion!['puntos']} Puntos', style: GoogleFonts.montserrat(fontSize: 16, fontWeight: FontWeight.w900, color: AppColors.textPrimary)),
                Text('${_miPosicion!['pg']}G - ${_miPosicion!['pe']}E - ${_miPosicion!['pp']}P', 
                  style: const TextStyle(fontSize: 11, color: Colors.grey, fontWeight: FontWeight.bold)),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildMatchSummaryCard(String label, dynamic partido) {
    bool hasData = partido != null;
    final clubName = widget.user.club.nombreCorto.toLowerCase();

    return Container(
      height: 100,
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(20),
        border: Border.all(color: Colors.grey.shade100),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(label, style: const TextStyle(fontSize: 9, fontWeight: FontWeight.w900, color: AppColors.primary, letterSpacing: 1)),
          const Spacer(),
          if (hasData) ...[
            if (partido['jugado'] == true)
              Text('${partido['goles_local']} - ${partido['goles_visita']}', 
                style: GoogleFonts.montserrat(fontSize: 18, fontWeight: FontWeight.w900, color: AppColors.textPrimary))
            else
              Text(partido['fecha']?.toString().split(' ').last.substring(0, 5) ?? 'PEND.', 
                style: GoogleFonts.montserrat(fontSize: 14, fontWeight: FontWeight.w900, color: AppColors.secondary)),
            
            Text('vs ${partido['equipo_local'].toString().toLowerCase().contains(clubName) ? partido['equipo_visita'] : partido['equipo_local']}', 
              maxLines: 1, overflow: TextOverflow.ellipsis, 
              style: const TextStyle(fontSize: 9, fontWeight: FontWeight.bold, color: Colors.grey)),
          ] else
            const Text('-', style: TextStyle(color: Colors.grey, fontWeight: FontWeight.bold)),
        ],
      ),
    );
  }
}
