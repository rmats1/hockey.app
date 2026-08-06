# Plan de Implementación: Scraper Automatizado, Sincronización Supabase y Git

Este plan detalla la integración del scraper original del proyecto antiguo, su evolución para cargar datos directamente en Supabase y la configuración final para sincronización con GitHub.

## Cambios Propuestos

### 1. Evolución del Scraper (Python)
Migraremos la lógica de `main.py` del escritorio al proyecto actual y la potenciaremos para que sea un "Backend Sync Tool".

*   **[NEW] [supabase_sync.py](file:///C:/Users/rmats/AndroidStudioProjects/Newhockeyapp/scripts/supabase_sync.py)**:
    - Hereda la lógica de descifrado AES-256-CTR.
    - Utiliza la librería `supabase-py` para subir los datos de clubes, torneos y partidos.
    - Maneja la lógica de "Upsert" (actualizar si existe, insertar si no) para evitar duplicados.

### 2. Infraestructura de Datos (Supabase)
*   **Verificación de Tablas**: Asegurar que las tablas en Supabase (`clubes`, `torneos`, `partidos`, `posiciones`, `goleadores`) coinciden con el esquema del scraper.
*   **Actualización de Modelos Kotlin**: Sincronizar los campos de los modelos en la App con los datos reales que entrega la API de AHBA.

### 3. Automatización y GitHub
*   **[NEW] [.github/workflows/data_sync.yml](file:///C:/Users/rmats/AndroidStudioProjects/Newhockeyapp/.github/workflows/data_sync.yml)**: (Opcional) Configurar una GitHub Action para que el scraper se ejecute automáticamente cada 24 horas y mantenga la App actualizada sin intervención manual.

## Plan de Verificación

### Prueba del Scraper
1.  Instalar dependencias necesarias (`pip install supabase cryptography requests`).
2.  Ejecutar el script de prueba para validar el descifrado de la API de AHBA.
3.  Verificar que los datos aparezcan reflejados en el panel de Supabase.

### Prueba en la App
1.  Abrir la App y navegar a "Torneos".
2.  Verificar que las tablas de posiciones y goleadores muestren los datos recién scrapeados.
3.  Comprobar que el "Detalle de Partido" funcione con los IDs reales generados por el sitio oficial.

### Estado de Git
1.  Inicializar el repositorio si no existe.
2.  Preparar el primer commit con toda la migración completa.
