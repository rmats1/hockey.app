import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import '../utils/colors.dart';
import '../models/ahba_models.dart';
import '../services/data_service.dart';
import 'torneo_detalle_screen.dart';

class FixtureScreen extends StatefulWidget {
  const FixtureScreen({super.key});

  @override
  State<FixtureScreen> createState() => _FixtureScreenState();
}

class _FixtureScreenState extends State<FixtureScreen> {
  bool _isLoading = true;
  List<TorneoResumen> _allTorneos = [];
  List<TorneoResumen> _filteredTorneos = [];
  String _rama = 'F'; // F o M
  String _filtroCategoria = 'Todas';

  @override
  void initState() {
    super.initState();
    _loadTorneos();
  }

  Future<void> _loadTorneos() async {
    setState(() => _isLoading = true);
    await DataService.instance.init();
    final list = await DataService.instance.getTorneosResumen();
    if (mounted) {
      setState(() {
        final seenNames = <String>{};
        _allTorneos = list.where((t) {
          if (t.temporada != '2026') return false;
          final key = '${t.nombre}-${t.rama}-${t.categoria}-${t.division}';
          if (seenNames.contains(key)) return false;
          seenNames.add(key);
          return true;
        }).toList();
        
        _isLoading = false;
        _applyFilters();
      });
    }
  }

  void _applyFilters() {
    setState(() {
      _filteredTorneos = _allTorneos.where((t) {
        if (t.rama != _rama) return false;
        if (_filtroCategoria != 'Todas' && !t.categoria.toLowerCase().contains(_filtroCategoria.toLowerCase())) {
          return false;
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
            padding: const EdgeInsets.fromLTRB(20, 60, 20, 24),
            decoration: const BoxDecoration(
              color: AppColors.primary,
              borderRadius: BorderRadius.only(bottomLeft: Radius.circular(30), bottomRight: Radius.circular(30)),
            ),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text('FIXTURE Y GOLEADORES', style: GoogleFonts.montserrat(color: AppColors.secondary, fontSize: 10, fontWeight: FontWeight.w900, letterSpacing: 2)),
                const SizedBox(height: 4),
                Text('Temporada 2026', style: GoogleFonts.montserrat(color: Colors.white, fontSize: 24, fontWeight: FontWeight.w900, letterSpacing: -1)),
                const SizedBox(height: 20),
                Row(
                  children: [
                    _tab('DAMAS', 'F'),
                    const SizedBox(width: 12),
                    _tab('CABALLEROS', 'M'),
                  ],
                ),
              ],
            ),
          ),

          // ========== FILTROS (Igual que Tabla de Posiciones) ==========
          Container(
            padding: const EdgeInsets.fromLTRB(16, 12, 0, 12),
            child: _buildCategorySelector(),
          ),

          Expanded(
            child: _isLoading 
              ? const Center(child: CircularProgressIndicator(color: AppColors.primary))
              : _filteredTorneos.isEmpty 
                  ? const Center(child: Text('No hay fixtures para esta selección.'))
                  : ListView.builder(
                      padding: const EdgeInsets.symmetric(horizontal: 16),
                      itemCount: _filteredTorneos.length,
                      itemBuilder: (context, index) {
                        final t = _filteredTorneos[index];
                        return _buildTorneoItem(t);
                      },
                    ),
          ),
        ],
      ),
    );
  }

  Widget _buildCategorySelector() {
    return SingleChildScrollView(
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
            padding: const EdgeInsets.only(right: 8),
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
    );
  }

  Widget _tab(String label, String code) {
    final isSel = _rama == code;
    return Expanded(
      child: GestureDetector(
        onTap: () {
          setState(() {
            _rama = code;
            _applyFilters();
          });
        },
        child: Container(
          padding: const EdgeInsets.symmetric(vertical: 12),
          decoration: BoxDecoration(
            color: isSel ? Colors.white : Colors.white.withOpacity(0.1),
            borderRadius: BorderRadius.circular(15),
          ),
          child: Center(
            child: Text(label, style: GoogleFonts.montserrat(
              color: isSel ? AppColors.primary : Colors.white,
              fontWeight: FontWeight.w900,
              fontSize: 11,
              letterSpacing: 1
            )),
          ),
        ),
      ),
    );
  }

  Widget _buildTorneoItem(TorneoResumen t) {
    return Card(
      margin: const EdgeInsets.only(bottom: 12),
      elevation: 0,
      color: Colors.white,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(16),
        side: BorderSide(color: Colors.grey.shade100)
      ),
      child: ListTile(
        onTap: () {
          Navigator.push(context, MaterialPageRoute(builder: (_) => TorneoDetalleScreen(torneoResumen: t)));
        },
        contentPadding: const EdgeInsets.symmetric(horizontal: 20, vertical: 8),
        title: Text(t.nombre, style: GoogleFonts.montserrat(fontWeight: FontWeight.bold, fontSize: 14)),
        subtitle: Text('${t.categoria} • ${t.division}', style: const TextStyle(fontSize: 11, color: Colors.grey, fontWeight: FontWeight.w500)),
        trailing: const Icon(Icons.arrow_forward_ios_rounded, size: 14, color: Colors.grey),
      ),
    );
  }
}
