import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:provider/provider.dart';
import 'package:supabase_flutter/supabase_flutter.dart';
import 'screens/animated_splash_screen.dart';
import 'screens/home_screen.dart';
import 'utils/colors.dart';
import 'models/user_model.dart';
import 'models/club_model.dart';
import 'services/auth_service.dart';
import 'services/security_service.dart';
import 'services/data_service.dart';
import 'services/theme_service.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();

  // Inicialización de Supabase con tu proyecto operativo
  await Supabase.initialize(
    url: 'https://hpvsvsvrdlucuxcdrgbg.supabase.co',
    publishableKey: 'sb_publishable_8jSWIC_m-NjRTbux2ZoYvA_I8ypilp7',
  );

  final themeService = ThemeService();
  await themeService.init();

  // Seguridad Máxima: Detección de Root/Jailbreak
  final isCompromised = await SecurityService.isDeviceCompromised();

  // Seguridad Máxima: Prevenir capturas de pantalla (solo Android)
  /* if (!kIsWeb && Platform.isAndroid) {
    try {
      await FlutterWindowManager.addFlags(FlutterWindowManager.FLAG_SECURE);
    } catch (e) {
      debugPrint('WindowManager error: $e');
    }
  } */

  // Precarga el índice de clubes y torneos en memoria
  await DataService.instance.init();

  runApp(
    ChangeNotifierProvider.value(
      value: themeService,
      child: HockeyApp(isCompromised: isCompromised),
    ),
  );
}

class HockeyApp extends StatelessWidget {
  final bool isCompromised;
  const HockeyApp({super.key, required this.isCompromised});

  @override
  Widget build(BuildContext context) {
    final themeService = Provider.of<ThemeService>(context);

    if (isCompromised) {
      return MaterialApp(
        debugShowCheckedModeBanner: false,
        theme: ThemeData(useMaterial3: true, colorSchemeSeed: Colors.red),
        home: const Scaffold(
          body: Center(
            child: Padding(
              padding: EdgeInsets.all(20),
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Icon(Icons.security_update_warning, color: Colors.red, size: 80),
                  SizedBox(height: 20),
                  Text(
                    'Dispositivo No Seguro',
                    style: TextStyle(fontSize: 22, fontWeight: FontWeight.bold),
                  ),
                  SizedBox(height: 10),
                  Text(
                    'Por razones de seguridad, esta aplicación no puede ejecutarse en dispositivos con Root, Jailbreak o Modo Desarrollador activo.',
                    textAlign: TextAlign.center,
                  ),
                ],
              ),
            ),
          ),
        ),
      );
    }

    return MaterialApp(
      title: 'Hockey Plus',
      debugShowCheckedModeBanner: false,
      themeMode: themeService.themeMode,
      theme: ThemeData(
        useMaterial3: true,
        colorScheme: ColorScheme.fromSeed(
          seedColor: AppColors.primary,
          primary: AppColors.primary,
          secondary: AppColors.secondary,
          surface: Colors.white,
          brightness: Brightness.light,
        ),
        textTheme: GoogleFonts.montserratTextTheme(Theme.of(context).textTheme),
        appBarTheme: AppBarTheme(
          centerTitle: true,
          backgroundColor: AppColors.primary,
          foregroundColor: Colors.white,
          elevation: 0,
          titleTextStyle: GoogleFonts.montserrat(
            fontSize: 20,
            fontWeight: FontWeight.bold,
            color: Colors.white,
          ),
        ),
        cardTheme: CardTheme(
          elevation: 2,
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
          clipBehavior: Clip.antiAlias,
        ),
      ),
      darkTheme: ThemeData(
        useMaterial3: true,
        brightness: Brightness.dark,
        colorScheme: ColorScheme.fromSeed(
          seedColor: AppColors.primary,
          brightness: Brightness.dark,
        ),
        textTheme: GoogleFonts.montserratTextTheme(ThemeData.dark().textTheme),
      ),
      home: const AppInitializer(),
    );
  }
}

class AppInitializer extends StatefulWidget {
  const AppInitializer({super.key});

  @override
  State<AppInitializer> createState() => _AppInitializerState();
}

class _AppInitializerState extends State<AppInitializer> {
  final _authService = AuthService();

  @override
  void initState() {
    super.initState();
    _checkAuth();
  }

  Future<void> _checkAuth() async {
    final isLoggedIn = await _authService.isLoggedIn();
    if (!mounted) return;
    
    if (isLoggedIn) {
      await Navigator.pushReplacement(
        context,
        MaterialPageRoute(builder: (context) => const HomeScreen()),
      );
    } else {
      await Navigator.pushReplacement(
        context,
        MaterialPageRoute(builder: (context) => const AnimatedSplashScreen()),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColors.primary,
      body: Center(
        child: TweenAnimationBuilder(
          duration: const Duration(seconds: 1),
          tween: Tween<double>(begin: 0, end: 1),
          builder: (context, double value, child) {
            return Opacity(
              opacity: value,
              child: Transform.scale(
                scale: value,
                child: child,
              ),
            );
          },
          child: Container(
            width: 100,
            height: 100,
            decoration: const BoxDecoration(
              color: Colors.white,
              shape: BoxShape.circle,
              boxShadow: [
                BoxShadow(color: Colors.black26, blurRadius: 20, offset: Offset(0, 10))
              ],
            ),
            child: const Icon(Icons.sports_hockey, size: 60, color: AppColors.primary),
          ),
        ),
      ),
    );
  }
}
