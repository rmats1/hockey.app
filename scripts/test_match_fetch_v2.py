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

print("Detalle content keys:", detail.get("detalle", {}).keys())
fases = detail.get("fases", [])
print(f"Fases count: {len(fases)}")

if fases:
    fase = fases[0]
    print("Fase keys:", fase.keys())
    print("Fase todosLosPartidos count:", len(fase.get("todosLosPartidos", [])))
    print("Fase fechas count:", len(fase.get("fechas", [])))
    if fase.get("fechas"):
        print("Sample match from fase.fechas:", fase["fechas"][0].get("partidos", [])[0] if fase["fechas"][0].get("partidos") else "Empty")
