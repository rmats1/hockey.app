import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import '../utils/colors.dart';
import '../data/clubes_ahba.dart';
import '../models/club_model.dart';
import '../services/favoritos_service.dart';
import '../services/auth_service.dart';

class ClubesFavoritosScreen extends StatefulWidget {
  const ClubesFavoritosScreen({super.key});

  @override
  State<ClubesFavoritosScreen> createState() => _ClubesFavoritosScreenState();
}

class _ClubesFavoritosScreenState extends State<ClubesFavoritosScreen> {
  final _favoritosService = FavoritosService();
  final _authService = AuthService();
  
  List<String> _favoritosIds = [];
  String _busqueda = '';
  String? _userId;
  bool _isLoading = true;

  @override
  void initState() {
    super.initState();
    _loadData();
  }

  Future<void> _loadData() async {
    final user = await _authService.getCurrentUser();
    if (user != null) {
      _userId = user.id;
      final favs = await _favoritosService.getFavoritos(user.id);
      setState(() {
        _favoritosIds = favs;
        _isLoading = false;
      });
    }
  }

  void _toggleFavorito(String clubId) async {
    if (_userId == null) return;

    if (_favoritosIds.contains(clubId)) {
      await _favoritosService.removeFavorito(_userId!, clubId);
      setState(() => _favoritosIds.remove(clubId));
    } else {
      await _favoritosService.addFavorito(_userId!, clubId);
      setState(() => _favoritosIds.add(clubId));
    }
  }

  @override
  Widget build(BuildContext context) {
    final todosLosClubes = ClubesAhba.clubes;
    final clubesFiltrados = todosLosClubes.where((c) {
      final matchBusqueda = c.nombre.toLowerCase().contains(_busqueda.toLowerCase()) || 
                          c.nombreCorto.toLowerCase().contains(_busqueda.toLowerCase());
      return matchBusqueda;
    }).toList();

    // Ordenar: favoritos primero, luego alfabéticamente
    clubesFiltrados.sort((a, b) {
      final isFavA = _favoritosIds.contains(a.id);
      final isFavB = _favoritosIds.contains(b.id);
      if (isFavA && !isFavB) return -1;
      if (!isFavA && isFavB) return 1;
      return a.nombre.compareTo(b.nombre);
    });

    return Scaffold(
      backgroundColor: AppColors.background,
      appBar: AppBar(
        title: Text('Mis Clubes', style: GoogleFonts.montserrat(fontWeight: FontWeight.bold)),
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
            child: Column(
              children: [
                const Row(
                  children: [
                    Icon(Icons.favorite, color: AppColors.secondary, size: 20),
                    SizedBox(width: 10),
                    Expanded(
                      child: Text(
                        'Marcá tus clubes favoritos para tener acceso rápido a sus estadísticas y fixture.',
                        style: TextStyle(color: Colors.white70, fontSize: 12),
                      ),
                    ),
                  ],
                ),
                const SizedBox(height: 20),
                // Buscador
                TextField(
                  onChanged: (v) => setState(() => _busqueda = v),
                  style: const TextStyle(color: Colors.white),
                  decoration: InputDecoration(
                    hintText: 'Buscar club...',
                    hintStyle: const TextStyle(color: Colors.white54),
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
              ],
            ),
          ),

          Expanded(
            child: _isLoading 
              ? const Center(child: CircularProgressIndicator(color: AppColors.primary))
              : clubesFiltrados.isEmpty
                ? _buildEmptyState()
                : ListView.builder(
                    padding: const EdgeInsets.all(20),
                    itemCount: clubesFiltrados.length,
                    itemBuilder: (context, index) {
                      final club = clubesFiltrados[index];
                      final isFav = _favoritosIds.contains(club.id);
                      return _buildClubCard(club, isFav);
                    },
                  ),
          ),
        ],
      ),
    );
  }

  Widget _buildEmptyState() {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Icon(Icons.search_off_rounded, size: 80, color: Colors.grey.shade300),
          const SizedBox(height: 16),
          Text(
            'No encontramos ese club',
            style: GoogleFonts.montserrat(
              fontSize: 18, 
              fontWeight: FontWeight.bold, 
              color: AppColors.textSecondary
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildClubCard(Club club, bool isFav) {
    return Container(
      margin: const EdgeInsets.only(bottom: 12),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(16),
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(0.03),
            blurRadius: 10,
            offset: const Offset(0, 4),
          ),
        ],
      ),
      child: ListTile(
        contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
        leading: Container(
          width: 50,
          height: 50,
          decoration: BoxDecoration(
            color: isFav ? AppColors.primary.withOpacity(0.1) : Colors.grey.shade50,
            borderRadius: BorderRadius.circular(12),
          ),
          child: Icon(
            Icons.sports_hockey, 
            color: isFav ? AppColors.primary : Colors.grey.shade400,
            size: 30,
          ),
        ),
        title: Text(
          club.nombreCorto,
          style: GoogleFonts.montserrat(
            fontWeight: FontWeight.bold,
            fontSize: 16,
            color: AppColors.textPrimary,
          ),
        ),
        subtitle: Text(
          club.nombre,
          style: const TextStyle(fontSize: 12, color: AppColors.textSecondary),
          maxLines: 1,
          overflow: TextOverflow.ellipsis,
        ),
        trailing: IconButton(
          icon: Icon(
            isFav ? Icons.favorite_rounded : Icons.favorite_border_rounded,
            color: isFav ? AppColors.error : Colors.grey.shade400,
          ),
          onPressed: () => _toggleFavorito(club.id),
        ),
      ),
    );
  }
}
