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

print("Fetching context...")
context = fetch(f"{API_BASE_URL}/get-context")
s2026 = [s for s in context if "2026" in str(s.get("nombre", ""))]

for s in s2026:
    print(f"\nSeason: {s['nombre']} (ID: {s['id']})")
    for fed in s.get("federaciones", []):
        for rama in fed.get("ramas", []):
            for cat in rama.get("categorias", []):
                print(f" Checking: {rama['text']} - {cat['nombre']}")
                url = f"{API_BASE_URL}/torneos-x-division/{fed['id']}/{s['id']}/{rama['key']}/{cat['id']}"
                divs = fetch(url)
                for dg in divs:
                    for d in dg.get("divisiones", []):
                        for t in d.get("torneos", []):
                            print(f"  Torneo: {t['nombre']} (ID: {t['id']})")
                            # Check one tournament detail
                            detail = fetch(f"{API_BASE_URL}/torneos/{t['id']}")
                            pm = detail.get("proximosPartidos", [])
                            tp = detail.get("todosLosPartidos", [])
                            ap = detail.get("partidosAnteriores", [])
                            print(f"    - Todos: {len(tp)}, Prox: {len(pm)}, Ant: {len(ap)}")
                            # Limit to 1 for brevity
                            break
                        break
                    break
                break
            break
        break
