import os
import json
import base64
import requests
from cryptography.hazmat.primitives.ciphers import Cipher, algorithms, modes
from cryptography.hazmat.backends import default_backend
from supabase import create_client, Client

# --- CONFIGURACIÓN ---
PASSPHRASE = "uweoEVNeycw7CFBXtHNCy3nbJZmUPl0EosXGRrNDgdU="
API_BASE_URL = "https://api.tournamenttracker.buenosaireshockey.ar"
FEDERACION_ID = "001"

# Supabase (Configuración desde variables de entorno para mayor seguridad)
SUPABASE_URL = os.environ.get("SUPABASE_URL", "https://hpvsvsvrdlucuxcdrgbg.supabase.co")
SUPABASE_KEY = os.environ.get("SUPABASE_SERVICE_ROLE_KEY") # Usar service_role para permisos de escritura

if not SUPABASE_KEY:
    print("[-] ERROR: No se encontró SUPABASE_SERVICE_ROLE_KEY en las variables de entorno.")
    exit(1)

# Inicializar Supabase
supabase: Client = create_client(SUPABASE_URL, SUPABASE_KEY)

HEADERS = {
    "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
    "Origin": "https://tournamenttracker.buenosaireshockey.ar",
    "Referer": "https://tournamenttracker.buenosaireshockey.ar/"
}

def decrypt(encrypted_text, passphrase):
    try:
        parts = encrypted_text.strip('"').split(":")
        if len(parts) != 2: return None
        iv = bytes.fromhex(parts[0])
        ciphertext = bytes.fromhex(parts[1])
        key = base64.b64decode(passphrase)
        cipher = Cipher(algorithms.AES(key), modes.CTR(iv), backend=default_backend())
        decryptor = cipher.decryptor()
        decrypted = decryptor.update(ciphertext) + decryptor.finalize()
        return decrypted.decode("utf-8")
    except Exception as e:
        print(f"[-] Error descifrando: {e}")
        return None

def fetch_api(endpoint):
    url = f"{API_BASE_URL}{endpoint}"
    try:
        response = requests.get(url, headers=HEADERS, timeout=20)
        if response.status_code == 200:
            return decrypt(response.text, PASSPHRASE)
    except Exception as e:
        print(f"[-] Error API {endpoint}: {e}")
    return None

def sync_data():
    print("[*] Iniciando sincronización global AHBA -> Supabase...")

    # 1. Sincronizar Clubes
    raw_clubes = fetch_api("/clubes")
    if raw_clubes:
        clubes = json.loads(raw_clubes)
        print(f"[*] Procesando {len(clubes)} clubes...")
        for c in clubes:
            if not c.get("clubId"): continue
            data = {
                "id": int(c["clubId"]),
                "nombre": c["club"],
                "escudo_url": c.get("clubEscudo")
            }
            # Upsert en Supabase
            try:
                supabase.table("clubes").upsert(data).execute()
            except Exception as e:
                print(f"    [!] Error upsert club {c['clubId']}: {e}")

    # 2. Sincronizar Torneos y su Contenido
    raw_context = fetch_api("/get-context")
    if not raw_context:
        print("[-] ERROR: No se pudo obtener el contexto de la API.")
        return

    context = json.loads(raw_context)
    print(f"[*] Contexto obtenido. Temporadas disponibles: {[s.get('id') for s in context]}")

    # Buscamos la temporada 2026 (sea por ID "008" o por nombre)
    seasons_to_sync = []
    for s in context:
        if s.get("id") == "008" or "2026" in str(s.get("nombre", "")):
            seasons_to_sync.append(s["id"])
            print(f"[*] Temporada 2026 detectada con ID: {s['id']}")

    if not seasons_to_sync:
        print("[!] ADVERTENCIA: No se encontró una temporada marcada como 2026. Usando ID 008 por defecto.")
        seasons_to_sync = ["008"]

    for season_id in seasons_to_sync:
        print(f"[*] Procesando temporada ID: {season_id}")
        # Obtener los datos de la temporada específica del contexto
        season_data = next((s for s in context if s["id"] == season_id), None)
        if not season_data: continue

        for fed in season_data.get("federaciones", []):
            if fed.get("id") != FEDERACION_ID: continue

            for rama in fed.get("ramas", []):
                r_key = rama.get("key")
                r_text = rama.get("text")
                for cat in rama.get("categorias", []):
                    c_id = cat.get("id")
                    c_nom = cat.get("nombre")

                    endpoint_div = f"/torneos-x-division/{FEDERACION_ID}/{season['id']}/{r_key}/{c_id}"
                    raw_divs = fetch_api(endpoint_div)
                    if not raw_divs: continue

                    divisions = json.loads(raw_divs)
                    for div_group in divisions:
                        for division in div_group.get("divisiones", []):
                            div_nom = division.get("nombre")

                            for t_ref in division.get("torneos", []):
                                t_id = t_ref.get("id")
                                print(f"[*] Sincronizando Torneo: {t_ref['nombre']} ({c_nom})")

                                # Guardar Cabecera Torneo
                                t_data = {
                                    "id": t_id,
                                    "nombre": t_ref["nombre"],
                                    "rama": r_text,
                                    "categoria": c_nom,
                                    "division": div_nom,
                                    "temporada": "2026"
                                }
                                supabase.table("torneos").upsert(t_data).execute()

                                # Obtener Detalle (Partidos, Posiciones, Goleadores)
                                raw_detail = fetch_api(f"/torneos/{t_id}")
                                if not raw_detail: continue
                                detail = json.loads(raw_detail)

                                # Sync Partidos
                                all_matches = []
                                # Intentar recolectar partidos de todos los campos posibles que usa la API de AHBA
                                all_matches.extend(detail.get("todosLosPartidos", []))
                                all_matches.extend(detail.get("proximosPartidos", []))
                                all_matches.extend(detail.get("partidosAnteriores", []))

                                # Si hay una lista de "fechas", iterar por cada una
                                if "fechas" in detail:
                                    for fecha_obj in detail["fechas"]:
                                        all_matches.extend(fecha_obj.get("partidos", []))

                                # Eliminar duplicados por ID para no saturar Supabase
                                seen_match_ids = set()
                                unique_matches = []
                                for m in all_matches:
                                    if m["id"] not in seen_match_ids:
                                        unique_matches.append(m)
                                        seen_match_ids.add(m["id"])

                                print(f"    [*] Encontrados {len(unique_matches)} partidos únicos.")

                                for p in unique_matches:
                                    p_data = {
                                        "id": p["id"],
                                        "torneo_id": t_id,
                                        "fecha": p.get("fecha") or p.get("horario"),
                                        "numero_fecha": str(p.get("numeroFecha", p.get("numero_fecha", ""))),
                                        "equipo_local": p.get("nombreLocal", p.get("equipo_local", "")),
                                        "equipo_visita": p.get("nombreVisitante", p.get("equipo_visita", "")),
                                        "escudo_local": p.get("escudoLocal", p.get("escudo_local")),
                                        "escudo_visita": p.get("escudoVisitante", p.get("escudo_visita")),
                                        "goles_local": p.get("golesLocal", p.get("goles_local")),
                                        "goles_visita": p.get("golesVisitante", p.get("goles_visita")),
                                        "jugado": p.get("jugado", False),
                                        "torneo_nombre": t_ref["nombre"]
                                    }
                                    supabase.table("partidos").upsert(p_data).execute()

                                # Sync Posiciones
                                for pos in detail.get("tablaGeneral", []):
                                    pos_data = {
                                        "id": f"{t_id}_{pos.get('clubNombre', '')}",
                                        "torneo_id": t_id,
                                        "torneo_nombre": t_ref["nombre"],
                                        "posicion": pos.get("puesto", 0),
                                        "equipo": pos.get("clubNombre", ""),
                                        "escudo": pos.get("escudoUrl"),
                                        "pj": pos.get("partidosJugados", 0),
                                        "pg": pos.get("partidosGanados", 0),
                                        "pe": pos.get("partidosEmpatados", 0),
                                        "pp": pos.get("partidosPerdidos", 0),
                                        "gf": pos.get("golesAFavor", 0),
                                        "gc": pos.get("golesEnContra", 0),
                                        "puntos": pos.get("puntos", 0),
                                        "categoria": c_nom,
                                        "genero": r_text
                                    }
                                    supabase.table("posiciones").upsert(pos_data).execute()

                                # Sync Goleadores
                                supabase.table("goleadores").delete().eq("torneo_id", t_id).execute()
                                seen_goleadores = set()
                                for g in detail.get("goleadores", []):
                                    jugador_nombre = " ".join(filter(None, [g.get("jug_nombre"), g.get("jug_apellido"), g.get("nombreCompleto"), g.get("jugador_nombre")])).strip()
                                    club_nombre = g.get("clubNombre", g.get("club_nombre", ""))
                                    dedupe_key = (jugador_nombre, club_nombre)
                                    if dedupe_key in seen_goleadores: continue
                                    seen_goleadores.add(dedupe_key)
                                    g_data = {
                                        "torneo_id": t_id,
                                        "torneo_nombre": t_ref["nombre"],
                                        "jugador_nombre": jugador_nombre,
                                        "club_nombre": club_nombre,
                                        "foto_url": g.get("jug_foto", g.get("fotoUrl", g.get("foto_url"))),
                                        "goles": g.get("goles", 0),
                                        "categoria": c_nom,
                                        "genero": r_text
                                    }
                                    supabase.table("goleadores").insert(g_data).execute()

if __name__ == "__main__":
    try:
        sync_data()
        print("[+] Sincronización finalizada exitosamente.")
    except Exception as e:
        print(f"[-] Error fatal en el proceso: {e}")
