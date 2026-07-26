import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import '../utils/colors.dart';

class AyudaScreen extends StatelessWidget {
  const AyudaScreen({super.key});

  final List<_Pregunta> _preguntas = const [
    _Pregunta(
      '¿Cómo me registro?',
      'Tocá "Registrate" en la pantalla de login, completá tus datos y elegí tu club. Necesitás un email válido y una contraseña de al menos 6 caracteres.',
    ),
    _Pregunta(
      '¿Cómo inicio con huella?',
      'Primero registrate con email. Después, cada vez que quieras entrar, podés usar tu huella dactilar si tu dispositivo lo soporta.',
    ),
    _Pregunta(
      '¿Puedo cambiar de club?',
      'Por ahora no. Una vez que elegiste tu club al registrarte, queda fijo. En futuras versiones se podrá editar.',
    ),
    _Pregunta(
      '¿Los datos son reales?',
      'Los datos de torneos, partidos, posiciones y goleadores son de ejemplo para demostración. En producción se conectarían a la base de datos oficial de la AHBA.',
    ),
    _Pregunta(
      '¿Cómo funcionan los filtros?',
      'En cada pantalla hay chips arriba que te permiten filtrar por categoría, división, estado, etc. Tocá el que quieras y la lista se actualiza.',
    ),
    _Pregunta(
      '¿Qué pasa si no aparece mi club?',
      'Tenemos cargados los 140 clubes principales de la AHBA. Si el tuyo no aparece, contactanos para agregarlo.',
    ),
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColors.background,
      appBar: AppBar(
        title: Text('Soporte', style: GoogleFonts.montserrat(fontWeight: FontWeight.bold)),
        backgroundColor: AppColors.primary,
        foregroundColor: Colors.white,
        elevation: 0,
      ),
      body: SingleChildScrollView(
        child: Column(
          children: [
            // Header
            Container(
              padding: const EdgeInsets.fromLTRB(20, 10, 20, 24),
              decoration: const BoxDecoration(
                color: AppColors.primary,
                borderRadius: BorderRadius.only(
                  bottomLeft: Radius.circular(24),
                  bottomRight: Radius.circular(24),
                ),
              ),
              child: Row(
                children: [
                  Container(
                    padding: const EdgeInsets.all(12),
                    decoration: BoxDecoration(color: Colors.white.withOpacity(0.1), borderRadius: BorderRadius.circular(16)),
                    child: const Icon(Icons.help_center_rounded, color: AppColors.secondary, size: 28),
                  ),
                  const SizedBox(width: 16),
                  const Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text('¿Cómo podemos ayudarte?', style: TextStyle(color: Colors.white, fontSize: 16, fontWeight: FontWeight.bold)),
                        SizedBox(height: 2),
                        Text('Preguntas frecuentes y soporte técnico', style: TextStyle(color: Colors.white70, fontSize: 11)),
                      ],
                    ),
                  ),
                ],
              ),
            ),

            Padding(
              padding: const EdgeInsets.all(20),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  _buildSectionTitle('PREGUNTAS FRECUENTES'),
                  const SizedBox(height: 16),
                  ..._preguntas.asMap().entries.map((entry) => _buildPreguntaCard(entry.key, entry.value)),
                  
                  const SizedBox(height: 30),
                  
                  _buildSectionTitle('CONTACTO DIRECTO'),
                  const SizedBox(height: 16),
                  _buildContactCard(),
                  const SizedBox(height: 30),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildSectionTitle(String title) {
    return Row(
      children: [
        Container(width: 4, height: 16, decoration: BoxDecoration(color: AppColors.secondary, borderRadius: BorderRadius.circular(2))),
        const SizedBox(width: 10),
        Text(title, style: GoogleFonts.montserrat(fontSize: 12, fontWeight: FontWeight.w800, color: AppColors.textPrimary, letterSpacing: 1)),
      ],
    );
  }

  Widget _buildPreguntaCard(int index, _Pregunta p) {
    return Container(
      margin: const EdgeInsets.only(bottom: 12),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(16),
        boxShadow: [
          BoxShadow(color: Colors.black.withOpacity(0.02), blurRadius: 10, offset: const Offset(0, 4))
        ],
      ),
      child: Theme(
        data: ThemeData().copyWith(dividerColor: Colors.transparent),
        child: ExpansionTile(
          iconColor: AppColors.primary,
          collapsedIconColor: Colors.grey,
          leading: CircleAvatar(
            backgroundColor: AppColors.primary.withOpacity(0.1),
            radius: 14,
            child: Text('${index + 1}', style: const TextStyle(color: AppColors.primary, fontSize: 12, fontWeight: FontWeight.bold)),
          ),
          title: Text(p.pregunta, style: GoogleFonts.montserrat(fontWeight: FontWeight.bold, fontSize: 13, color: AppColors.textPrimary)),
          children: [
            Padding(
              padding: const EdgeInsets.fromLTRB(20, 0, 20, 20),
              child: Text(p.respuesta, style: const TextStyle(color: AppColors.textSecondary, fontSize: 13, height: 1.5)),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildContactCard() {
    return Container(
      padding: const EdgeInsets.all(24),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(24),
        boxShadow: [
          BoxShadow(color: Colors.black.withOpacity(0.02), blurRadius: 10, offset: const Offset(0, 4))
        ],
      ),
      child: Column(
        children: [
          const Icon(Icons.alternate_email_rounded, color: AppColors.primary, size: 40),
          const SizedBox(height: 16),
          Text('¿Tenés otra consulta?', style: GoogleFonts.montserrat(fontSize: 16, fontWeight: FontWeight.bold)),
          const SizedBox(height: 8),
          const Text('Escribinos y te responderemos a la brevedad.', textAlign: TextAlign.center, style: TextStyle(color: AppColors.textSecondary, fontSize: 12)),
          const SizedBox(height: 20),
          Container(
            padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 12),
            decoration: BoxDecoration(color: AppColors.primary.withOpacity(0.05), borderRadius: BorderRadius.circular(12)),
            child: const Text('soporte@hockeyahba.com.ar', style: TextStyle(color: AppColors.primary, fontWeight: FontWeight.bold, fontSize: 14)),
          ),
        ],
      ),
    );
  }
}

class _Pregunta {
  final String pregunta;
  final String respuesta;

  const _Pregunta(this.pregunta, this.respuesta);
}
