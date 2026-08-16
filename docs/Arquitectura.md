# Arquitectura

## Resumen

La aplicación es actualmente un proyecto Android de un único módulo (`:app`) escrito en Kotlin y construido con Jetpack Compose.

La organización principal es:

```text
:app
└── src/main/java/com/example/hockey_app
    ├── data        modelos y servicios de Supabase
    ├── di          módulos de Hilt
    ├── ui          navegación, tema, pantallas y ViewModels
    └── utils       utilidades Android
```

## Flujo actual

```text
Composable → ViewModel → servicio de datos → Supabase
```

Los ViewModels usan inyección de constructor con Hilt y las pantallas están implementadas como Composables.

## Capas observadas

### UI

Incluye `MainActivity`, navegación, pantallas Compose y ViewModels por funcionalidad.

### Datos

Incluye modelos en `data/models` y servicios como `AuthService`, `DataService` y `SupabaseService`.

### Dominio

No existe todavía una capa `domain` independiente. La lógica de negocio y el acceso a servicios están parcialmente concentrados en los ViewModels.

## Inyección de dependencias

Hilt está configurado mediante:

- `HockeyApp` con `@HiltAndroidApp`.
- `MainActivity` con `@AndroidEntryPoint`.
- ViewModels con `@HiltViewModel`.
- `SupabaseModule` instalado en `SingletonComponent`.

## Evolución recomendada

Para crecer hacia Clean Architecture, separar gradualmente:

1. Interfaces de repositorio y casos de uso en `domain`.
2. Implementaciones Supabase en `data`.
3. Modelos de dominio independientes de los modelos remotos.
4. Estado de pantalla y eventos expuestos mediante `StateFlow`/`SharedFlow`.
