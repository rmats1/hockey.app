import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:google_fonts/google_fonts.dart';
import '../utils/colors.dart';
import '../services/biometric_service.dart';
import '../services/theme_service.dart';

class SettingsScreen extends StatefulWidget {
  const SettingsScreen({super.key});

  @override
  State<SettingsScreen> createState() => _SettingsScreenState();
}

class _SettingsScreenState extends State<SettingsScreen> {
  final _biometricService = BiometricService();
  
  bool _biometricEnabled = false;
  bool _biometricAvailable = false;
  bool _notificationsEnabled = true;

  @override
  void initState() {
    super.initState();
    _loadSettings();
  }

  Future<void> _loadSettings() async {
    final available = await _biometricService.isBiometricAvailable();
    setState(() {
      _biometricAvailable = available;
    });
  }

  Future<void> _toggleBiometric(bool value) async {
    if (value && !_biometricAvailable) {
      _showSnack('Tu dispositivo no soporta huella', isError: true);
      return;
    }
    setState(() => _biometricEnabled = value);
    _showSnack(value ? 'Huella activada' : 'Huella desactivada');
  }

  void _showSnack(String msg, {bool isError = false}) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(msg, style: GoogleFonts.montserrat()),
        backgroundColor: isError ? AppColors.error : AppColors.primary,
        behavior: SnackBarBehavior.floating,
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final themeService = Provider.of<ThemeService>(context);

    return Scaffold(
      backgroundColor: Theme.of(context).brightness == Brightness.light ? AppColors.background : null,
      appBar: AppBar(
        title: Text('Configuración', style: GoogleFonts.montserrat(fontWeight: FontWeight.bold)),
        backgroundColor: AppColors.primary,
        foregroundColor: Colors.white,
        elevation: 0,
      ),
      body: ListView(
        padding: const EdgeInsets.all(20),
        children: [
          // ========== SEGURIDAD ==========
          _buildSectionTitle('SEGURIDAD'),
          const SizedBox(height: 12),
          _buildCard([
            SwitchListTile(
              secondary: const Icon(Icons.fingerprint_rounded, color: AppColors.primary),
              title: Text('Inicio con huella', style: GoogleFonts.montserrat(fontWeight: FontWeight.bold, fontSize: 14)),
              subtitle: Text(_biometricAvailable 
                ? 'Usá tu huella para iniciar sesión' 
                : 'No disponible en este dispositivo', style: const TextStyle(fontSize: 11)),
              value: _biometricAvailable && _biometricEnabled,
              onChanged: _biometricAvailable ? _toggleBiometric : null,
              activeColor: AppColors.primary,
            ),
            const Divider(height: 1, indent: 60),
            ListTile(
              leading: const Icon(Icons.lock_outline_rounded, color: AppColors.primary),
              title: Text('Cambiar contraseña', style: GoogleFonts.montserrat(fontWeight: FontWeight.bold, fontSize: 14)),
              trailing: const Icon(Icons.chevron_right_rounded),
              onTap: () => _showSnack('Función disponible en versión PRO'),
            ),
          ]),

          const SizedBox(height: 30),

          // ========== APARIENCIA ==========
          _buildSectionTitle('APARIENCIA'),
          const SizedBox(height: 12),
          _buildCard([
            SwitchListTile(
              secondary: const Icon(Icons.dark_mode_rounded, color: AppColors.primary),
              title: Text('Modo Oscuro', style: GoogleFonts.montserrat(fontWeight: FontWeight.bold, fontSize: 14)),
              subtitle: const Text('Cambiar el aspecto de la aplicación', style: TextStyle(fontSize: 11)),
              value: themeService.isDarkMode,
              onChanged: (v) => themeService.toggleTheme(v),
              activeColor: AppColors.primary,
            ),
            const Divider(height: 1, indent: 60),
            ListTile(
              leading: const Icon(Icons.language_rounded, color: AppColors.primary),
              title: Text('Idioma', style: GoogleFonts.montserrat(fontWeight: FontWeight.bold, fontSize: 14)),
              subtitle: const Text('Español (Argentina)', style: TextStyle(fontSize: 11)),
              trailing: const Icon(Icons.chevron_right_rounded),
            ),
          ]),

          const SizedBox(height: 30),

          // ========== NOTIFICACIONES ==========
          _buildSectionTitle('NOTIFICACIONES'),
          const SizedBox(height: 12),
          _buildCard([
            SwitchListTile(
              secondary: const Icon(Icons.notifications_active_rounded, color: AppColors.primary),
              title: Text('Notificaciones Push', style: GoogleFonts.montserrat(fontWeight: FontWeight.bold, fontSize: 14)),
              subtitle: const Text('Recibir avisos de partidos y novedades', style: TextStyle(fontSize: 11)),
              value: _notificationsEnabled,
              onChanged: (v) {
                setState(() => _notificationsEnabled = v);
                _showSnack(v ? 'Notificaciones activadas' : 'Notificaciones desactivadas');
              },
              activeColor: AppColors.primary,
            ),
          ]),

          const SizedBox(height: 30),

          // ========== CUENTA ==========
          _buildSectionTitle('CUENTA'),
          const SizedBox(height: 12),
          _buildCard([
            ListTile(
              leading: const Icon(Icons.privacy_tip_rounded, color: AppColors.primary),
              title: Text('Privacidad de datos', style: GoogleFonts.montserrat(fontWeight: FontWeight.bold, fontSize: 14)),
              trailing: const Icon(Icons.chevron_right_rounded),
              onTap: () => _showSnack('Tus datos están cifrados con AES-256'),
            ),
            const Divider(height: 1, indent: 60),
            ListTile(
              leading: const Icon(Icons.delete_forever_rounded, color: AppColors.error),
              title: Text('Eliminar mi cuenta', style: GoogleFonts.montserrat(fontWeight: FontWeight.bold, fontSize: 14, color: AppColors.error)),
              onTap: () => _showSnack('Contacto soporte para eliminación'),
            ),
          ]),

          const SizedBox(height: 30),

          // ========== INFO ==========
          _buildSectionTitle('ACERCA DE'),
          const SizedBox(height: 12),
          _buildCard([
            const ListTile(
              leading: Icon(Icons.info_outline_rounded, color: AppColors.primary),
              title: Text('Hockey AHBA', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 14)),
              subtitle: Text('Versión 1.2.0 - Stable', style: TextStyle(fontSize: 11)),
            ),
          ]),
          const SizedBox(height: 40),
        ],
      ),
    );
  }

  Widget _buildSectionTitle(String title) {
    return Text(
      title,
      style: GoogleFonts.montserrat(
        fontSize: 10,
        fontWeight: FontWeight.w900,
        color: Colors.grey,
        letterSpacing: 2,
      ),
    );
  }

  Widget _buildCard(List<Widget> children) {
    return Container(
      decoration: BoxDecoration(
        color: Theme.of(context).cardColor,
        borderRadius: BorderRadius.circular(20),
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(0.02),
            blurRadius: 10,
            offset: const Offset(0, 4),
          ),
        ],
      ),
      child: Column(children: children),
    );
  }
}
