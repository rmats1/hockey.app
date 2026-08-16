# Navegación

La navegación actual está centralizada en `ui/navigation/AppNavigation.kt` y utiliza `NavHost`, `rememberNavController` y rutas string de Navigation Compose.

## Flujo de entrada

```text
Splash → Login → Register
       ↘ Home ← Onboarding
```

## Rutas principales

| Área | Rutas |
|---|---|
| Acceso | `splash`, `login`, `register`, `onboarding` |
| Inicio | `home` |
| Torneos | `torneo_detalle`, `estadisticas`, `charts` |
| Fixture | `calendar`, `match_detail`, `predictions`, `comments` |
| Clubes/equipo | `compare_clubs`, `favorite_clubs`, `search_players` |
| Entrenador | `physical_planning`, `call_up_management` |
| Perfil | `settings`, `ayuda`, `share_app` |
| Noticias | `news_detail` |
| Táctica | `tactical_board` |

## Observación

Las instrucciones del proyecto piden Navigation 3, pero la implementación actual usa Navigation Compose tradicional (`androidx.navigation.compose`).
