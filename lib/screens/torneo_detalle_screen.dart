import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:url_launcher/url_launcher.dart';
import '../utils/colors.dart';
import '../models/ahba_models.dart';
import '../services/supabase_service.dart';
import '../services/data_service.dart';

enum TorneoDetalleMode { posiciones, fixture }

class TorneoDetalleScreen extends StatefulWidget {
  final TorneoResumen torneoResumen;
  final TorneoDetalleMode mode;

  const TorneoDetalleScreen({
    super.key, 
    required this.torneoResumen, 
    this.mode = TorneoDetalleMode.fixture
  });

  @override
  State<TorneoDetalleScreen> createState() => _TorneoDetalleScreenState();
}

class _TorneoDetalleScreenState extends State<TorneoDetalleScreen> {
  bool _isLoading = true;
  List<dynamic> _posiciones = [];
  List<dynamic> _partidos = [];
  List<dynamic> _goleadores = [];

  @override
  void initState() {
    super.initState();
    _loadData();
  }

  Future<void> _loadData() async {
    if (!mounted) return;
    setState(() => _isLoading = true);
    
    final String tId = widget.torneoResumen.id;
    
    // 1. Intentar traer de Supabase
    final results = await Future.wait([
      SupabaseService.instance.getPosiciones(tId),
      SupabaseService.instance.getPartidos(tId),
      SupabaseService.instance.getGoleadores(tId),
    ]);

    _posiciones = results[0];
    _partidos = results[1];
    _goleadores = results[2];

    // 2. Si Supabase está vacío (IDs nuevos de 2026), usar Fetch en Vivo con tu lógica de descifrado
    if (_partidos.isEmpty || _goleadores.isEmpty) {
      final torneoLive = await DataService.instance.getTorneoCompleto(tId);
      if (torneoLive != null) {
        if (_partidos.isEmpty) {
          _partidos = torneoLive.todosLosPartidos.map((p) => {
            'equipo_local': p.nombreLocal,
            'equipo_visita': p.nombreVisitante,
            'escudo_local': p.escudoLocal,
            'escudo_visita': p.escudoVisitante,
            'goles_local': p.golesLocal,
            'goles_visita': p.golesVisitante,
            'jugado': p.jugado,
            'numero_fecha': p.numeroFecha,
            'fecha': p.horario,
          }).toList();
        }
        if (_goleadores.isEmpty) {
          _goleadores = torneoLive.goleadores.map((g) => {
            'jugador_nombre': g.nombreCompleto,
            'club_nombre': g.clubNombre,
            'goles': g.goles,
            'foto_url': g.fotoUrl,
          }).toList();
        }
        if (_posiciones.isEmpty) {
          _posiciones = torneoLive.tablaGeneral.map((p) => {
            'posicion': p.puesto,
            'equipo': p.clubNombre,
            'puntos': p.puntos,
          }).toList();
        }
      }
    }

    if (mounted) {
      setState(() => _isLoading = false);
    }
  }

  Future<void> _openMaps(String clubName) async {
    final query = Uri.encodeComponent('$clubName Hockey Buenos Aires Argentina');
    final url = Uri.parse('https://www.google.com/maps/search/?api=1&query=$query');
    if (await canLaunchUrl(url)) {
      await launchUrl(url, mode: LaunchMode.externalApplication);
    }
  }

  @override
  Widget build(BuildContext context) {
    final isPosiciones = widget.mode == TorneoDetalleMode.posiciones;

    return DefaultTabController(
      length: isPosiciones ? 1 : 2,
      child: Scaffold(
        backgroundColor: AppColors.background,
        appBar: AppBar(
          title: Column(
            children: [
              Text(widget.torneoResumen.nombre.toUpperCase(), 
                style: GoogleFonts.montserrat(fontSize: 13, fontWeight: FontWeight.w900, color: Colors.white)),
              const Text('TEMPORADA 2026', style: TextStyle(fontSize: 9, color: Colors.white70, fontWeight: FontWeight.bold)),
            ],
          ),
          backgroundColor: AppColors.primary,
          foregroundColor: Colors.white,
          elevation: 4,
          bottom: TabBar(
            indicatorColor: AppColors.secondary,
            indicatorWeight: 4,
            labelColor: Colors.white,
            unselectedLabelColor: Colors.white.withOpacity(0.7),
            labelStyle: GoogleFonts.montserrat(fontWeight: FontWeight.w900, fontSize: 13, letterSpacing: 1),
            tabs: isPosiciones 
              ? [const Tab(text: 'TABLA DE POSICIONES')]
              : [const Tab(text: 'FIXTURE'), const Tab(text: 'GOLEADORES')],
          ),
        ),
        body: _isLoading 
          ? const Center(child: CircularProgressIndicator(color: AppColors.primary))
          : TabBarView(
              children: isPosiciones 
                ? [_buildTabla()]
                : [_buildFixture(), _buildGoleadores()],
            ),
      ),
    );
  }

  Widget _buildTabla() {
    if (_posiciones.isEmpty) return _noData('No hay posiciones registradas aún.');
    return ListView.builder(
      padding: const EdgeInsets.all(16),
      itemCount: _posiciones.length,
      itemBuilder: (context, i) {
        final p = _posiciones[i];
        return Card(
          margin: const EdgeInsets.only(bottom: 8),
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(15)),
          child: ListTile(
            leading: CircleAvatar(
              backgroundColor: AppColors.primary,
              radius: 15,
              child: Text('${p['posicion']}', style: const TextStyle(color: Colors.white, fontSize: 12, fontWeight: FontWeight.bold)),
            ),
            title: Text(p['equipo'], style: GoogleFonts.montserrat(fontWeight: FontWeight.w900, fontSize: 13, color: AppColors.textPrimary)),
            trailing: Text('${p['puntos']} PTS', style: GoogleFonts.montserrat(fontWeight: FontWeight.w900, color: AppColors.primary)),
          ),
        );
      },
    );
  }

  Widget _buildFixture() {
    if (_partidos.isEmpty) return _noData('No hay partidos programados aún.');
    return ListView.builder(
      padding: const EdgeInsets.all(16),
      itemCount: _partidos.length,
      itemBuilder: (context, i) {
        final p = _partidos[i];
        final bool jugado = p['jugado'] == true;
        final String golesLocal = jugado ? (p['goles_local']?.toString() ?? '-') : '-';
        final String golesVisita = jugado ? (p['goles_visita']?.toString() ?? '-') : '-';
        
        return Card(
          margin: const EdgeInsets.only(bottom: 12),
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
          elevation: 2,
          shadowColor: Colors.black12,
          color: Colors.white,
          child: Padding(
            padding: const EdgeInsets.all(16),
            child: Column(
              children: [
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Text('FECHA ${p['numero_fecha']}', style: const TextStyle(fontSize: 10, fontWeight: FontWeight.w900, color: AppColors.primary)),
                    Text(p['fecha'] ?? '', style: const TextStyle(fontSize: 10, color: Colors.grey, fontWeight: FontWeight.bold)),
                  ],
                ),
                const SizedBox(height: 20),
                Row(
                  children: [
                    Expanded(child: _teamInfo(p['equipo_local'], p['escudo_local'], true)),
                    Container(
                      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                      margin: const EdgeInsets.symmetric(horizontal: 12),
                      decoration: BoxDecoration(color: AppColors.background, borderRadius: BorderRadius.circular(12)),
                      child: Text('$golesLocal - $golesVisita', 
                        style: GoogleFonts.montserrat(fontWeight: FontWeight.w900, fontSize: 20, color: AppColors.textPrimary)),
                    ),
                    Expanded(child: _teamInfo(p['equipo_visita'], p['escudo_visita'], false)),
                  ],
                ),
              ],
            ),
          ),
        );
      },
    );
  }

  Widget _teamInfo(String name, String? escudo, bool isLocal) {
    return InkWell(
      onTap: () => _openMaps(name),
      child: Column(
        children: [
          Container(
            width: 55, height: 55,
            padding: const EdgeInsets.all(8),
            decoration: BoxDecoration(
              color: Colors.white, 
              shape: BoxShape.circle,
              border: Border.all(color: Colors.grey.shade200, width: 1),
            ),
            child: (escudo != null && escudo.isNotEmpty)
                ? Image.network(escudo, fit: BoxFit.contain, errorBuilder: (_,__,___) => const Text('🏑', style: TextStyle(fontSize: 24)))
                : const Text('🏑', style: TextStyle(fontSize: 24)),
          ),
          const SizedBox(height: 10),
          Text(name.toUpperCase(), textAlign: TextAlign.center, 
            style: GoogleFonts.montserrat(fontSize: 11, fontWeight: FontWeight.w900, color: Colors.black, height: 1.2), 
            maxLines: 2, overflow: TextOverflow.ellipsis),
        ],
      ),
    );
  }

  Widget _buildGoleadores() {
    if (_goleadores.isEmpty) return _noData('No hay datos de goleadores disponibles.');
    return ListView.builder(
      padding: const EdgeInsets.all(16),
      itemCount: _goleadores.length,
      itemBuilder: (context, i) {
        final g = _goleadores[i];
        return Container(
          margin: const EdgeInsets.only(bottom: 12),
          decoration: BoxDecoration(
            color: Colors.white,
            borderRadius: BorderRadius.circular(20),
            boxShadow: [BoxShadow(color: Colors.black.withOpacity(0.05), blurRadius: 10)],
          ),
          child: ListTile(
            contentPadding: const EdgeInsets.symmetric(horizontal: 20, vertical: 10),
            leading: Stack(
              children: [
                ClipOval(
                  child: g['foto_url'] != null && g['foto_url'].toString().isNotEmpty
                    ? Image.network(g['foto_url'], width: 50, height: 50, fit: BoxFit.cover, errorBuilder: (_,__,___) => _defaultAvatar(g))
                    : _defaultAvatar(g),
                ),
                Positioned(
                  right: 0, bottom: 0,
                  child: Container(
                    padding: const EdgeInsets.all(2),
                    decoration: const BoxDecoration(color: Colors.white, shape: BoxShape.circle),
                    child: const Text('🏑', style: TextStyle(fontSize: 14)),
                  ),
                ),
              ],
            ),
            title: Text(g['jugador_nombre']?.toString().toUpperCase() ?? 'JUGADOR', 
              style: GoogleFonts.montserrat(fontWeight: FontWeight.w900, fontSize: 14, color: Colors.black)),
            subtitle: Text(g['club_nombre'] ?? 'Club', style: const TextStyle(fontSize: 11, color: Colors.grey, fontWeight: FontWeight.bold)),
            trailing: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Text('${g['goles']}', style: GoogleFonts.montserrat(fontWeight: FontWeight.w900, fontSize: 24, color: AppColors.primary)),
                const Text('GOLES', style: TextStyle(fontSize: 8, fontWeight: FontWeight.w900, color: AppColors.secondary)),
              ],
            ),
          ),
        );
      },
    );
  }

  Widget _defaultAvatar(dynamic g) {
    String initial = '?';
    if (g['jugador_nombre'] != null && g['jugador_nombre'].toString().isNotEmpty) {
      initial = g['jugador_nombre'][0].toUpperCase();
    }
    return Container(
      width: 50, height: 50,
      color: AppColors.primary.withOpacity(0.1),
      child: Center(child: Text(initial, style: GoogleFonts.montserrat(fontWeight: FontWeight.w900, color: AppColors.primary, fontSize: 22))),
    );
  }

  Widget _noData(String msg) {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          const Text('🏑', style: TextStyle(fontSize: 40)),
          const SizedBox(height: 16),
          Text(msg, style: const TextStyle(color: Colors.grey, fontWeight: FontWeight.bold)),
          const SizedBox(height: 20),
          ElevatedButton(
            onPressed: _loadData,
            style: ElevatedButton.styleFrom(backgroundColor: AppColors.primary, foregroundColor: Colors.white),
            child: const Text('REINTENTAR CARGA'),
          ),
        ],
      ),
    );
  }
}
