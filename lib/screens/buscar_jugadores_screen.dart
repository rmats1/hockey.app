import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import '../utils/colors.dart';
import '../models/jugador_model.dart';
import '../data/jugadores_data.dart';

class BuscarJugadoresScreen extends StatefulWidget {
  const BuscarJugadoresScreen({super.key});

  @override
  State<BuscarJugadoresScreen> createState() => _BuscarJugadoresScreenState();
}

class _BuscarJugadoresScreenState extends State<BuscarJugadoresScreen> {
  final _searchController = TextEditingController();
  String _filtroCategoria = 'Todos';
  List<JugadorModel> _resultados = [];

  @override
  void initState() {
    super.initState();
    _resultados = JugadoresData.jugadores;
  }

  void _aplicarFiltros() {
    setState(() {
      _resultados = JugadoresData.buscarPorNombre(_searchController.text);
      if (_filtroCategoria != 'Todos') {
        _resultados = _resultados.where((j) => j.categoria == _filtroCategoria).toList();
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColors.background,
      appBar: AppBar(
        title: Text('Buscador', style: GoogleFonts.montserrat(fontWeight: FontWeight.bold)),
        backgroundColor: AppColors.primary,
        foregroundColor: Colors.white,
        elevation: 0,
      ),
      body: Column(
        children: [
          // Header Buscador
          Container(
            padding: const EdgeInsets.fromLTRB(20, 10, 20, 20),
            decoration: const BoxDecoration(
              color: AppColors.primary,
              borderRadius: BorderRadius.only(
                bottomLeft: Radius.circular(24),
                bottomRight: Radius.circular(24),
              ),
            ),
            child: Column(
              children: [
                TextField(
                  controller: _searchController,
                  onChanged: (_) => _aplicarFiltros(),
                  style: const TextStyle(color: Colors.white),
                  decoration: InputDecoration(
                    hintText: 'Buscar por nombre o club...',
                    hintStyle: const TextStyle(color: Colors.white54, fontSize: 14),
                    prefixIcon: const Icon(Icons.search, color: Colors.white70),
                    filled: true,
                    fillColor: Colors.white.withOpacity(0.1),
                    border: OutlineInputBorder(
                      borderRadius: BorderRadius.circular(15),
                      borderSide: BorderSide.none,
                    ),
                    contentPadding: const EdgeInsets.symmetric(vertical: 10),
                  ),
                ),
                const SizedBox(height: 16),
                Row(
                  children: [
                    _buildTabFiltro('TODOS', 'Todos'),
                    const SizedBox(width: 8),
                    _buildTabFiltro('DAMAS', 'Damas'),
                    const SizedBox(width: 8),
                    _buildTabFiltro('CABALLEROS', 'Caballeros'),
                  ],
                ),
              ],
            ),
          ),
          
          // Lista de Resultados
          Expanded(
            child: _resultados.isEmpty
                ? _buildEmptyState()
                : ListView.builder(
                    padding: const EdgeInsets.all(20),
                    itemCount: _resultados.length,
                    itemBuilder: (context, index) {
                      return _buildJugadorCard(_resultados[index]);
                    },
                  ),
          ),
        ],
      ),
    );
  }

  Widget _buildTabFiltro(String label, String value) {
    final isSelected = _filtroCategoria == value;
    return Expanded(
      child: GestureDetector(
        onTap: () {
          setState(() => _filtroCategoria = value);
          _aplicarFiltros();
        },
        child: AnimatedContainer(
          duration: const Duration(milliseconds: 200),
          padding: const EdgeInsets.symmetric(vertical: 10),
          decoration: BoxDecoration(
            color: isSelected ? Colors.white : Colors.white.withOpacity(0.05),
            borderRadius: BorderRadius.circular(12),
          ),
          child: Center(
            child: Text(
              label,
              style: GoogleFonts.montserrat(
                color: isSelected ? AppColors.primary : Colors.white70,
                fontWeight: FontWeight.bold,
                fontSize: 10,
                letterSpacing: 1,
              ),
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildEmptyState() {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Icon(Icons.person_search_rounded, size: 80, color: Colors.grey.shade300),
          const SizedBox(height: 16),
          Text(
            'No encontramos jugadores',
            style: GoogleFonts.montserrat(
              fontSize: 16, 
              fontWeight: FontWeight.bold, 
              color: AppColors.textSecondary
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildJugadorCard(JugadorModel j) {
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
        contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
        leading: CircleAvatar(
          radius: 25,
          backgroundColor: j.categoria == 'Damas' ? const Color(0xFFE91E63).withOpacity(0.1) : const Color(0xFF1976D2).withOpacity(0.1),
          child: Text(
            j.nombre[0],
            style: TextStyle(
              color: j.categoria == 'Damas' ? const Color(0xFFE91E63) : const Color(0xFF1976D2),
              fontWeight: FontWeight.bold,
              fontSize: 20,
            ),
          ),
        ),
        title: Text(
          j.nombre, 
          style: GoogleFonts.montserrat(fontWeight: FontWeight.bold, fontSize: 15)
        ),
        subtitle: Padding(
          padding: const EdgeInsets.only(top: 4),
          child: Row(
            children: [
              const Icon(Icons.sports_hockey, size: 12, color: Colors.grey),
              const SizedBox(width: 4),
              Expanded(
                child: Text(
                  '${j.club.nombreCorto} • #${j.numeroCamiseta}', 
                  style: const TextStyle(fontSize: 12),
                  maxLines: 1,
                  overflow: TextOverflow.ellipsis,
                ),
              ),
            ],
          ),
        ),
        trailing: Container(
          padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
          decoration: BoxDecoration(
            color: AppColors.secondary.withOpacity(0.1),
            borderRadius: BorderRadius.circular(20),
          ),
          child: Row(
            mainAxisSize: MainAxisSize.min,
            children: [
              const Icon(Icons.star, color: AppColors.secondary, size: 14),
              const SizedBox(width: 4),
              Text(
                '${j.goles}',
                style: const TextStyle(color: AppColors.secondary, fontWeight: FontWeight.bold, fontSize: 13),
              ),
            ],
          ),
        ),
        onTap: () => _mostrarDetalle(j),
      ),
    );
  }

  void _mostrarDetalle(JugadorModel j) {
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      backgroundColor: Colors.transparent,
      builder: (context) => Container(
        height: MediaQuery.of(context).size.height * 0.7,
        decoration: const BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.vertical(top: Radius.circular(32)),
        ),
        child: Column(
          children: [
            const SizedBox(height: 12),
            Container(width: 40, height: 4, decoration: BoxDecoration(color: Colors.grey.shade300, borderRadius: BorderRadius.circular(2))),
            const SizedBox(height: 30),
            
            CircleAvatar(
              radius: 40,
              backgroundColor: AppColors.primary.withOpacity(0.1),
              child: Text(j.nombre[0], style: const TextStyle(fontSize: 32, fontWeight: FontWeight.bold, color: AppColors.primary)),
            ),
            const SizedBox(height: 16),
            Text(j.nombre, style: GoogleFonts.montserrat(fontSize: 22, fontWeight: FontWeight.bold)),
            Text('${j.club.nombre} - #${j.numeroCamiseta}', style: const TextStyle(color: Colors.grey, fontSize: 14)),
            
            const SizedBox(height: 30),
            
            Expanded(
              child: ListView(
                padding: const EdgeInsets.symmetric(horizontal: 30),
                children: [
                  _buildDetailRow(Icons.flag, 'Categoría', j.categoria),
                  _buildDetailRow(Icons.military_tech, 'División', j.division),
                  _buildDetailRow(Icons.sports, 'Posición', j.posicion),
                  _buildDetailRow(Icons.sports_score, 'Goles Totales', j.goles.toString()),
                  _buildDetailRow(Icons.calendar_month, 'Edad', '${_calcularEdad(j.fechaNacimiento)} años'),
                  const SizedBox(height: 20),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildDetailRow(IconData icon, String label, String value) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 20),
      child: Row(
        children: [
          Container(
            padding: const EdgeInsets.all(10),
            decoration: BoxDecoration(color: Colors.grey.shade50, borderRadius: BorderRadius.circular(12)),
            child: Icon(icon, color: AppColors.primary, size: 20),
          ),
          const SizedBox(width: 16),
          Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(label, style: const TextStyle(fontSize: 11, color: Colors.grey, fontWeight: FontWeight.w600)),
              Text(value, style: const TextStyle(fontSize: 15, fontWeight: FontWeight.bold)),
            ],
          ),
        ],
      ),
    );
  }

  int _calcularEdad(DateTime fecha) {
    final hoy = DateTime.now();
    int edad = hoy.year - fecha.year;
    if (hoy.month < fecha.month || (hoy.month == fecha.month && hoy.day < fecha.day)) {
      edad--;
    }
    return edad;
  }
}
