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

                                # --- FUSIÓN INTELIGENTE DE PARTIDOS ---
                                all_matches = []
                                all_matches.extend(detail.get("todosLosPartidos") or [])
                                all_matches.extend(detail.get("proximosPartidos") or [])
                                all_matches.extend(detail.get("partidosAnteriores") or [])
                                if "fechas" in detail and detail["fechas"]:
                                    for f in detail["fechas"]:
                                        all_matches.extend(f.get("partidos") or [])

                                seen_ids = set()
                                for p in all_matches:
                                    if not p or p["id"] in seen_ids: continue
                                    seen_ids.add(p["id"])
                                    p_data = {
                                        "id": p["id"], "torneo_id": t_id, "fecha": p.get("fecha") or p.get("horario"),
                                        "numero_fecha": str(p.get("numeroFecha", p.get("numero_fecha", ""))),
                                        "equipo_local": p.get("nombreLocal", p.get("equipo_local", "")),
                                        "equipo_visita": p.get("nombreVisitante", p.get("equipo_visita", "")),
                                        "escudo_local": p.get("escudoLocal", p.get("escudo_local")),
                                        "escudo_visita": p.get("escudoVisitante", p.get("escudo_visita")),
                                        "goles_local": p.get("golesLocal", p.get("goles_local")),
                                        "goles_visita": p.get("golesVisitante", p.get("goles_visita")),
                                        "jugado": p.get("jugado", False), "torneo_nombre": t_ref["nombre"]
                                    }
                                    supabase.table("partidos").upsert(p_data).execute()

                                # Sync Posiciones
                                for pos in detail.get("tablaGeneral", []):
                                    pos_data = {
                                        "id": f"{t_id}_{pos.get('clubNombre', '')}", "torneo_id": t_id,
                                        "posicion": pos.get("puesto", 0), "equipo": pos.get("clubNombre", ""),
                                        "escudo": pos.get("escudoUrl"), "pj": pos.get("partidosJugados", 0),
                                        "pg": pos.get("partidosGanados", 0), "pe": pos.get("partidosEmpatados", 0),
                                        "pp": pos.get("partidosPerdidos", 0), "gf": pos.get("golesAFavor", 0),
                                        "gc": pos.get("golesEnContra", 0), "puntos": pos.get("puntos", 0)
                                    }
                                    supabase.table("posiciones").upsert(pos_data).execute()

                                # Sync Goleadores
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
