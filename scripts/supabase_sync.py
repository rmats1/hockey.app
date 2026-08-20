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

# Supabase
SUPABASE_URL = os.environ.get("SUPABASE_URL", "https://hpvsvsvrdlucuxcdrgbg.supabase.co")
SUPABASE_KEY = os.environ.get("SUPABASE_SERVICE_ROLE_KEY")

if not SUPABASE_KEY:
    print("[-] ERROR: No se encontró SUPABASE_SERVICE_ROLE_KEY.")
    exit(1)

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
    except: return None

def fetch_api(endpoint):
    url = f"{API_BASE_URL}{endpoint}"
    try:
        response = requests.get(url, headers=HEADERS, timeout=30)
        if response.status_code == 200:
            return decrypt(response.text, PASSPHRASE)
    except Exception as e:
        print(f"[-] Error API {endpoint}: {e}")
    return None

def sync_data():
    print("[*] Iniciando sincronización DEEP AHBA -> Supabase...")

    # 1. Sincronizar Clubes
    raw_clubes = fetch_api("/clubes")
    if raw_clubes:
        clubes = json.loads(raw_clubes)
        print(f"[*] Procesando {len(clubes)} clubes...")
        for c in clubes:
            if not c.get("clubId"): continue
            data = {"id": int(c["clubId"]), "nombre": c["club"], "escudo_url": c.get("clubEscudo")}
            try: supabase.table("clubes").upsert(data).execute()
            except: pass

    # 2. Sincronizar Torneos
    raw_context = fetch_api("/get-context")
    if not raw_context: return

    context = json.loads(raw_context)
    seasons_to_sync = []
    for s in context:
        if s.get("id") == "008" or "2026" in str(s.get("nombre", "")):
            seasons_to_sync.append(s["id"])

    for season_id in seasons_to_sync:
        season_data = next((s for s in context if s["id"] == season_id), None)
        if not season_data: continue

        for fed in season_data.get("federaciones", []):
            if fed.get("id") != FEDERACION_ID: continue
            for rama in fed.get("ramas", []):
                r_key, r_text = rama.get("key"), rama.get("text")
                for cat in rama.get("categorias", []):
                    c_id, c_nom = cat.get("id"), cat.get("nombre")
                    raw_divs = fetch_api(f"/torneos-x-division/{FEDERACION_ID}/{season_id}/{r_key}/{c_id}")
                    if not raw_divs: continue

                    divisions = json.loads(raw_divs)
                    for div_group in divisions:
                        for division in div_group.get("divisiones", []):
                            div_nom = division.get("nombre")
                            for t_ref in division.get("torneos", []):
                                t_id = t_ref.get("id")
                                print(f"[*] Sincronizando Torneo: {t_ref['nombre']} ({c_nom})")

                                t_data = {
                                    "id": t_id, "nombre": t_ref["nombre"], "rama": r_text,
                                    "categoria": c_nom, "division": div_nom, "temporada": "2026"
                                }
                                supabase.table("torneos").upsert(t_data).execute()

                                raw_detail = fetch_api(f"/torneos/{t_id}")
                                if not raw_detail: continue
                                detail = json.loads(raw_detail)

                                # --- EXTRACCIÓN DEEP DE PARTIDOS Y POSICIONES ---
                                all_matches = []
                                unique_positions = []

                                for fase in detail.get("fases", []):
                                    for zona in fase.get("zonas", []):
                                        # Partidos
                                        all_matches.extend(zona.get("partidos") or [])
                                        # Posiciones
                                        unique_positions.extend(zona.get("tablaGeneral") or [])

                                # Upsert Partidos
                                seen_match_ids = set()
                                for p in all_matches:
                                    if not p or p.get("id") in seen_match_ids: continue
                                    seen_match_ids.add(p["id"])
                                    p_data = {
                                        "id": p["id"], "torneo_id": t_id,
                                        "fecha": p.get("horario") or p.get("fecha"),
                                        "numero_fecha": str(p.get("numeroFecha", "")),
                                        "equipo_local": p.get("nombreLocal", ""),
                                        "equipo_visita": p.get("nombreVisitante", ""),
                                        "escudo_local": p.get("escudoImagePathLocal"),
                                        "escudo_visita": p.get("escudoImagePathVisitante"),
                                        "goles_local": p.get("golesLocal"),
                                        "goles_visita": p.get("golesVisitante"),
                                        "jugado": p.get("played", False),
                                        "torneo_nombre": t_ref["nombre"]
                                    }
                                    supabase.table("partidos").upsert(p_data).execute()

                                # Upsert Posiciones
                                for pos in unique_positions:
                                    if not pos.get("clubNombre"): continue
                                    pos_data = {
                                        "id": f"{t_id}_{pos.get('clubNombre')}", "torneo_id": t_id,
                                        "posicion": pos.get("puesto", 0), "equipo": pos.get("clubNombre"),
                                        "escudo": pos.get("escudoUrl"), "pj": pos.get("partidosJugados", 0),
                                        "pg": pos.get("partidosGanados", 0), "pe": pos.get("partidosEmpatados", 0),
                                        "pp": pos.get("partidosPerdidos", 0), "gf": pos.get("golesAFavor", 0),
                                        "gc": pos.get("golesEnContra", 0), "puntos": pos.get("puntos", 0)
                                    }
                                    supabase.table("posiciones").upsert(pos_data).execute()

                                # Sync Goleadores
                                if detail.get("goleadores"):
                                    supabase.table("goleadores").delete().eq("torneo_id", t_id).execute()
                                    for g in detail.get("goleadores", []):
                                        nom = " ".join(filter(None, [g.get("jug_nombre"), g.get("jug_apellido"), g.get("nombreCompleto")])).strip()
                                        if not nom: continue
                                        g_data = {
                                            "torneo_id": t_id, "jugador_nombre": nom, "club_nombre": g.get("clubNombre", ""),
                                            "foto_url": g.get("jug_foto", g.get("fotoUrl")), "goles": g.get("goles", 0)
                                        }
                                        try: supabase.table("goleadores").insert(g_data).execute()
                                        except: pass

if __name__ == "__main__":
    try: sync_data(); print("[+] Sincronización finalizada exitosamente.")
    except Exception as e: print(f"[-] Error fatal: {e}")
