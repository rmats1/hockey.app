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

# Supabase (Extraído de SupabaseModule.kt)
SUPABASE_URL = "https://hpvsvsvrdlucuxcdrgbg.supabase.co"
SUPABASE_KEY = "sb_publishable_8jSWIC_m-NjRTbux2ZoYvA_I8ypilp7" # Idealmente usar SERVICE_ROLE_KEY para backend sync

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
                "id": c["clubId"],
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
    if not raw_context: return

    context = json.loads(raw_context)
    seasons = ["008"] # 2026

    for season in context:
        if season.get("id") not in seasons: continue

        for fed in season.get("federaciones", []):
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
                                for p in detail.get("todosLosPartidos", []):
                                    p_data = {
                                        "id": p["id"],
                                        "torneo_id": t_id,
                                        "nombre_local": p["nombreLocal"],
                                        "nombre_visitante": p["nombreVisitante"],
                                        "escudo_local": p.get("escudoLocal"),
                                        "escudo_visitante": p.get("escudoVisitante"),
                                        "goles_local": p.get("golesLocal"),
                                        "goles_visitante": p.get("golesVisitante"),
                                        "horario": p.get("horario"),
                                        "numero_fecha": p["numeroFecha"],
                                        "jugado": p["jugado"]
                                    }
                                    supabase.table("partidos").upsert(p_data).execute()

                                # Sync Posiciones
                                for pos in detail.get("tablaGeneral", []):
                                    pos_data = {
                                        "torneo_id": t_id,
                                        "club_nombre": pos["clubNombre"],
                                        "puesto": pos["puesto"],
                                        "puntos": pos["puntos"],
                                        "partidos_jugados": pos.get("partidosJugados", 0),
                                        "goles_a_favor": pos.get("golesAFavor", 0),
                                        "goles_en_contra": pos.get("golesEnContra", 0)
                                    }
                                    # Para posiciones usualmente borramos y reinsertamos o usamos una clave compuesta
                                    supabase.table("posiciones").upsert(pos_data, on_conflict="torneo_id,club_nombre").execute()

                                # Sync Goleadores
                                for g in detail.get("goleadores", []):
                                    g_data = {
                                        "torneo_id": t_id,
                                        "nombre_completo": g["nombreCompleto"],
                                        "club_nombre": g["clubNombre"],
                                        "goles": g["goles"]
                                    }
                                    supabase.table("goleadores").upsert(g_data, on_conflict="torneo_id,nombre_completo").execute()

if __name__ == "__main__":
    try:
        sync_data()
        print("[+] Sincronización finalizada exitosamente.")
    except Exception as e:
        print(f"[-] Error fatal en el proceso: {e}")
