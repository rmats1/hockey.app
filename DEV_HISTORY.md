# Historial de Desarrollo - Hockey Plus

## ⚠️ Procesos y Fallos de Sincronización (Temporada 2026)

Durante la implementación del motor de resultados oficiales de la AHBA, se probaron las siguientes estrategias que resultaron fallidas:

### 1. Conexión Directa a API (v1 y v2)
*   **Intento**: Llamar a `api.tournamenttracker.buenosaireshockey.ar`.
*   **Resultado**: **Error 500 / 403**.
*   **Causa**: El servidor de la AHBA implementó un firewall estricto para 2026 que detecta y bloquea peticiones que no provengan de un navegador humano real (User-Agent móvil no es suficiente).

### 2. Scraping Off-Screen (Navegador Invisible)
*   **Intento**: Cargar un WebView de 1x1 píxeles fuera de la pantalla para extraer datos mediante JavaScript.
*   **Resultado**: **Pantalla en blanco infinita**.
*   **Causa**: La web oficial está construida en React. Si el navegador detecta que es invisible o que el área de renderizado es muy pequeña, detiene el procesamiento de JavaScript para ahorrar recursos, impidiendo que la tabla se genere.

### 3. Enlaces Dinámicos (Slugs Predictivos)
*   **Intento**: Construir URLs como `/torneos/caballeros-c1-2026`.
*   **Resultado**: **Error 404**.
*   **Causa**: La asociación cambia los identificadores de texto frecuentemente (ej: de `caballeros-c1` a `masculino-c-1`). No es posible predecir el enlace exacto sin una base de datos maestra actualizada cada hora.

---

## ✅ Solución Operativa Actual: Scraping por "Inyección Sincronizada"
*   **Estado**: Implementado en `torneo_detalle_screen.dart`.
*   **Técnica**: Renderizado activo en primer plano (bajo capa de diseño) + Bucle de observación de DOM (DataBridge).

## 🛠️ Actualización de Infraestructura (Julio 2026)
*   **Migración Supabase v2**: Actualización de `supabase_flutter` a v2.15.4 para aprovechar mejoras en rendimiento y seguridad.
*   **Auth UI**: Integración de `supabase_auth_ui` para estandarizar el flujo de login y registro.
*   **Persistencia Cloud**: Transición de `FlutterSecureStorage` a **Supabase Auth + Profiles** para garantizar que los usuarios no se pierdan al borrar datos del dispositivo.

