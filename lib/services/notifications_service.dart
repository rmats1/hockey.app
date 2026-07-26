import 'package:flutter/foundation.dart' show kIsWeb;

class NotificationsService {
  // Placeholder del servicio de notificaciones
  // En una versión completa usaría firebase_messaging o similar

  static bool _initialized = false;

  static Future<void> initialize() async {
    if (kIsWeb) return;
    // Inicialización real con firebase_messaging
    _initialized = true;
  }

  static Future<void> showLocalNotification({
    required String title,
    required String body,
  }) async {
    if (kIsWeb || !_initialized) return;
    // Lógica para mostrar notificación local
  }

  static Future<void> subscribeToTopic(String topic) async {
    if (kIsWeb || !_initialized) return;
    // Suscribirse a tema de notificaciones
  }
}
