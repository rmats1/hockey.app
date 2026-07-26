import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import '../utils/colors.dart';
import '../models/user_model.dart';
import '../services/auth_service.dart';
import '../services/team_service.dart';
import 'coach/tactical_board_screen.dart';

class MyTeamScreen extends StatefulWidget {
  const MyTeamScreen({super.key});

  @override
  State<MyTeamScreen> createState() => _MyTeamScreenState();
}

class _MyTeamScreenState extends State<MyTeamScreen> {
  final _authService = AuthService();
  final _teamService = TeamService();
  UserModel? _user;
  String? _trainingPlan;
  bool _isCalledUp = false;
  bool _isLoading = true;

  @override
  void initState() {
    super.initState();
    _loadData();
  }

  Future<void> _loadData() async {
    final user = await _authService.getCurrentUser();
    if (user != null) {
      // Usamos la división del usuario para obtener sus mensajes específicos
      final plan = await _teamService.getTrainingPlan(user.club.id, user.division ?? 'General');
      final calledUp = await _teamService.isPlayerCalledUp(user.club.id, user.division ?? 'General', user.id);
      setState(() {
        _user = user;
        _trainingPlan = plan;
        _isCalledUp = calledUp;
        _isLoading = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    if (_isLoading) return const Scaffold(body: Center(child: CircularProgressIndicator()));
    if (_user == null) return const Scaffold(body: Center(child: Text('Iniciá sesión para ver tu equipo')));

    return Scaffold(
      backgroundColor: AppColors.background,
      appBar: AppBar(
        title: Text('Mi Equipo', style: GoogleFonts.montserrat(fontWeight: FontWeight.bold)),
        backgroundColor: AppColors.primary,
        foregroundColor: Colors.white,
        elevation: 0,
        actions: [IconButton(icon: const Icon(Icons.refresh_rounded), onPressed: _loadData)],
      ),
      body: SingleChildScrollView(
        child: Column(
          children: [
            // Header del Club
            Container(
              padding: const EdgeInsets.fromLTRB(30, 10, 30, 40),
              decoration: const BoxDecoration(
                color: AppColors.primary,
                borderRadius: BorderRadius.only(bottomLeft: Radius.circular(32), bottomRight: Radius.circular(32)),
              ),
              child: Row(
                children: [
                  Container(
                    width: 60, height: 60,
                    decoration: BoxDecoration(color: Colors.white.withOpacity(0.1), borderRadius: BorderRadius.circular(16)),
                    child: const Icon(Icons.sports_hockey_rounded, color: AppColors.secondary, size: 30),
                  ),
                  const SizedBox(width: 16),
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(_user!.club.nombreCorto.toUpperCase(), style: GoogleFonts.montserrat(color: Colors.white, fontSize: 18, fontWeight: FontWeight.w900)),
                        Text('${_user!.categoria} • ${_user!.division ?? "General"}', style: const TextStyle(color: Colors.white70, fontSize: 12)),
                      ],
                    ),
                  ),
                ],
              ),
            ),

            Padding(
              padding: const EdgeInsets.all(24),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  _buildSectionHeader('ESTADO DE CITACIÓN'),
                  const SizedBox(height: 12),
                  _buildCallUpCard(),

                  const SizedBox(height: 32),

                  _buildSectionHeader('PLANIFICACIÓN FÍSICA'),
                  const SizedBox(height: 12),
                  _buildTrainingCard(),

                  const SizedBox(height: 32),

                  _buildSectionHeader('HERRAMIENTAS'),
                  const SizedBox(height: 12),
                  _buildToolItem(
                    'Pizarra Táctica', 
                    'Repasá las jugadas del equipo.', 
                    Icons.auto_graph_rounded, 
                    () => Navigator.push(context, MaterialPageRoute(builder: (_) => const TacticalBoardScreen()))
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildSectionHeader(String title) {
    return Text(title, style: GoogleFonts.montserrat(fontSize: 10, fontWeight: FontWeight.w800, color: Colors.grey, letterSpacing: 1.5));
  }

  Widget _buildCallUpCard() {
    final color = _isCalledUp ? AppColors.secondary : Colors.grey;
    final icon = _isCalledUp ? Icons.check_circle_rounded : Icons.info_outline_rounded;
    final text = _isCalledUp ? '¡ESTÁS CONVOCADO!' : 'PENDIENTE DE CITACIÓN';

    return Container(
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(24),
        boxShadow: [BoxShadow(color: Colors.black.withOpacity(0.02), blurRadius: 10, offset: const Offset(0, 4))],
      ),
      child: Column(
        children: [
          Row(
            children: [
              Container(
                padding: const EdgeInsets.all(10),
                decoration: BoxDecoration(color: color.withOpacity(0.1), shape: BoxShape.circle),
                child: Icon(icon, color: color, size: 20),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(text, style: TextStyle(fontWeight: FontWeight.w900, color: color, fontSize: 10, letterSpacing: 1)),
                    Text('Próxima Fecha - ${_user!.categoria}', style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 14)),
                  ],
                ),
              ),
            ],
          ),
          if (_isCalledUp) ...[
            const SizedBox(height: 20),
            const Divider(height: 1),
            const SizedBox(height: 16),
            const Row(
              children: [
                Icon(Icons.location_on_outlined, size: 14, color: Colors.grey),
                SizedBox(width: 8),
                Text('Lugar: Cancha del Club', style: TextStyle(fontSize: 12, fontWeight: FontWeight.w500)),
              ],
            ),
            const SizedBox(height: 8),
            const Row(
              children: [
                Icon(Icons.access_time, size: 14, color: Colors.grey),
                SizedBox(width: 8),
                Text('Horario: 14:30 hs (Citación)', style: TextStyle(fontSize: 12, fontWeight: FontWeight.w500)),
              ],
            ),
          ],
        ],
      ),
    );
  }

  Widget _buildTrainingCard() {
    return Container(
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(24),
        boxShadow: [BoxShadow(color: Colors.black.withOpacity(0.02), blurRadius: 10, offset: const Offset(0, 4))],
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Container(
                padding: const EdgeInsets.all(10),
                decoration: BoxDecoration(color: AppColors.primary.withOpacity(0.1), shape: BoxShape.circle),
                child: const Icon(Icons.fitness_center_rounded, color: AppColors.primary, size: 20),
              ),
              const SizedBox(width: 12),
              Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text('INDICACIONES ${_user!.division?.toUpperCase() ?? "GENERAL"}', style: GoogleFonts.montserrat(fontWeight: FontWeight.w800, color: AppColors.primary, fontSize: 10, letterSpacing: 1)),
                  const Text('Plan Semanal', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 14)),
                ],
              ),
            ],
          ),
          const SizedBox(height: 20),
          Container(
            width: double.infinity,
            padding: const EdgeInsets.all(16),
            decoration: BoxDecoration(color: Colors.grey.shade50, borderRadius: BorderRadius.circular(16)),
            child: Text(
              _trainingPlan ?? 'No hay trabajos físicos asignados para esta semana por el staff técnico.',
              style: TextStyle(fontSize: 13, height: 1.5, fontStyle: _trainingPlan == null ? FontStyle.normal : FontStyle.italic, color: _trainingPlan == null ? Colors.grey : AppColors.textPrimary),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildToolItem(String title, String sub, IconData icon, VoidCallback onTap) {
    return Container(
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(20),
      ),
      child: ListTile(
        onTap: onTap,
        leading: Icon(icon, color: AppColors.primary),
        title: Text(title, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 14)),
        subtitle: Text(sub, style: const TextStyle(fontSize: 11)),
        trailing: const Icon(Icons.arrow_forward_ios_rounded, size: 12),
      ),
    );
  }
}
