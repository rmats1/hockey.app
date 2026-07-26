import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:share_plus/share_plus.dart';
import 'package:font_awesome_flutter/font_awesome_flutter.dart';
import '../utils/colors.dart';

class CompartirScreen extends StatelessWidget {
  const CompartirScreen({super.key});

  void _shareApp(BuildContext context, String platform) {
    const String text = '¡Descargá la App de Hockey AHBA! Seguí los torneos, estadísticas y a tu club favorito. 🏑\n\nhttps://hockeyahba.com.ar/descargar';
    
    Share.share(text);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColors.background,
      appBar: AppBar(
        title: Text('Compartir', style: GoogleFonts.montserrat(fontWeight: FontWeight.bold)),
        backgroundColor: AppColors.primary,
        foregroundColor: Colors.white,
        elevation: 0,
      ),
      body: Column(
        children: [
          // Header
          Container(
            padding: const EdgeInsets.fromLTRB(30, 10, 30, 40),
            decoration: const BoxDecoration(
              color: AppColors.primary,
              borderRadius: BorderRadius.only(
                bottomLeft: Radius.circular(32),
                bottomRight: Radius.circular(32),
              ),
            ),
            child: Column(
              children: [
                Container(
                  padding: const EdgeInsets.all(20),
                  decoration: BoxDecoration(color: Colors.white.withOpacity(0.1), shape: BoxShape.circle),
                  child: const Icon(Icons.share_rounded, color: AppColors.secondary, size: 40),
                ),
                const SizedBox(height: 20),
                Text(
                  '¡SUMÁ A TU EQUIPO!',
                  style: GoogleFonts.montserrat(color: Colors.white, fontSize: 18, fontWeight: FontWeight.w900, letterSpacing: 1),
                ),
                const SizedBox(height: 8),
                const Text(
                  'Compartí la app con tus compañeros de club y amigos para que nadie se pierda nada.',
                  textAlign: TextAlign.center,
                  style: TextStyle(color: Colors.white70, fontSize: 13, height: 1.5),
                ),
              ],
            ),
          ),

          Padding(
            padding: const EdgeInsets.all(30),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  'ELEGÍ UNA PLATAFORMA',
                  style: GoogleFonts.montserrat(fontSize: 11, fontWeight: FontWeight.w800, color: Colors.grey, letterSpacing: 2),
                ),
                const SizedBox(height: 24),
                
                GridView.count(
                  shrinkWrap: true,
                  physics: const NeverScrollableScrollPhysics(),
                  crossAxisCount: 3,
                  mainAxisSpacing: 20,
                  crossAxisSpacing: 20,
                  children: [
                    _buildOption(context, FontAwesomeIcons.whatsapp, 'WhatsApp', Colors.green.shade600),
                    _buildOption(context, FontAwesomeIcons.instagram, 'Instagram', Colors.purple.shade600),
                    _buildOption(context, FontAwesomeIcons.facebook, 'Facebook', Colors.blue.shade800),
                    _buildOption(context, FontAwesomeIcons.xTwitter, 'Twitter', Colors.black87),
                    _buildOption(context, FontAwesomeIcons.telegram, 'Telegram', Colors.blue.shade400),
                    _buildOption(context, Icons.link_rounded, 'Copiar Link', Colors.grey.shade700),
                  ],
                ),
                
                const SizedBox(height: 40),
                
                // Card de recomendación
                Container(
                  padding: const EdgeInsets.all(20),
                  decoration: BoxDecoration(
                    color: AppColors.secondary.withOpacity(0.1),
                    borderRadius: BorderRadius.circular(24),
                    border: Border.all(color: AppColors.secondary.withOpacity(0.2)),
                  ),
                  child: Row(
                    children: [
                      const Icon(Icons.stars_rounded, color: AppColors.secondary, size: 30),
                      const SizedBox(width: 16),
                      Expanded(
                        child: Text(
                          'Cada vez que compartís, ayudás a profesionalizar el hockey amateur.',
                          style: GoogleFonts.montserrat(fontSize: 12, fontWeight: FontWeight.w600, color: AppColors.secondary),
                        ),
                      ),
                    ],
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildOption(BuildContext context, IconData icon, String label, Color color) {
    return InkWell(
      onTap: () => _shareApp(context, label),
      borderRadius: BorderRadius.circular(20),
      child: Column(
        children: [
          Container(
            width: 60, height: 60,
            decoration: BoxDecoration(
              color: Colors.white,
              borderRadius: BorderRadius.circular(20),
              boxShadow: [
                BoxShadow(color: color.withOpacity(0.1), blurRadius: 10, offset: const Offset(0, 4))
              ],
            ),
            child: Icon(icon, color: color, size: 24),
          ),
          const SizedBox(height: 10),
          Text(
            label, 
            style: GoogleFonts.montserrat(fontSize: 10, fontWeight: FontWeight.w700, color: AppColors.textPrimary),
            maxLines: 1,
            overflow: TextOverflow.ellipsis,
          ),
        ],
      ),
    );
  }
}
