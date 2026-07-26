import 'dart:convert';
import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart' show rootBundle;
import 'package:http/http.dart' as http;
import 'package:encrypt/encrypt.dart' as enc;
import '../models/ahba_models.dart';
import '../models/club_model.dart';

/// Servicio central de datos - Portado de la lógica funcional de Supabase.
class DataService {
  DataService._();
  static final DataService instance = DataService._();

  static const String _apiBase = 'https://api.tournamenttracker.buenosaireshockey.ar';
  static const String _passphrase = 'uweoEVNeycw7CFBXtHNCy3nbJZmUPl0EosXGRrNDgdU=';
  
  static const Map<String, String> _apiHeaders = {
    'Accept': 'application/json',
    'User-Agent': 'Mozilla/5.0',
    'Referer': 'https://tournamenttracker.buenosaireshockey.ar',
    'Origin': 'https://tournamenttracker.buenosaireshockey.ar',
  };

  List<ClubModel>? _clubesCache;
  List<TorneoResumen>? _resumenCache;
  final Map<String, TorneoCompleto> _torneoCache = {};
  bool _initialized = false;

  Future<void> init() async {
    if (_initialized) return;
    try {
      await _loadClubes();
      await _loadResumenLocal();
      _initialized = true;
    } catch (_) {}
  }

  Future<void> _loadClubes() async {
    try {
      final raw = await rootBundle.loadString('assets/database/clubes.json');
      final list = json.decode(raw) as List<dynamic>;
      _clubesCache = list.map((e) => ClubModel.fromJson(e as Map<String, dynamic>)).toList();
    } catch (_) {}
  }

  Future<void> _loadResumenLocal() async {
    try {
      final raw = await rootBundle.loadString('assets/database/torneos_resumen.json');
      final list = json.decode(raw) as List<dynamic>;
      
      // Filtrar por 2026 y eliminar duplicados por ID
      final Map<String, TorneoResumen> uniqueMap = {};
      for (var e in list) {
        final t = TorneoResumen.fromJson(e as Map<String, dynamic>);
        if (t.temporada == '2026') {
          uniqueMap[t.id] = t;
        }
      }
      _resumenCache = uniqueMap.values.toList();
    } catch (_) {}
  }

  Future<List<ClubModel>> getClubes() async {
    if (_clubesCache == null) await _loadClubes();
    return _clubesCache ?? [];
  }

  Future<List<TorneoResumen>> getTorneosResumen() async {
    if (_resumenCache == null) await _loadResumenLocal();
    return _resumenCache ?? [];
  }

  Future<TorneoCompleto?> getTorneoCompleto(String torneoId) async {
    if (_torneoCache.containsKey(torneoId)) return _torneoCache[torneoId];
    return _downloadTorneo(torneoId);
  }

  Future<TorneoCompleto?> _downloadTorneo(String torneoId) async {
    try {
      final paddedId = torneoId.padLeft(8, '0');
      final url = '$_apiBase/torneos/$paddedId';
      
      final response = await http.get(Uri.parse(url), headers: _apiHeaders).timeout(const Duration(seconds: 15));

      if (response.statusCode != 200) return null;

      String body = response.body.trim();
      
      if (body.startsWith('"')) {
        try { body = json.decode(body); } catch (_) {}
      }

      Map<String, dynamic>? data;

      if (body.contains(':')) {
        final decrypted = await compute(_decryptInIsolate, {
          'encrypted': body,
          'passphrase': _passphrase,
        });
        data = json.decode(decrypted) as Map<String, dynamic>;
      } else {
        data = json.decode(body) as Map<String, dynamic>;
      }

      if (data != null) {
        data['id'] = torneoId;
        final torneo = TorneoCompleto.fromJson(data);
        _torneoCache[torneoId] = torneo;
        return torneo;
      }
    } catch (e) {
      debugPrint('[Scraper Error]: $e');
    }
    return null;
  }

  static String _decryptInIsolate(Map<String, String> args) {
    try {
      final encrypted = args['encrypted']!;
      final passphrase = args['passphrase']!;
      final parts = encrypted.split(':');
      if (parts.length != 2) return encrypted;

      Uint8List hexToBytes(String hex) {
        final bytes = <int>[];
        for (var i = 0; i < hex.length; i += 2) {
          bytes.add(int.parse(hex.substring(i, i + 2), radix: 16));
        }
        return Uint8List.fromList(bytes);
      }

      final key = enc.Key.fromBase64(passphrase);
      final iv = enc.IV(hexToBytes(parts[0]));
      final encrypter = enc.Encrypter(enc.AES(key, mode: enc.AESMode.ctr, padding: null));
      
      return encrypter.decrypt(enc.Encrypted(hexToBytes(parts[1])), iv: iv);
    } catch (e) {
      return '';
    }
  }
}
