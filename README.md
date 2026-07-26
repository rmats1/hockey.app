# 🏑 Hockey Plus (Temporada 2026)

**Hockey Plus** es una aplicación integral para el seguimiento del Hockey Metropolitano (AHBA) en Argentina. Diseñada para jugadores, cuerpos técnicos y aficionados.

## 🚀 Características Principales

*   **Resultados en Vivo:** Motor de sincronización avanzada con la AHBA (DataBridge v2) para superar firewalls y bloqueos de React.
*   **Gestión de Torneos:** Tablas de posiciones, fixtures completos y tablas de goleadores siempre actualizadas.
*   **Panel de Coach:** Herramientas tácticas y seguimiento físico para cuerpos técnicos.
*   **Mi Equipo:** Espacio personalizado para el jugador con estadísticas y próximos partidos.
*   **Noticias y Novedades:** Integración con noticias oficiales y notificaciones.

## 🛠️ Stack Tecnológico

*   **Frontend:** [Flutter](https://flutter.dev) (Dart 3.5.4)
*   **Backend:** [Supabase](https://supabase.com) (Auth, Database, Storage)
*   **Seguridad:** Detección de Root/Jailbreak y protección de integridad de datos.
*   **Persistencia:** Híbrida (JSON local + Supabase + Scraping en tiempo real).

## 📦 Instalación

1.  Asegúrate de tener instalado el SDK de Flutter (^3.5.4).
2.  Clona el repositorio: `git clone https://github.com/rmats1/hockey.app.git`
3.  Instala las dependencias: `flutter pub get`
4.  Configura las claves de Supabase en `lib/main.dart`.
5.  Ejecuta la aplicación: `flutter run`

---
*Desarrollado con ❤️ para la comunidad del hockey argentino.*
