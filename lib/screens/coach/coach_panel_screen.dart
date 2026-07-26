import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:url_launcher/url_launcher.dart';
import '../../utils/colors.dart';
import '../../models/user_model.dart';
import '../../services/auth_service.dart';
import '../../services/team_service.dart';
import '../../data/jugadores_data.dart';
import 'tactical_board_screen.dart';
import 'physical_training_screen.dart';

class CoachPanelScreen extends StatefulWidget {
  const CoachPanelScreen({super.key});

  @override
  State<CoachPanelScreen> createState() => _CoachPanelScreenState();
}

class _CoachPanelScreenState extends State<CoachPanelScreen> {
  final _authService = AuthService();
  final _teamService = TeamService();
  UserModel? _user;
  final List<String> _selectedPlayers = [];

  @override
  void initState() {
    super.initState();
    _loadUser();
  }

  Future<void> _loadUser() async {
    final user = await _authService.getCurrentUser();
    if (user != null) {
      final savedCallUp = await _teamService.getCallUpList(user.club.id, user.division ?? 'General');
      setState(() {
        _user = user;
        _selectedPlayers.addAll(savedCallUp);
      });
    }
  }

  void _openWhatsApp() async {
    const String whatsappLink = 'https://chat.whatsapp.com/invite/example';
    final Uri url = Uri.parse(whatsappLink);
    if (!await launchUrl(url, mode: LaunchMode.externalApplication)) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('No se pudo abrir el enlace de WhatsApp')),
        );
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    final bool isPrepFisico = _user?.rolCuerpoTecnico == 'Preparador Físico';

    return Scaffold(
      backgroundColor: AppColors.background,
      body: Column(
        children: [
          Container(
            padding: const EdgeInsets.fromLTRB(30, 60, 30, 40),
            decoration: const BoxDecoration(
              color: AppColors.primary,
              borderRadius: BorderRadius.only(bottomLeft: Radius.circular(32), bottomRight: Radius.circular(32)),
              boxShadow: [BoxShadow(color: Colors.black26, blurRadius: 15, offset: Offset(0, 5))],
            ),
            child: Row(
              children: [
                Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text('ESTRATEGIA', style: GoogleFonts.montserrat(color: AppColors.secondary, fontSize: 10, fontWeight: FontWeight.w900, letterSpacing: 2)),
                    const SizedBox(height: 4),
                    Text('PANEL TÉCNICO', style: GoogleFonts.montserrat(color: Colors.white, fontSize: 24, fontWeight: FontWeight.w900, letterSpacing: -1)),
                  ],
                ),
                const Spacer(),
                Container(
                  padding: const EdgeInsets.all(12),
                  decoration: BoxDecoration(color: Colors.white.withOpacity(0.1), shape: BoxShape.circle),
                  child: const Icon(Icons.admin_panel_settings_rounded, color: Colors.white, size: 30),
                ),
              ],
            ),
          ),

          Expanded(
            child: ListView(
              padding: const EdgeInsets.all(24),
              children: [
                _buildActionCard(
                  'PIZARRA TÁCTICA',
                  'Diseñá jugadas y posicionamientos.',
                  Icons.gesture_rounded,
                  AppColors.primary,
                  () => Navigator.push(context, MaterialPageRoute(builder: (_) => const TacticalBoardScreen())),
                ),
                _buildActionCard(
                  'PLANIFICACIÓN FÍSICA',
                  'Enviá indicaciones de trabajo al plantel.',
                  Icons.fitness_center_rounded,
                  isPrepFisico ? AppColors.secondary : AppColors.info,
                  () {
                    if (_user != null) {
                      Navigator.push(context, MaterialPageRoute(builder: (_) => PhysicalTrainingScreen(coach: _user!)));
                    }
                  },
                  tag: isPrepFisico ? 'TU ESPECIALIDAD' : null,
                ),
                _buildActionCard(
                  'GRUPO DE JUGADORES',
                  'Chat directo en WhatsApp con el equipo.',
                  Icons.chat_bubble_rounded,
                  Colors.green.shade600,
                  _openWhatsApp,
                ),
                _buildActionCard(
                  'CONVOCATORIA',
                  'Armá la lista para el próximo partido.',
                  Icons.assignment_ind_rounded,
                  AppColors.secondary,
                  () => _showCallUpSheet(),
                ),
                _buildActionCard(
                  'RED DE DIRECTORES',
                  'Contacto con otros cuerpos técnicos.',
                  Icons.connect_without_contact_rounded,
                  Colors.blue.shade700,
                  () => _showCoachesNetwork(),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildActionCard(String title, String subtitle, IconData icon, Color color, VoidCallback onTap, {String? tag}) {
    return Container(
      margin: const EdgeInsets.only(bottom: 20),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(24),
        boxShadow: [BoxShadow(color: Colors.black.withOpacity(0.03), blurRadius: 10, offset: const Offset(0, 4))],
      ),
      child: Stack(
        children: [
          ListTile(
            onTap: onTap,
            contentPadding: const EdgeInsets.all(20),
            leading: Container(
              width: 55, height: 55,
              decoration: BoxDecoration(color: color.withOpacity(0.1), borderRadius: BorderRadius.circular(16)),
              child: Icon(icon, color: color, size: 28),
            ),
            title: Text(title, style: GoogleFonts.montserrat(fontWeight: FontWeight.w900, fontSize: 13, letterSpacing: 0.5)),
            subtitle: Padding(
              padding: const EdgeInsets.only(top: 4),
              child: Text(subtitle, style: const TextStyle(fontSize: 11, color: Colors.grey, height: 1.3)),
            ),
            trailing: const Icon(Icons.arrow_forward_ios_rounded, color: Colors.grey, size: 14),
          ),
          if (tag != null)
            Positioned(
              top: 10, right: 20,
              child: Container(
                padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
                decoration: BoxDecoration(color: color, borderRadius: BorderRadius.circular(8)),
                child: Text(tag, style: const TextStyle(color: Colors.white, fontSize: 7, fontWeight: FontWeight.bold)),
              ),
            ),
        ],
      ),
    );
  }

  void _showCallUpSheet() {
    if (_user == null) return;
    final players = JugadoresData.getJugadoresPorClub(_user!.club.id);

    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      backgroundColor: Colors.transparent,
      builder: (context) => StatefulBuilder(
        builder: (context, setModalState) => Container(
          height: MediaQuery.of(context).size.height * 0.85,
          decoration: const BoxDecoration(color: Colors.white, borderRadius: BorderRadius.vertical(top: Radius.circular(32))),
          child: Column(
            children: [
              const SizedBox(height: 12),
              Container(width: 40, height: 4, decoration: BoxDecoration(color: Colors.grey.shade300, borderRadius: BorderRadius.circular(2))),
              Padding(
                padding: const EdgeInsets.all(24),
                child: Row(
                  children: [
                    const Icon(Icons.assignment_ind_rounded, color: AppColors.primary),
                    const SizedBox(width: 12),
                    Text('CONVOCATORIA', style: GoogleFonts.montserrat(fontWeight: FontWeight.w900, fontSize: 18, letterSpacing: -0.5)),
                    const Spacer(),
                    Text('${_selectedPlayers.length} / 18', style: const TextStyle(fontWeight: FontWeight.bold, color: Colors.grey)),
                  ],
                ),
              ),
              const Divider(height: 1),
              Expanded(
                child: players.isEmpty 
                  ? const Center(child: Text('No hay jugadores registrados en tu club.'))
                  : ListView.builder(
                      padding: const EdgeInsets.symmetric(vertical: 10),
                      itemCount: players.length,
                      itemBuilder: (context, i) {
                        final p = players[i];
                        final isSel = _selectedPlayers.contains(p.id);
                        return CheckboxListTile(
                          activeColor: AppColors.primary,
                          value: isSel,
                          onChanged: (v) {
                            setModalState(() {
                              if (v == true) {
                                _selectedPlayers.add(p.id);
                              } else {
                                _selectedPlayers.remove(p.id);
                              }
                            });
                          },
                          title: Text(p.nombre, style: GoogleFonts.montserrat(fontWeight: FontWeight.bold, fontSize: 14)),
                          subtitle: Text('${p.posicion} • #${p.numeroCamiseta}', style: const TextStyle(fontSize: 12)),
                          secondary: CircleAvatar(
                            backgroundColor: AppColors.primary.withOpacity(0.1),
                            child: Text(p.nombre[0], style: const TextStyle(color: AppColors.primary, fontWeight: FontWeight.bold)),
                          ),
                        );
                      },
                    ),
              ),
              Padding(
                padding: const EdgeInsets.all(24),
                child: SizedBox(
                  width: double.infinity,
                  height: 55,
                  child: ElevatedButton(
                    onPressed: () async {
                      if (_user != null) {
                        final nav = Navigator.of(context);
                        final sm = ScaffoldMessenger.of(context);
                        await _teamService.saveCallUpList(_user!.club.id, _user!.division ?? 'General', _selectedPlayers);
                        nav.pop();
                        sm.showSnackBar(const SnackBar(content: Text('Citación guardada y enviada.'), backgroundColor: AppColors.success));
                      }
                    },
                    style: ElevatedButton.styleFrom(backgroundColor: AppColors.primary, foregroundColor: Colors.white),
                    child: Text('CONFIRMAR CITACIÓN', style: GoogleFonts.montserrat(fontWeight: FontWeight.bold, letterSpacing: 1)),
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  void _showCoachesNetwork() {
    final coaches = [
      {'name': 'Ricardo Gareca', 'club': 'Belgrano Athletic', 'role': 'Head Coach'},
      {'name': 'Marcelo Bielsa', 'club': 'Alumni', 'role': 'Director Técnico'},
      {'name': 'Lionel Scaloni', 'club': 'River Plate', 'role': 'Preparador Físico'},
      {'name': 'Pep Guardiola', 'club': 'CUBA', 'role': 'Asistente'},
    ];

    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      backgroundColor: Colors.transparent,
      builder: (context) => Container(
        height: MediaQuery.of(context).size.height * 0.7,
        decoration: const BoxDecoration(color: Colors.white, borderRadius: BorderRadius.vertical(top: Radius.circular(32))),
        child: Column(
          children: [
            const SizedBox(height: 12),
            Container(width: 40, height: 4, decoration: BoxDecoration(color: Colors.grey.shade300, borderRadius: BorderRadius.circular(2))),
            Padding(
              padding: const EdgeInsets.all(24),
              child: Row(
                children: [
                  const Icon(Icons.connect_without_contact_rounded, color: Colors.blue),
                  const SizedBox(width: 12),
                  Text('RED DE TÉCNICOS', style: GoogleFonts.montserrat(fontWeight: FontWeight.w900, fontSize: 18, letterSpacing: -0.5)),
                ],
              ),
            ),
            const Divider(height: 1),
            Expanded(
              child: ListView.builder(
                padding: const EdgeInsets.all(20),
                itemCount: coaches.length,
                itemBuilder: (context, i) {
                  final c = coaches[i];
                  return Container(
                    margin: const EdgeInsets.only(bottom: 12),
                    decoration: BoxDecoration(color: Colors.grey.shade50, borderRadius: BorderRadius.circular(16)),
                    child: ListTile(
                      leading: const CircleAvatar(backgroundColor: Colors.blue, child: Icon(Icons.person, color: Colors.white)),
                      title: Text(c['name']!, style: GoogleFonts.montserrat(fontWeight: FontWeight.bold, fontSize: 14)),
                      subtitle: Text('${c['role']} en ${c['club']}'),
                      trailing: IconButton(icon: const Icon(Icons.chat_outlined, color: Colors.blue), onPressed: () {}),
                    ),
                  );
                },
              ),
            ),
          ],
        ),
      ),
    );
  }
}
