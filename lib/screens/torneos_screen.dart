import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import '../utils/colors.dart';
import '../models/ahba_models.dart';
import '../services/data_service.dart';
import 'torneo_detalle_screen.dart';

class TorneosScreen extends StatefulWidget {
  const TorneosScreen({super.key});

  @override
  State<TorneosScreen> createState() => _TorneosScreenState();
}

class _TorneosScreenState extends State<TorneosScreen> {
  String _busqueda = '';
  String _filtroRama = 'Todas'; // 'Todas', 'F', 'M'
  String _filtroCategoria = 'Todas';
  
  List<TorneoResumen> _allTorneos = [];
  List<TorneoResumen> _filteredTorneos = [];
  bool _loading = true;

  @override
  void initState() {
    super.initState();
    _loadTorneos();
  }

  Future<void> _loadTorneos() async {
    setState(() => _loading = true);
    await DataService.instance.init();
    final list = await DataService.instance.getTorneosResumen();
    if (!mounted) return;
    setState(() {
      // Filtrar para que solo aparezcan torneos de 2026 y no repetidos
      final seenNames = <String>{};
      _allTorneos = list.where((t) {
        if (t.temporada != '2026') return false;
        final key = '${t.nombre}-${t.rama}-${t.categoria}-${t.division}';
        if (seenNames.contains(key)) return false;
        seenNames.add(key);
        return true;
      }).toList();
      
      _loading = false;
      _applyFilters();
    });
  }

  void _applyFilters() {
    setState(() {
      _filteredTorneos = _allTorneos.where((t) {
        if (_filtroRama != 'Todas' && t.rama != _filtroRama) return false;
        if (_filtroCategoria != 'Todas' &&
            !t.categoria.toLowerCase().contains(_filtroCategoria.toLowerCase())) {
          return false;
        }
        if (_busqueda.isNotEmpty) {
          final q = _busqueda.toLowerCase();
          final matchNombre = t.nombre.toLowerCase().contains(q);
          final matchCat = t.categoria.toLowerCase().contains(q);
          final matchDiv = t.division.toLowerCase().contains(q);
          if (!matchNombre && !matchCat && !matchDiv) return false;
        }
        return true;
      }).toList();
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColors.background,
      body: Column(
        children: [
          // ========== HEADER ==========
          Container(
            padding: const EdgeInsets.fromLTRB(20, 48, 20, 20),
            decoration: const BoxDecoration(
              gradient: LinearGradient(
                colors: [AppColors.primary, Color(0xFF0F2B48)],
                begin: Alignment.topLeft,
                end: Alignment.bottomRight,
              ),
              borderRadius: BorderRadius.only(
                bottomLeft: Radius.circular(24),
                bottomRight: Radius.circular(24),
              ),
            ),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Row(
                          children: [
                            const Icon(Icons.emoji_events, color: AppColors.secondary, size: 28),
                            const SizedBox(width: 8),
                            Text(
                              'TABLA DE POSICIONES',
                              style: GoogleFonts.montserrat(
                                fontSize: 18,
                                fontWeight: FontWeight.w900,
                                color: Colors.white,
                                letterSpacing: -0.5
                              ),
                            ),
                          ],
                        ),
                        const SizedBox(height: 4),
                        const Text(
                          'Temporada Oficial 2026',
                          style: TextStyle(
                            fontSize: 12,
                            color: Colors.white70,
                            fontWeight: FontWeight.w500
                          ),
                        ),
                      ],
                    ),
                    IconButton(
                      icon: const Icon(Icons.refresh, color: Colors.white),
                      onPressed: _loadTorneos,
                    ),
                  ],
                ),
                const SizedBox(height: 16),
                
                // BARRA DE BÚSQUEDA
                TextField(
                  onChanged: (val) {
                    _busqueda = val;
                    _applyFilters();
                  },
                  style: const TextStyle(color: Colors.white),
                  decoration: InputDecoration(
                    hintText: 'Buscar torneo o división...',
                    hintStyle: const TextStyle(color: Colors.white54, fontSize: 13),
                    prefixIcon: const Icon(Icons.search, color: Colors.white70, size: 20),
                    filled: true,
                    fillColor: Colors.white.withOpacity(0.12),
                    contentPadding: const EdgeInsets.symmetric(vertical: 0),
                    border: OutlineInputBorder(
                      borderRadius: BorderRadius.circular(12),
                      borderSide: BorderSide.none,
                    ),
                  ),
                ),
              ],
            ),
          ),

          // ========== FILTROS ==========
          Container(
            padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Row(
                  children: [
                    _buildRamaChip('Damas', 'F'),
                    const SizedBox(width: 8),
                    _buildRamaChip('Caballeros', 'M'),
                  ],
                ),
                const SizedBox(height: 8),

                SingleChildScrollView(
                  scrollDirection: Axis.horizontal,
                  child: Row(
                    children: [
                      'Todas',
                      'Primera',
                      'Intermedia',
                      'Segunda',
                      'Cuarta',
                      'Quinta',
                      'Sexta',
                      'Septima',
                      'Octava',
                      'Novena',
                      '10ma',
                    ].map((cat) {
                      final isSelected = _filtroCategoria == cat;
                      return Padding(
                        padding: const EdgeInsets.only(right: 6),
                        child: FilterChip(
                          label: Text(cat, style: const TextStyle(fontSize: 11)),
                          selected: isSelected,
                          onSelected: (selected) {
                            setState(() {
                              _filtroCategoria = cat;
                              _applyFilters();
                            });
                          },
                          selectedColor: AppColors.primary,
                          backgroundColor: Colors.white,
                          labelStyle: TextStyle(
                            color: isSelected ? Colors.white : AppColors.textPrimary,
                            fontWeight: isSelected ? FontWeight.bold : FontWeight.normal,
                          ),
                          shape: RoundedRectangleBorder(
                            borderRadius: BorderRadius.circular(20),
                            side: BorderSide(
                              color: isSelected ? AppColors.primary : Colors.grey.shade300,
                            ),
                          ),
                        ),
                      );
                    }).toList(),
                  ),
                ),
              ],
            ),
          ),

          // ========== LISTA DE TORNEOS ==========
          Expanded(
            child: _loading
                ? const Center(child: CircularProgressIndicator(color: AppColors.primary))
                : _filteredTorneos.isEmpty
                    ? const Center(child: Text('No hay torneos registrados para esta selección.'))
                    : ListView.builder(
                        padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                        itemCount: _filteredTorneos.length,
                        itemBuilder: (context, index) {
                          return _buildTorneoCard(_filteredTorneos[index]);
                        },
                      ),
          ),
        ],
      ),
    );
  }

  Widget _buildRamaChip(String label, String value) {
    final isSelected = _filtroRama == value;
    return ChoiceChip(
      label: Text(label),
      selected: isSelected,
      onSelected: (selected) {
        setState(() {
          _filtroRama = selected ? value : 'Todas';
          _applyFilters();
        });
      },
      selectedColor: AppColors.secondary,
      backgroundColor: Colors.white,
      labelStyle: TextStyle(
        color: isSelected ? Colors.white : AppColors.textPrimary,
        fontWeight: FontWeight.bold,
        fontSize: 12,
      ),
    );
  }

  Widget _buildTorneoCard(TorneoResumen torneo) {
    return Card(
      margin: const EdgeInsets.only(bottom: 12),
      elevation: 0,
      color: Colors.white,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(16),
        side: BorderSide(color: Colors.grey.shade100)
      ),
      child: InkWell(
        onTap: () {
          Navigator.push(
            context,
            MaterialPageRoute(
              builder: (context) => TorneoDetalleScreen(
                torneoResumen: torneo,
                mode: TorneoDetalleMode.posiciones,
              ),
            ),
          );
        },
        borderRadius: BorderRadius.circular(16),
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Row(
            children: [
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      torneo.nombre,
                      style: GoogleFonts.montserrat(
                        fontSize: 15,
                        fontWeight: FontWeight.bold,
                        color: AppColors.textPrimary,
                      ),
                    ),
                    const SizedBox(height: 6),
                    Row(
                      children: [
                        Container(
                          padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 3),
                          decoration: BoxDecoration(
                            color: AppColors.primary.withOpacity(0.08),
                            borderRadius: BorderRadius.circular(6),
                          ),
                          child: Text(
                            torneo.categoria,
                            style: const TextStyle(
                              fontSize: 10,
                              fontWeight: FontWeight.w800,
                              color: AppColors.primary,
                            ),
                          ),
                        ),
                        if (torneo.division.isNotEmpty) ...[
                          const SizedBox(width: 8),
                          Text(
                            '•  División ${torneo.division}',
                            style: const TextStyle(
                              fontSize: 11,
                              color: Colors.grey,
                              fontWeight: FontWeight.w500
                            ),
                          ),
                        ],
                      ],
                    ),
                  ],
                ),
              ),
              const Icon(Icons.arrow_forward_ios_rounded, color: Colors.grey, size: 16),
            ],
          ),
        ),
      ),
    );
  }
}
