import requests, base64, json
from cryptography.hazmat.primitives.ciphers import Cipher, algorithms, modes
from cryptography.hazmat.backends import default_backend

PASSPHRASE = "uweoEVNeycw7CFBXtHNCy3nbJZmUPl0EosXGRrNDgdU="
API_BASE_URL = "https://api.tournamenttracker.buenosaireshockey.ar"

def decrypt(encrypted_text, passphrase):
    try:
        parts = encrypted_text.strip('"').split(":")
        iv = bytes.fromhex(parts[0])
        ciphertext = bytes.fromhex(parts[1])
        key = base64.b64decode(passphrase)
        cipher = Cipher(algorithms.AES(key), modes.CTR(iv), backend=default_backend())
        decryptor = cipher.decryptor()
        return (decryptor.update(ciphertext) + decryptor.finalize()).decode("utf-8")
    except Exception as e:
        return None

def fetch(url):
    r = requests.get(url)
    return json.loads(decrypt(r.text, PASSPHRASE))

t_id = "00000406"
detail = fetch(f"{API_BASE_URL}/torneos/{t_id}")

for fase in detail.get("fases", []):
    print(f"\nFase: {fase.get('nombre')}")
    for zona in fase.get("zonas", []):
        print(f"  Zona: {zona.get('nombre')}")
        print(f"  Zona keys: {zona.keys()}")
        tp = zona.get("todosLosPartidos", [])
        fechas = zona.get("fechas", [])
        tabla = zona.get("tablaGeneral", [])
        print(f"    todosLosPartidos: {len(tp)}")
        print(f"    fechas: {len(fechas)}")
        print(f"    tablaGeneral: {len(tabla)}")
        if fechas:
            print(f"    Sample match from fecha 1: {fechas[0].get('partidos', [])[0] if fechas[0].get('partidos') else 'Empty'}")
