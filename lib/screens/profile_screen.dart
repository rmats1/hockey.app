import 'dart:io';
import 'package:flutter/material.dart';
import 'package:flutter/foundation.dart' show kIsWeb;
import 'package:image_picker/image_picker.dart';
import 'package:google_fonts/google_fonts.dart';
import '../utils/colors.dart';
import '../models/user_model.dart';
import '../models/club_model.dart';
import '../services/auth_service.dart';
import '../services/data_service.dart';
import 'login_screen.dart';
import 'widgets/dashboard_widget.dart';

class ProfileScreen extends StatefulWidget {
  const ProfileScreen({super.key});

  @override
  State<ProfileScreen> createState() => _ProfileScreenState();
}

class _ProfileScreenState extends State<ProfileScreen> {
  final _authService = AuthService();
  final _imagePicker = ImagePicker();
  UserModel? _user;
  bool _isLoading = true;

  @override
  void initState() {
    super.initState();
    _loadUser();
  }

  Future<void> _loadUser() async {
    final user = await _authService.getCurrentUser();
    if (mounted) {
      if (user != null && (user.club.escudoUrl == null || user.club.escudoUrl!.isEmpty)) {
        try {
          final clubes = await DataService.instance.getClubes();
          final clubInfo = clubes.firstWhere((c) => c.clubId == user.club.id);
          if (clubInfo.escudoUrl != null) {
            setState(() {
              _user = UserModel(
                id: user.id, email: user.email, nombre: user.nombre, 
                userType: user.userType, rama: user.rama, categoria: user.categoria,
                division: user.division, numeroCamiseta: user.numeroCamiseta,
                posicion: user.posicion, rolCuerpoTecnico: user.rolCuerpoTecnico,
                fechaNacimiento: user.fechaNacimiento, fechaRegistro: user.fechaRegistro,
                fotoPath: user.fotoPath,
                club: Club(
                  id: clubInfo.clubId,
                  nombre: clubInfo.nombre,
                  escudoUrl: clubInfo.escudoUrl
                )
              );
              _isLoading = false;
            });
            await _authService.saveCurrentUser(_user!);
            return;
          }
        } catch (_) {}
      }

      setState(() {
        _user = user;
        _isLoading = false;
      });
    }
  }

  Future<void> _pickImage(ImageSource source) async {
    if (kIsWeb) {
      _showSnackBar('La función de foto está disponible en Android.', isError: true);
      return;
    }

    try {
      final pickedFile = await _imagePicker.pickImage(source: source, maxWidth: 800, maxHeight: 800, imageQuality: 85);
      if (pickedFile != null) {
        await _authService.updateUserPhoto(pickedFile.path);
        await _loadUser();
        if (mounted) _showSnackBar('¡Foto actualizada!', isError: false);
      }
    } catch (e) {
      if (mounted) _showSnackBar('Error: $e', isError: true);
    }
  }

  void _showImagePickerOptions() {
    showModalBottomSheet(
      context: context,
      shape: const RoundedRectangleBorder(borderRadius: BorderRadius.vertical(top: Radius.circular(32))),
      builder: (context) => SafeArea(
        child: Padding(
          padding: const EdgeInsets.all(24),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              Text('CAMBIAR FOTO', style: GoogleFonts.montserrat(fontSize: 16, fontWeight: FontWeight.w900, letterSpacing: 1)),
              const SizedBox(height: 24),
              ListTile(
                leading: Container(padding: const EdgeInsets.all(10), decoration: BoxDecoration(color: AppColors.primary.withOpacity(0.1), borderRadius: BorderRadius.circular(12)), child: const Icon(Icons.photo_camera_rounded, color: AppColors.primary)),
                title: Text('Tomar Foto', style: GoogleFonts.montserrat(fontWeight: FontWeight.bold, fontSize: 14)),
                onTap: () { Navigator.pop(context); _pickImage(ImageSource.camera); },
              ),
              const SizedBox(height: 8),
              ListTile(
                leading: Container(padding: const EdgeInsets.all(10), decoration: BoxDecoration(color: AppColors.primary.withOpacity(0.1), borderRadius: BorderRadius.circular(12)), child: const Icon(Icons.photo_library_rounded, color: AppColors.primary)),
                title: Text('Elegir de Galería', style: GoogleFonts.montserrat(fontWeight: FontWeight.bold, fontSize: 14)),
                onTap: () { Navigator.pop(context); _pickImage(ImageSource.gallery); },
              ),
            ],
          ),
        ),
      ),
    );
  }

  Future<void> _logout() async {
    final confirm = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(24)),
        title: Text('Cerrar sesión', style: GoogleFonts.montserrat(fontWeight: FontWeight.bold)),
        content: const Text('¿Estás seguro que querés cerrar sesión?'),
        actions: [
          TextButton(onPressed: () => Navigator.pop(context, false), child: const Text('CANCELAR', style: TextStyle(color: Colors.grey))),
          TextButton(onPressed: () => Navigator.pop(context, true), child: const Text('CERRAR SESIÓN', style: TextStyle(color: AppColors.error, fontWeight: FontWeight.bold))),
        ],
      ),
    );

    if (confirm == true) {
      await _authService.logout();
      if (!mounted) return;
      await Navigator.pushReplacement(context, MaterialPageRoute(builder: (context) => const LoginScreen()));
    }
  }

  void _showSnackBar(String message, {bool isError = false}) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(content: Text(message, style: GoogleFonts.montserrat()), backgroundColor: isError ? AppColors.error : AppColors.primary, behavior: SnackBarBehavior.floating),
    );
  }

  @override
  Widget build(BuildContext context) {
    if (_isLoading) return const Scaffold(body: Center(child: CircularProgressIndicator(color: AppColors.primary)));
    if (_user == null) return const Scaffold(body: Center(child: Text('Iniciá sesión para ver tu perfil')));

    return Scaffold(
      backgroundColor: AppColors.background,
      appBar: AppBar(
        title: Text('MI PERFIL', style: GoogleFonts.montserrat(fontWeight: FontWeight.w900, fontSize: 16)),
        backgroundColor: AppColors.primary,
        foregroundColor: Colors.white,
        elevation: 0,
        actions: [IconButton(icon: const Icon(Icons.logout_rounded), onPressed: _logout)],
      ),
      body: SingleChildScrollView(
        child: Column(
          children: [
            DashboardWidget(user: _user!),
            
            const SizedBox(height: 20),
            
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 24),
              child: Column(
                children: [
                  _buildHeaderProfile(),
                  const SizedBox(height: 30),
                  
                  _buildSectionHeader('INFORMACIÓN PERSONAL'),
                  const SizedBox(height: 12),
                  _buildCardInfo([
                    _InfoItem('Nombre Completo', _user!.nombre, Icons.person_rounded),
                    _InfoItem('Email', _user!.email, Icons.alternate_email_rounded),
                  ]),
                  
                  const SizedBox(height: 24),
                  
                  _buildSectionHeader('DATOS DEL CLUB'),
                  const SizedBox(height: 12),
                  _buildCardInfo([
                    _InfoItem('Club', _user!.club.nombre, Icons.sports_hockey_rounded),
                    _InfoItem('Rama', _user!.rama, Icons.people_rounded),
                    _InfoItem('Categoría', _user!.categoria, Icons.emoji_events_rounded),
                    if (_user!.userType == 'jugador') _InfoItem('División', _user!.division ?? '-', Icons.military_tech_rounded),
                    if (_user!.userType == 'jugador') _InfoItem('Número Camiseta', '#${_user!.numeroCamiseta}', Icons.numbers_rounded),
                  ]),
                  
                  const SizedBox(height: 40),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildHeaderProfile() {
    return Column(
      children: [
        Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            // Foto de Perfil
            Stack(
              alignment: Alignment.bottomRight,
              children: [
                Container(
                  width: 90, height: 90,
                  decoration: BoxDecoration(
                    shape: BoxShape.circle,
                    border: Border.all(color: Colors.white, width: 3),
                    boxShadow: [BoxShadow(color: Colors.black.withOpacity(0.08), blurRadius: 15, offset: const Offset(0, 8))],
                  ),
                  child: ClipOval(
                    child: _hasValidPhoto() 
                      ? Image.file(File(_user!.fotoPath!), fit: BoxFit.cover)
                      : Container(
                          color: AppColors.primary.withOpacity(0.1),
                          child: Center(child: Text(_user!.nombre.isNotEmpty ? _user!.nombre[0].toUpperCase() : '?', style: GoogleFonts.montserrat(fontSize: 32, fontWeight: FontWeight.w900, color: AppColors.primary))),
                        ),
                  ),
                ),
                GestureDetector(
                  onTap: _showImagePickerOptions,
                  child: Container(
                    padding: const EdgeInsets.all(6),
                    decoration: const BoxDecoration(color: AppColors.secondary, shape: BoxShape.circle),
                    child: const Icon(Icons.camera_alt_rounded, color: Colors.white, size: 14),
                  ),
                ),
              ],
            ),
            
            const SizedBox(width: 20),
            
            // Escudo del Club
            Container(
              width: 90, height: 90,
              padding: const EdgeInsets.all(12),
              decoration: BoxDecoration(
                color: Colors.white,
                shape: BoxShape.circle,
                boxShadow: [BoxShadow(color: Colors.black.withOpacity(0.05), blurRadius: 12, offset: const Offset(0, 4))],
              ),
              child: ClipRRect(
                borderRadius: BorderRadius.circular(40),
                child: (_user!.club.escudoUrl != null && _user!.club.escudoUrl!.isNotEmpty)
                  ? Image.network(_user!.club.escudoUrl!, fit: BoxFit.contain, errorBuilder: (_,__,___) => const Text('🏑', style: TextStyle(fontSize: 40)))
                  : const Text('🏑', style: TextStyle(fontSize: 40)),
              ),
            ),
          ],
        ),
        const SizedBox(height: 20),
        Text(_user!.nombre.toUpperCase(), style: GoogleFonts.montserrat(fontSize: 18, fontWeight: FontWeight.w900, letterSpacing: 1, color: AppColors.textPrimary)),
        const SizedBox(height: 4),
        Text(_user!.userType == 'jugador' ? 'JUGADOR/A DE HOCKEY' : 'CUERPO TÉCNICO', style: TextStyle(color: Colors.grey.shade500, fontSize: 10, fontWeight: FontWeight.w800, letterSpacing: 1.5)),
      ],
    );
  }

  Widget _buildSectionHeader(String title) {
    return Row(
      children: [
        Text(title, style: GoogleFonts.montserrat(fontSize: 9, fontWeight: FontWeight.w900, color: Colors.grey.shade600, letterSpacing: 2)),
      ],
    );
  }

  Widget _buildCardInfo(List<_InfoItem> items) {
    return Container(
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(24),
        border: Border.all(color: Colors.grey.shade100),
        boxShadow: [BoxShadow(color: Colors.black.withOpacity(0.02), blurRadius: 10, offset: const Offset(0, 4))],
      ),
      child: Column(
        children: items.asMap().entries.map((entry) {
          final isLast = entry.key == items.length - 1;
          return Column(
            children: [
              _buildInfoTile(entry.value),
              if (!isLast) Divider(height: 1, color: Colors.grey.shade50, indent: 60),
            ],
          );
        }).toList(),
      ),
    );
  }

  Widget _buildInfoTile(_InfoItem item) {
    return ListTile(
      leading: Container(padding: const EdgeInsets.all(10), decoration: BoxDecoration(color: AppColors.primary.withOpacity(0.06), borderRadius: BorderRadius.circular(12)), child: Icon(item.icon, color: AppColors.primary, size: 18)),
      title: Text(item.label, style: const TextStyle(fontSize: 9, color: Colors.grey, fontWeight: FontWeight.w800, letterSpacing: 0.5)),
      subtitle: Text(item.value, style: GoogleFonts.montserrat(fontSize: 14, fontWeight: FontWeight.bold, color: AppColors.textPrimary)),
    );
  }

  bool _hasValidPhoto() {
    if (_user!.fotoPath == null || kIsWeb) return false;
    return File(_user!.fotoPath!).existsSync();
  }
}

class _InfoItem {
  final String label;
  final String value;
  final IconData icon;
  _InfoItem(this.label, this.value, this.icon);
}
