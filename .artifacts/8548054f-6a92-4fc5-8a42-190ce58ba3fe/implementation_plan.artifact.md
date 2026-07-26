# Plan de Reestructuración y Conexión (GitHub + Supabase)

Este plan describe el proceso para estabilizar el proyecto, documentarlo correctamente, conectarlo a GitHub y finalizar la integración con Supabase.

## User Review Required

> [!IMPORTANT]
> **GitHub**: Necesitaré que crees un repositorio vacío en GitHub y me proporciones la URL (ej. `https://github.com/usuario/repositorio.git`).
>
> **Supabase**: Para la gestión de usuarios, sigue siendo crítico ejecutar el script SQL en el Dashboard de Supabase.

## Proposed Changes

### 1. Respaldo y Estabilización
- Identificar y asegurar la última APK estable (`hockey_plus_v1.0.2_master.apk`).
- Crear una carpeta de respaldo específica para la versión actual antes de los cambios mayores.

### 2. Documentación (Actualización de MDs)
- **[MODIFY] [README.md](file:///C:/Users/rmats/Desktop/proyecto numero 1/hockey_app/README.md)**: Actualizar con la descripción real del proyecto, arquitectura (Supabase, Scraping AHBA) y guía de instalación.
- **[MODIFY] [DEV_HISTORY.md](file:///C:/Users/rmats/Desktop/proyecto numero 1/hockey_app/DEV_HISTORY.md)**: Añadir la sección de "Actualización de Infraestructura 2026" incluyendo la migración a Supabase v2.

### 3. Conexión a GitHub
- Inicializar repositorio Git local.
- Configurar `.gitignore` profesional para Flutter.
- Realizar el primer commit ("Initial stable version before Supabase Auth migration").
- Vincular con el repositorio remoto.

### 4. Conexión a Supabase (Auth & Profiles)
- Migrar `AuthService` para usar `SupabaseAuth`.
- Implementar el flujo de Registro/Login real.
- Guía paso a paso para el usuario sobre el SQL necesario.

## Paso a Paso para el Usuario

### Fase 1: GitHub (Ahora)
1. Crea un repo en GitHub.
2. Pásame la URL.

### Fase 2: Supabase (Después de GitHub)
1. Ir a [Supabase Dashboard](https://app.supabase.com/).
2. SQL Editor -> Nuevo Query.
3. Pegar el script de perfiles (te lo daré en el momento exacto).
4. Run.

## Verification Plan

### Automated Tests
- `git status` para verificar el control de versiones.
- `flutter pub get` para asegurar dependencias limpias.

### Manual Verification
- Verificar la visibilidad del código en GitHub.
- Probar el flujo de login real con Supabase Auth.
