# Build y dependencias

## Configuración

- Sistema: Gradle Kotlin DSL.
- Módulos: `:app`.
- Android Gradle Plugin: `9.3.1`.
- Gradle Wrapper: `9.6.1`.
- Kotlin: `2.4.10`.
- `compileSdk`: 37.
- `minSdk`: 24.
- `targetSdk`: 35.

## Tecnologías principales

- Jetpack Compose y Material 3.
- Hilt y KSP para inyección de dependencias.
- Navigation Compose 2.x.
- Supabase Auth, PostgREST, Storage y Realtime.
- Ktor para red.
- Coil para imágenes.
- Firebase Messaging, Crashlytics y Performance.
- Kotlin Serialization.

## Comandos útiles

```powershell
.\gradlew.bat :app:assembleDebug
.\gradlew.bat :app:testDebugUnitTest
.\gradlew.bat :app:connectedDebugAndroidTest
```

## Nota de seguridad

La configuración actual contiene la URL y la clave publicable de Supabase en `app/build.gradle.kts`. Debe revisarse antes de publicar una versión de producción.
