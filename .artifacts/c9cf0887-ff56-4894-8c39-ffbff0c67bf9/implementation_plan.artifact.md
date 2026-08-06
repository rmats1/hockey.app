# Plan de Migración: Flutter a Kotlin Nativo (Hockey Plus)

Este plan detalla la migración de la aplicación "Hockey Plus" desde Flutter a Kotlin nativo utilizando Jetpack Compose, Supabase y Firebase. El objetivo es recrear la funcionalidad actual manteniendo una arquitectura limpia y moderna (MVVM).

## User Review Required

> [!IMPORTANT]
> - **Supabase & Firebase:** Necesitaremos configurar las credenciales en el nuevo proyecto. Ya identifiqué el `google-services.json` y la URL/Key de Supabase en el código original.
> - **Arquitectura:** Propongo usar **Hilt** para Inyección de Dependencias y **MVVM** para la lógica de negocio.
> - **SDK de Android:** El proyecto actual apunta al SDK 37. Es muy reciente, pero compatible si los dispositivos de destino son modernos.

## Proposed Changes

### 1. Configuración de Dependencias e Infraestructura
Añadir todas las librerías necesarias para igualar la funcionalidad de Flutter.

#### [MODIFY] [libs.versions.toml](file:///C:/Users/rmats/AndroidStudioProjects/Newhockeyapp/gradle/libs.versions.toml)
#### [MODIFY] [build.gradle.kts (app)](file:///C:/Users/rmats/AndroidStudioProjects/Newhockeyapp/app/build.gradle.kts)
#### [NEW] [google-services.json](file:///C:/Users/rmats/AndroidStudioProjects/Newhockeyapp/app/google-services.json)

- **Auth & DB:** Supabase Kotlin SDK.
- **Networking:** Ktor o Retrofit (Supabase usa Ktor internamente).
- **DI:** Hilt.
- **UI:** Compose, Navigation, Material 3, Coil (imágenes).
- **AI:** Google AI Client (Gemini).

---

### 2. Capa de Datos (Modelos y Servicios)
Migrar la lógica de servicios de Flutter a clases de Kotlin.

#### [NEW] `data/models/UserModel.kt`
#### [NEW] `data/services/SupabaseService.kt`
#### [NEW] `data/services/AuthService.kt`

---

### 3. Diseño y Tematización (UI)
Migrar los colores y tipografía definidos en `AppColors` de Flutter a Compose.

#### [MODIFY] [Color.kt](file:///C:/Users/rmats/AndroidStudioProjects/Newhockeyapp/app/src/main/java/com/example/newhockeyapp/ui/theme/Color.kt)
#### [MODIFY] [Theme.kt](file:///C:/Users/rmats/AndroidStudioProjects/Newhockeyapp/app/src/main/java/com/example/newhockeyapp/ui/theme/Theme.kt)

---

### 4. Seguridad e Inicio
Implementar la lógica de seguridad y el flujo de arranque.

#### [NEW] `utils/SecurityUtils.kt` (Detección de Root y Ventana Segura).
#### [NEW] `ui/screens/splash/SplashScreen.kt`
#### [NEW] `ui/screens/onboarding/OnboardingScreen.kt`

---

### 5. Navegación Principal
Configurar el `NavHost` para manejar el flujo entre Splash -> Login -> Home.

#### [NEW] `ui/navigation/AppNavigation.kt`

## Verification Plan

### Automated Tests
- Pruebas unitarias para `AuthService` (mockeando Supabase).
- Pruebas de UI con Compose Previews para verificar que los colores coinciden con la original.

### Manual Verification
- Ejecutar la app en un emulador/dispositivo para verificar el flujo inicial de Splash.
- Verificar que el `google-services.json` permite la comunicación con Firebase.
