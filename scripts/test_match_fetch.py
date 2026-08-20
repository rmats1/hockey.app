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
print(f"Fetching details for tournament {t_id}...")
detail = fetch(f"{API_BASE_URL}/torneos/{t_id}")

print("Keys in detail:", detail.keys())
pm = detail.get("proximosPartidos", [])
tp = detail.get("todosLosPartidos", [])
ap = detail.get("partidosAnteriores", [])
fechas = detail.get("fechas", [])

print(f"todosLosPartidos: {len(tp)}")
print(f"proximosPartidos: {len(pm)}")
print(f"partidosAnteriores: {len(ap)}")
print(f"fechas: {len(fechas)}")

if fechas:
    print("Sample from first fecha:")
    f1 = fechas[0]
    print(f"  Nombre: {f1.get('nombreFecha')}")
    print(f"  Partidos: {len(f1.get('partidos', []))}")
    if f1.get('partidos'):
        print(f"  Sample match: {f1['partidos'][0]}")
