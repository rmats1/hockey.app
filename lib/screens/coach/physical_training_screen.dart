import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import '../../utils/colors.dart';
import '../../models/user_model.dart';
import '../../data/jugadores_data.dart';
import '../../models/jugador_model.dart';
import '../../services/team_service.dart';

class PhysicalTrainingScreen extends StatefulWidget {
  final UserModel coach;
  const PhysicalTrainingScreen({super.key, required this.coach});

  @override
  State<PhysicalTrainingScreen> createState() => _PhysicalTrainingScreenState();
}

class _PhysicalTrainingScreenState extends State<PhysicalTrainingScreen> {
  final _instructionController = TextEditingController();
  final _teamService = TeamService();
  final List<String> _selectedPlayerIds = [];
  bool _sendToAll = true; 
  bool _isSending = false;
  
  late List<JugadorModel> _divisionPlayers;

  @override
  void initState() {
    super.initState();
    _divisionPlayers = JugadoresData.getJugadoresPorClub(widget.coach.club.id)
        .where((p) => p.division == widget.coach.division)
        .toList();
  }

  @override
  void dispose() {
    _instructionController.dispose();
    super.dispose();
  }

  Future<void> _sendInstructions() async {
    if (_instructionController.text.trim().isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Escribí las indicaciones antes de enviar.')),
      );
      return;
    }

    setState(() => _isSending = true);

    await _teamService.saveTrainingPlan(
      widget.coach.club.id, 
      widget.coach.division ?? 'General', 
      _instructionController.text.trim()
    );

    if (!mounted) return;
    setState(() => _isSending = false);

    String recipient = _sendToAll ? 'todo el plantel de ${widget.coach.division}' : '${_selectedPlayerIds.length} jugadores';
    
    await showDialog(
      context: context,
      builder: (context) => AlertDialog(
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(24)),
        title: const Row(
          children: [
            Icon(Icons.check_circle, color: AppColors.success),
            SizedBox(width: 10),
            Text('¡Enviado!'),
          ],
        ),
        content: Text('Las indicaciones fueron enviadas a $recipient correctamente.'),
        actions: [
          TextButton(
            onPressed: () {
              Navigator.pop(context);
              Navigator.pop(context);
            },
            child: const Text('VOLVER AL PANEL'),
          ),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColors.background,
      appBar: AppBar(
        title: Text('Planificación ${_coachDivision()}', style: GoogleFonts.montserrat(fontWeight: FontWeight.bold)),
        backgroundColor: AppColors.primary,
        foregroundColor: Colors.white,
        elevation: 0,
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(24),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            _buildSectionTitle('INDICACIONES PARA ${widget.coach.division?.toUpperCase() ?? "EL EQUIPO"}'),
            const SizedBox(height: 12),
            Container(
              decoration: BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.circular(20),
                boxShadow: [BoxShadow(color: Colors.black.withOpacity(0.02), blurRadius: 10, offset: const Offset(0, 4))],
              ),
              child: TextField(
                controller: _instructionController,
                maxLines: 6,
                style: const TextStyle(fontSize: 14),
                decoration: InputDecoration(
                  hintText: 'Ej: Trabajos de velocidad para la ${widget.coach.division}...',
                  hintStyle: TextStyle(color: Colors.grey.shade400, fontSize: 13),
                  border: InputBorder.none,
                  contentPadding: const EdgeInsets.all(20),
                ),
              ),
            ),

            const SizedBox(height: 30),
            _buildSectionTitle('DESTINATARIOS'),
            const SizedBox(height: 12),
            
            Container(
              decoration: BoxDecoration(
                color: _sendToAll ? AppColors.primary.withOpacity(0.05) : Colors.white,
                borderRadius: BorderRadius.circular(16),
                border: Border.all(color: _sendToAll ? AppColors.primary : Colors.transparent),
              ),
              child: SwitchListTile(
                value: _sendToAll,
                activeColor: AppColors.primary,
                onChanged: (v) => setState(() => _sendToAll = v),
                title: Text('TODA LA ${_coachDivision().toUpperCase()}', style: GoogleFonts.montserrat(fontWeight: FontWeight.bold, fontSize: 11, letterSpacing: 1)),
                secondary: Icon(Icons.groups_rounded, color: _sendToAll ? AppColors.primary : Colors.grey),
              ),
            ),

            if (!_sendToAll) ...[
              const SizedBox(height: 16),
              const Text('SELECCIONÁ JUGADORES DE TU DIVISIÓN:', style: TextStyle(fontSize: 10, fontWeight: FontWeight.bold, color: Colors.grey)),
              const SizedBox(height: 12),
              Container(
                height: 250,
                decoration: BoxDecoration(
                  color: Colors.white,
                  borderRadius: BorderRadius.circular(20),
                ),
                child: _divisionPlayers.isEmpty 
                  ? const Center(child: Text('No hay jugadores registrados en tu división.', style: TextStyle(fontSize: 12, color: Colors.grey)))
                  : ListView.separated(
                      padding: const EdgeInsets.all(10),
                      itemCount: _divisionPlayers.length,
                      separatorBuilder: (_, __) => Divider(height: 1, color: Colors.grey.shade50),
                      itemBuilder: (context, i) {
                        final p = _divisionPlayers[i];
                        final isSel = _selectedPlayerIds.contains(p.id);
                        return CheckboxListTile(
                          value: isSel,
                          activeColor: AppColors.primary,
                          onChanged: (v) {
                            setState(() {
                              if (v == true) {
                                _selectedPlayerIds.add(p.id);
                              } else {
                                _selectedPlayerIds.remove(p.id);
                              }
                            });
                          },
                          title: Text(p.nombre, style: const TextStyle(fontSize: 13, fontWeight: FontWeight.w600)),
                          subtitle: Text(p.posicion, style: const TextStyle(fontSize: 11)),
                        );
                      },
                    ),
              ),
            ],

            const SizedBox(height: 40),

            SizedBox(
              width: double.infinity,
              height: 55,
              child: ElevatedButton.icon(
                onPressed: _isSending ? null : _sendInstructions,
                icon: _isSending 
                  ? const SizedBox(width: 20, height: 20, child: CircularProgressIndicator(color: Colors.white, strokeWidth: 2))
                  : const Icon(Icons.send_rounded),
                label: Text(_isSending ? 'ENVIANDO...' : 'ENVIAR A LA ${_coachDivision().toUpperCase()}', style: GoogleFonts.montserrat(fontWeight: FontWeight.bold, letterSpacing: 1)),
                style: ElevatedButton.styleFrom(
                  backgroundColor: AppColors.secondary,
                  foregroundColor: Colors.white,
                ),
              ),
            ),
            const SizedBox(height: 30),
          ],
        ),
      ),
    );
  }

  String _coachDivision() => widget.coach.division ?? 'Equipo';

  Widget _buildSectionTitle(String title) {
    return Row(
      children: [
        Container(width: 4, height: 14, decoration: BoxDecoration(color: AppColors.secondary, borderRadius: BorderRadius.circular(2))),
        const SizedBox(width: 10),
        Text(title, style: GoogleFonts.montserrat(fontSize: 11, fontWeight: FontWeight.w800, color: AppColors.textPrimary, letterSpacing: 1)),
      ],
    );
  }
}
