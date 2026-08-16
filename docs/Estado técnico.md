# Estado técnico

## Error de compilación conocido

En el estado actual del proyecto, `MainActivity.kt` llama a `AppNavigation(supabaseClient)`, pero `AppNavigation` está declarado sin parámetros. La tarea `:app:compileDebugKotlin` falla con:

```text
Too many arguments for 'fun AppNavigation(): Unit'.
```

Este problema todavía no está corregido en esta integración documental.

## Observaciones

- Hay dos paquetes raíz: `com.example.hockey_app` y `com.example.newhockeyapp`.
- El proyecto tiene modificaciones locales preexistentes; esta documentación no intenta resolverlas.
- La capa de dominio aún no está separada.
- La navegación debe evaluarse si se migra a Navigation 3.

## Próximos pasos sugeridos

1. Resolver la firma inconsistente de `AppNavigation`.
2. Ejecutar compilación y pruebas.
3. Definir el diseño visual común con Material 3.
4. Extraer casos de uso y repositorios cuando la lógica crezca.
