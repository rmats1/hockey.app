import 'dart:io';
import 'dart:convert';
import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:flutter_jailbreak_detection/flutter_jailbreak_detection.dart';

class SecurityService {
  /// Verifica si el dispositivo está rooteado o tiene jailbreak.
  /// En Android también verifica si el modo desarrollador está activo (opcional).
  static Future<bool> isDeviceCompromised() async {
    try {
      // No aplica para Web o Desktop
      if (kIsWeb || (!Platform.isAndroid && !Platform.isIOS)) return false;

      bool jailbroken = await FlutterJailbreakDetection.jailbroken;
      bool developerMode = false;
      
      if (Platform.isAndroid) {
        developerMode = await FlutterJailbreakDetection.developerMode;
      }

      return jailbroken || developerMode;
    } on PlatformException {
      return true; // En caso de error, pecamos de precavidos
    }
  }

  /// Ofuscación básica de strings sensibles (ej. API Keys)
  /// Solo como ejemplo de "Defense in Depth"
  static String simpleDeobfuscate(String base64String) {
    return String.fromCharCodes(base64.decode(base64String));
  }
}
