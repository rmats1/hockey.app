import 'package:flutter/material.dart';
import '../utils/colors.dart';
import '../models/novedad_model.dart';
import '../data/novedades_data.dart';
import 'novedad_detalle_screen.dart';

class NovedadesScreen extends StatefulWidget {
  const NovedadesScreen({super.key});

  @override
  State<NovedadesScreen> createState() => _NovedadesScreenState();
}

class _NovedadesScreenState extends State<NovedadesScreen> {
  String _filtroCategoria = 'Todas';

  @override
  Widget build(BuildContext context) {
    final novedades = _filtroCategoria == 'Todas'
        ? NovedadesData.novedades
        : NovedadesData.filtrarPorCategoria(_filtroCategoria);

    final destacadas = NovedadesData.destacadas;

    return Scaffold(
      backgroundColor: AppColors.background,
      body: Column(
        children: [
          // ========== HEADER ==========
          Container(
            padding: const EdgeInsets.fromLTRB(20, 20, 20, 16),
            decoration: const BoxDecoration(
              color: AppColors.primary,
              borderRadius: BorderRadius.only(
                bottomLeft: Radius.circular(20),
                bottomRight: Radius.circular(20),
              ),
            ),
            child: const Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  'Novedades',
                  style: TextStyle(
                    fontSize: 24,
                    fontWeight: FontWeight.bold,
                    color: Colors.white,
                  ),
                ),
                SizedBox(height: 4),
                Text(
                  'Las últimas noticias del hockey AHBA',
                  style: TextStyle(fontSize: 14, color: Colors.white70),
                ),
              ],
            ),
          ),

          // ========== FILTROS ==========
          Container(
            padding: const EdgeInsets.all(16),
            child: SingleChildScrollView(
              scrollDirection: Axis.horizontal,
              child: Row(
                children: [
                  _buildFilterChip('Todas', 'Todas'),
                  _buildFilterChip('Torneos', 'torneo'),
                  _buildFilterChip('Clubes', 'club'),
                  _buildFilterChip('Selección', 'seleccion'),
                  _buildFilterChip('General', 'general'),
                ],
              ),
            ),
          ),

          // ========== LISTA DE NOVEDADES ==========
          Expanded(
            child: ListView(
              padding: const EdgeInsets.symmetric(horizontal: 16),
              children: [
                // Destacadas
                if (_filtroCategoria == 'Todas' && destacadas.isNotEmpty) ...[
                  const Text(
                    'Destacadas',
                    style: TextStyle(
                      fontSize: 16,
                      fontWeight: FontWeight.bold,
                      color: AppColors.textPrimary,
                    ),
                  ),
                  const SizedBox(height: 12),
                  ...destacadas.map((n) => _buildNovedadCard(n, esDestacada: true)),
                  const SizedBox(height: 16),
                  const Text(
                    'Recientes',
                    style: TextStyle(
                      fontSize: 16,
                      fontWeight: FontWeight.bold,
                      color: AppColors.textPrimary,
                    ),
                  ),
                  const SizedBox(height: 12),
                ],

                // Resto
                ...novedades.map((n) => _buildNovedadCard(n)),
                const SizedBox(height: 20),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildFilterChip(String label, String value) {
    final isSelected = _filtroCategoria == value;
    return Padding(
      padding: const EdgeInsets.only(right: 8),
      child: FilterChip(
        label: Text(label),
        selected: isSelected,
        onSelected: (selected) {
          setState(() => _filtroCategoria = value);
        },
        selectedColor: AppColors.primary,
        backgroundColor: Colors.white,
        labelStyle: TextStyle(
          color: isSelected ? Colors.white : AppColors.textPrimary,
          fontWeight: FontWeight.w600,
        ),
      ),
    );
  }

  Widget _buildNovedadCard(NovedadModel novedad, {bool esDestacada = false}) {
    return Card(
      margin: const EdgeInsets.only(bottom: 12),
      elevation: esDestacada ? 3 : 1,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(16),
        side: esDestacada
            ? const BorderSide(color: AppColors.secondary, width: 1.5)
            : BorderSide.none,
      ),
      child: InkWell(
        onTap: () {
          Navigator.push(
            context,
            MaterialPageRoute(
              builder: (context) => NovedadDetalleScreen(novedad: novedad),
            ),
          );
        },
        borderRadius: BorderRadius.circular(16),
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              // Header con categoría y fecha
              Row(
                children: [
                  _buildCategoriaChip(novedad.categoria),
                  const Spacer(),
                  Text(
                    _formatearFecha(novedad.fechaPublicacion),
                    style: const TextStyle(fontSize: 11, color: AppColors.textSecondary),
                  ),
                ],
              ),
              const SizedBox(height: 10),

              // Imagen placeholder
              if (esDestacada)
                Container(
                  height: 120,
                  margin: const EdgeInsets.only(bottom: 12),
                  decoration: BoxDecoration(
                    borderRadius: BorderRadius.circular(12),
                    gradient: const LinearGradient(
                      begin: Alignment.topLeft,
                      end: Alignment.bottomRight,
                      colors: [
                        AppColors.primary,
                        AppColors.primaryLight,
                      ],
                    ),
                  ),
                  child: const Center(
                    child: Icon(
                      Icons.sports_hockey,
                      color: Colors.white,
                      size: 50,
                    ),
                  ),
                ),

              // Título
              Text(
                novedad.titulo,
                style: const TextStyle(
                  fontSize: 16,
                  fontWeight: FontWeight.bold,
                  color: AppColors.textPrimary,
                ),
              ),
              const SizedBox(height: 6),

              // Resumen
              Text(
                novedad.resumen,
                maxLines: 2,
                overflow: TextOverflow.ellipsis,
                style: const TextStyle(
                  fontSize: 13,
                  color: AppColors.textSecondary,
                ),
              ),
              const SizedBox(height: 10),

              // Footer
              Row(
                children: [
                  const Icon(Icons.person, size: 12, color: AppColors.textSecondary),
                  const SizedBox(width: 4),
                  Text(
                    novedad.autor,
                    style: const TextStyle(fontSize: 11, color: AppColors.textSecondary),
                  ),
                  const Spacer(),
                  if (esDestacada)
                    const Icon(Icons.star, size: 14, color: AppColors.secondary),
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildCategoriaChip(String categoria) {
    IconData icon;
    String label;
    Color color;

    switch (categoria) {
      case 'torneo':
        icon = Icons.emoji_events;
        label = 'TORNEO';
        color = AppColors.primary;
        break;
      case 'club':
        icon = Icons.sports_hockey;
        label = 'CLUB';
        color = AppColors.info;
        break;
      case 'seleccion':
        icon = Icons.flag;
        label = 'SELECCIÓN';
        color = AppColors.secondary;
        break;
      default:
        icon = Icons.article;
        label = 'GENERAL';
        color = AppColors.textSecondary;
    }

    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 3),
      decoration: BoxDecoration(
        color: color.withOpacity(0.1),
        borderRadius: BorderRadius.circular(12),
        border: Border.all(color: color.withOpacity(0.3)),
      ),
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          Icon(icon, size: 10, color: color),
          const SizedBox(width: 4),
          Text(
            label,
            style: TextStyle(
              color: color,
              fontSize: 9,
              fontWeight: FontWeight.bold,
            ),
          ),
        ],
      ),
    );
  }

  String _formatearFecha(DateTime fecha) {
    const meses = ['Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun', 'Jul', 'Ago', 'Sep', 'Oct', 'Nov', 'Dic'];
    return '${fecha.day} ${meses[fecha.month - 1]}';
  }
}
