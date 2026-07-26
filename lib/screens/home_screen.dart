import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import '../utils/colors.dart';
import '../services/auth_service.dart';
import '../models/user_model.dart';
import 'torneos_screen.dart';
import 'news_screen.dart';
import 'profile_screen.dart';
import 'fixture_screen.dart';
import 'login_screen.dart';
import 'settings_screen.dart';
import 'buscar_jugadores_screen.dart';
import 'estadisticas_screen.dart';
import 'ayuda_screen.dart';
import 'calendario_screen.dart';
import 'comparar_clubes_screen.dart';
import 'clubes_favoritos_screen.dart';
import 'predicciones_screen.dart';
import 'graficos_screen.dart';
import 'compartir_screen.dart';
import 'coach/coach_panel_screen.dart';
import 'my_team_screen.dart';

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  int _currentIndex = 0;
  final _authService = AuthService();
  UserModel? _user;

  List<Widget> get _screens {
    final List<Widget> baseScreens = [
      const TorneosScreen(),
      const FixtureScreen(),
      const NewsScreen(),
    ];

    if (_user?.userType == 'cuerpo_tecnico') {
      baseScreens.add(const CoachPanelScreen());
    } else {
      baseScreens.add(const MyTeamScreen());
    }

    baseScreens.add(const ProfileScreen());
    return baseScreens;
  }

  @override
  void initState() {
    super.initState();
    _loadUser();
  }

  Future<void> _loadUser() async {
    final user = await _authService.getCurrentUser();
    setState(() {
      _user = user;
    });
  }

  Future<void> _logout() async {
    final confirm = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(24)),
        title: Text('Cerrar sesión', style: GoogleFonts.montserrat(fontWeight: FontWeight.bold)),
        content: const Text('¿Estás seguro que querés cerrar sesión?'),
        actions: [
          TextButton(onPressed: () => Navigator.pop(context, false), child: const Text('Cancelar', style: TextStyle(color: AppColors.textSecondary))),
          TextButton(onPressed: () => Navigator.pop(context, true), child: const Text('Cerrar sesión', style: TextStyle(color: AppColors.error))),
        ],
      ),
    );

    if (confirm == true) {
      await _authService.logout();
      if (!mounted) return;
      await Navigator.pushReplacement(context, MaterialPageRoute(builder: (context) => const LoginScreen()));
    }
  }

  @override
  Widget build(BuildContext context) {
    if (_user == null) {
      return const Scaffold(body: Center(child: CircularProgressIndicator()));
    }

    final isCoach = _user?.userType == 'cuerpo_tecnico';

    return Scaffold(
      backgroundColor: AppColors.background,
      drawer: _buildDrawer(),
      body: IndexedStack(
        index: _currentIndex,
        children: _screens,
      ),
      bottomNavigationBar: Container(
        decoration: BoxDecoration(
          boxShadow: [BoxShadow(color: Colors.black.withOpacity(0.05), blurRadius: 10, offset: const Offset(0, -5))]
        ),
        child: BottomNavigationBar(
          currentIndex: _currentIndex,
          onTap: (index) => setState(() => _currentIndex = index),
          selectedItemColor: AppColors.primary,
          unselectedItemColor: Colors.grey.shade400,
          selectedLabelStyle: GoogleFonts.montserrat(fontWeight: FontWeight.bold, fontSize: 9),
          unselectedLabelStyle: GoogleFonts.montserrat(fontWeight: FontWeight.w500, fontSize: 9),
          type: BottomNavigationBarType.fixed,
          backgroundColor: Colors.white,
          elevation: 0,
          items: [
            const BottomNavigationBarItem(icon: Icon(Icons.emoji_events_outlined), activeIcon: Icon(Icons.emoji_events), label: 'TABLA DE POSICIONES'),
            const BottomNavigationBarItem(icon: Icon(Icons.sports_outlined), activeIcon: Icon(Icons.sports), label: 'FIXTURE'),
            const BottomNavigationBarItem(icon: Icon(Icons.article_outlined), activeIcon: Icon(Icons.article), label: 'NOTICIAS'),
            if (isCoach)
              const BottomNavigationBarItem(icon: Icon(Icons.admin_panel_settings_outlined), activeIcon: Icon(Icons.admin_panel_settings), label: 'TÉCNICO')
            else
              const BottomNavigationBarItem(icon: Icon(Icons.groups_outlined), activeIcon: Icon(Icons.groups), label: 'EQUIPO'),
            const BottomNavigationBarItem(icon: Icon(Icons.person_outline), activeIcon: Icon(Icons.person), label: 'PERFIL'),
          ],
        ),
      ),
    );
  }

  Widget _buildDrawer() {
    return Drawer(
      backgroundColor: Colors.white,
      child: Column(
        children: [
          Container(
            width: double.infinity,
            padding: const EdgeInsets.fromLTRB(24, 60, 24, 30),
            decoration: const BoxDecoration(
              color: AppColors.primary,
              borderRadius: BorderRadius.only(bottomRight: Radius.circular(40)),
            ),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Container(
                  width: 70, height: 70,
                  decoration: const BoxDecoration(color: Colors.white, shape: BoxShape.circle),
                  child: Center(
                    child: Text(
                      _user?.nombre.isNotEmpty == true ? _user!.nombre[0].toUpperCase() : '?',
                      style: GoogleFonts.montserrat(fontSize: 30, fontWeight: FontWeight.w900, color: AppColors.primary),
                    ),
                  ),
                ),
                const SizedBox(height: 20),
                Text(_user?.nombre ?? 'Usuario', style: GoogleFonts.montserrat(color: Colors.white, fontSize: 20, fontWeight: FontWeight.bold)),
                Text(_user?.email ?? '', style: const TextStyle(color: Colors.white70, fontSize: 12)),
                const SizedBox(height: 12),
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
                  decoration: BoxDecoration(color: Colors.white.withOpacity(0.15), borderRadius: BorderRadius.circular(12)),
                  child: Text(
                    _user?.userType == 'jugador' ? 'JUGADOR/A' : 'CUERPO TÉCNICO',
                    style: GoogleFonts.montserrat(color: Colors.white, fontSize: 9, fontWeight: FontWeight.w800, letterSpacing: 1),
                  ),
                ),
              ],
            ),
          ),
          
          Expanded(
            child: ListView(
              padding: const EdgeInsets.all(12),
              children: [
                _buildDrawerItem(Icons.emoji_events_rounded, 'Torneos', 0),
                _buildDrawerItem(Icons.sports_rounded, 'Fixture', 1),
                _buildDrawerItem(Icons.article_rounded, 'Novedades', 2),
                if (_user?.userType == 'cuerpo_tecnico')
                  _buildDrawerItem(Icons.admin_panel_settings_rounded, 'Panel Técnico', 3)
                else
                  _buildDrawerItem(Icons.groups_rounded, 'Mi Equipo', 3),
                _buildDrawerItem(Icons.person_rounded, 'Mi Perfil', 4),
                
                const Padding(
                  padding: EdgeInsets.fromLTRB(16, 24, 16, 8),
                  child: Text('EXPLORAR', style: TextStyle(fontSize: 10, fontWeight: FontWeight.w800, color: Colors.grey, letterSpacing: 2)),
                ),

                _buildMenuItem(Icons.calendar_today_rounded, 'Calendario', () => const CalendarioScreen()),
                _buildMenuItem(Icons.compare_arrows_rounded, 'Comparar clubes', () => const CompararClubesScreen()),
                _buildMenuItem(Icons.favorite_rounded, 'Mis Clubes', () => const ClubesFavoritosScreen()),
                _buildMenuItem(Icons.psychology_rounded, 'Predicciones', () => const PrediccionesScreen()),
                _buildMenuItem(Icons.person_search_rounded, 'Buscador', () => const BuscarJugadoresScreen()),
                _buildMenuItem(Icons.bar_chart_rounded, 'Estadísticas', () => const EstadisticasScreen()),
                _buildMenuItem(Icons.show_chart_rounded, 'Visualización', () => const GraficosScreen()),
                
                const Padding(
                  padding: EdgeInsets.fromLTRB(16, 24, 16, 8),
                  child: Text('SISTEMA', style: TextStyle(fontSize: 10, fontWeight: FontWeight.w800, color: Colors.grey, letterSpacing: 2)),
                ),

                _buildMenuItem(Icons.settings_rounded, 'Configuración', () => const SettingsScreen()),
                _buildMenuItem(Icons.help_center_rounded, 'Soporte', () => const AyudaScreen()),
                _buildMenuItem(Icons.share_rounded, 'Compartir', () => const CompartirScreen()),
              ],
            ),
          ),
          
          const Divider(height: 1),
          ListTile(
            contentPadding: const EdgeInsets.symmetric(horizontal: 24, vertical: 8),
            leading: const Icon(Icons.logout_rounded, color: AppColors.error),
            title: Text('Cerrar sesión', style: GoogleFonts.montserrat(color: AppColors.error, fontWeight: FontWeight.bold, fontSize: 14)),
            onTap: _logout,
          ),
        ],
      ),
    );
  }

  Widget _buildDrawerItem(IconData icon, String title, int index) {
    final isSelected = _currentIndex == index;
    return Container(
      margin: const EdgeInsets.only(bottom: 4),
      decoration: BoxDecoration(
        color: isSelected ? AppColors.primary.withOpacity(0.08) : Colors.transparent,
        borderRadius: BorderRadius.circular(12),
      ),
      child: ListTile(
        leading: Icon(icon, color: isSelected ? AppColors.primary : Colors.grey.shade600, size: 22),
        title: Text(title, style: GoogleFonts.montserrat(
          fontWeight: isSelected ? FontWeight.bold : FontWeight.w500, 
          fontSize: 14,
          color: isSelected ? AppColors.primary : AppColors.textPrimary
        )),
        onTap: () { Navigator.pop(context); setState(() => _currentIndex = index); },
        dense: true,
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
      ),
    );
  }

  Widget _buildMenuItem(IconData icon, String title, Widget Function() screenBuilder) {
    return ListTile(
      leading: Icon(icon, color: Colors.grey.shade600, size: 22),
      title: Text(title, style: GoogleFonts.montserrat(fontWeight: FontWeight.w500, fontSize: 14, color: AppColors.textPrimary)),
      onTap: () {
        Navigator.pop(context);
        Navigator.push(context, MaterialPageRoute(builder: (_) => screenBuilder()));
      },
      dense: true,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
    );
  }
}
