import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:share_plus/share_plus.dart';
import '../utils/colors.dart';
import '../models/novedad_model.dart';

class NovedadDetalleScreen extends StatelessWidget {
  final NovedadModel novedad;

  const NovedadDetalleScreen({super.key, required this.novedad});

  void _compartirNovedad(BuildContext context) {
    final String text = '¡Mirá esta noticia de Hockey AHBA!\n\n${novedad.titulo}\n\n${novedad.resumen}\n\nLeé más en la App.';
    Share.share(text);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Theme.of(context).brightness == Brightness.light ? AppColors.background : null,
      appBar: AppBar(
        title: Text('NOTICIA', style: GoogleFonts.montserrat(fontWeight: FontWeight.w900, fontSize: 14, letterSpacing: 2)),
        backgroundColor: AppColors.primary,
        foregroundColor: Colors.white,
        elevation: 0,
        actions: [
          IconButton(
            icon: const Icon(Icons.share_rounded),
            onPressed: () => _compartirNovedad(context),
          ),
        ],
      ),
      body: SingleChildScrollView(
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Imagen destacada
            Container(
              width: double.infinity,
              height: 250,
              decoration: BoxDecoration(
                gradient: LinearGradient(
                  begin: Alignment.topLeft,
                  end: Alignment.bottomRight,
                  colors: [AppColors.primary, AppColors.primary.withOpacity(0.8)],
                ),
                borderRadius: const BorderRadius.vertical(bottom: Radius.circular(32)),
              ),
              child: const Center(
                child: Icon(Icons.article_rounded, color: Colors.white24, size: 120),
              ),
            ),

            // Contenido
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 30),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Row(
                    children: [
                      _buildCategoriaChip(novedad.categoria),
                      const SizedBox(width: 12),
                      if (novedad.destacada)
                        const Icon(Icons.stars_rounded, color: AppColors.secondary, size: 20),
                      const Spacer(),
                      Text(
                        _formatearFechaCompleta(novedad.fechaPublicacion),
                        style: const TextStyle(fontSize: 11, color: Colors.grey, fontWeight: FontWeight.bold),
                      ),
                    ],
                  ),
                  const SizedBox(height: 24),

                  Text(
                    novedad.titulo,
                    style: GoogleFonts.montserrat(
                      fontSize: 26,
                      fontWeight: FontWeight.w900,
                      color: Theme.of(context).brightness == Brightness.light ? AppColors.textPrimary : Colors.white,
                      height: 1.1,
                    ),
                  ),
                  const SizedBox(height: 24),

                  Row(
                    children: [
                      CircleAvatar(
                        radius: 20,
                        backgroundColor: AppColors.primary.withOpacity(0.1),
                        child: const Icon(Icons.person_rounded, color: AppColors.primary),
                      ),
                      const SizedBox(width: 12),
                      Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(
                            novedad.autor.toUpperCase(),
                            style: GoogleFonts.montserrat(fontSize: 12, fontWeight: FontWeight.w800, color: AppColors.primary),
                          ),
                          const Text('REDACCIÓN AHBA', style: TextStyle(fontSize: 10, color: Colors.grey, fontWeight: FontWeight.bold)),
                        ],
                      ),
                    ],
                  ),
                  
                  const SizedBox(height: 32),
                  const Divider(),
                  const SizedBox(height: 32),

                  // Resumen destacado
                  Container(
                    padding: const EdgeInsets.all(24),
                    decoration: BoxDecoration(
                      color: AppColors.primary.withOpacity(0.05),
                      borderRadius: BorderRadius.circular(24),
                      border: Border.all(color: AppColors.primary.withOpacity(0.1)),
                    ),
                    child: Text(
                      novedad.resumen,
                      style: GoogleFonts.montserrat(
                        fontSize: 15,
                        fontWeight: FontWeight.w600,
                        color: AppColors.primary,
                        fontStyle: FontStyle.italic,
                        height: 1.5,
                      ),
                    ),
                  ),
                  
                  const SizedBox(height: 32),

                  Text(
                    novedad.contenido,
                    style: const TextStyle(
                      fontSize: 16,
                      height: 1.8,
                      letterSpacing: 0.2,
                    ),
                  ),
                  
                  const SizedBox(height: 60),
                  
                  SizedBox(
                    width: double.infinity,
                    height: 55,
                    child: ElevatedButton.icon(
                      onPressed: () => _compartirNovedad(context),
                      icon: const Icon(Icons.share_rounded),
                      label: Text('COMPARTIR NOTICIA', style: GoogleFonts.montserrat(fontWeight: FontWeight.bold, letterSpacing: 1)),
                      style: ElevatedButton.styleFrom(
                        backgroundColor: AppColors.primary,
                        foregroundColor: Colors.white,
                        elevation: 0,
                      ),
                    ),
                  ),
                  const SizedBox(height: 40),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildCategoriaChip(String categoria) {
    Color color;
    switch (categoria) {
      case 'torneo': color = AppColors.primary; break;
      case 'club': color = AppColors.info; break;
      case 'seleccion': color = AppColors.secondary; break;
      default: color = Colors.grey;
    }

    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 6),
      decoration: BoxDecoration(
        color: color.withOpacity(0.1),
        borderRadius: BorderRadius.circular(10),
      ),
      child: Text(
        categoria.toUpperCase(),
        style: TextStyle(color: color, fontSize: 9, fontWeight: FontWeight.w900, letterSpacing: 1),
      ),
    );
  }

  String _formatearFechaCompleta(DateTime fecha) {
    const meses = ['Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio', 'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'];
    return '${fecha.day} ${meses[fecha.month - 1].toUpperCase()} ${fecha.year}';
  }
}
