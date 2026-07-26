import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'dart:convert';

class FavoritosService {
  static const _storage = FlutterSecureStorage(
    aOptions: AndroidOptions(encryptedSharedPreferences: true),
    iOptions: IOSOptions(accessibility: KeychainAccessibility.first_unlock),
  );

  static const String _key = 'secure_clubes_favoritos';

  Future<List<String>> getFavoritos(String userId) async {
    try {
      final data = await _storage.read(key: '${_key}_$userId');
      if (data == null) return [];
      return List<String>.from(json.decode(data));
    } catch (e) {
      return [];
    }
  }

  Future<void> addFavorito(String userId, String clubId) async {
    final favoritos = await getFavoritos(userId);
    if (!favoritos.contains(clubId)) {
      favoritos.add(clubId);
      await _storage.write(key: '${_key}_$userId', value: json.encode(favoritos));
    }
  }

  Future<void> removeFavorito(String userId, String clubId) async {
    final favoritos = await getFavoritos(userId);
    favoritos.remove(clubId);
    await _storage.write(key: '${_key}_$userId', value: json.encode(favoritos));
  }

  Future<bool> isFavorito(String userId, String clubId) async {
    final favoritos = await getFavoritos(userId);
    return favoritos.contains(clubId);
  }
}
