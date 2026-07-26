import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'dart:convert';

class TeamService {
  static const _storage = FlutterSecureStorage(
    aOptions: AndroidOptions(encryptedSharedPreferences: true),
  );

  static String _trainingKey(String clubId, String division) => 'secure_training_${clubId}_$division';
  static String _callUpKey(String clubId, String division) => 'secure_callup_${clubId}_$division';

  Future<void> saveTrainingPlan(String clubId, String division, String instruction) async {
    await _storage.write(key: _trainingKey(clubId, division), value: instruction);
  }

  Future<String?> getTrainingPlan(String clubId, String division) async {
    return _storage.read(key: _trainingKey(clubId, division));
  }

  Future<void> saveCallUpList(String clubId, String division, List<String> playerIds) async {
    await _storage.write(key: _callUpKey(clubId, division), value: json.encode(playerIds));
  }

  Future<List<String>> getCallUpList(String clubId, String division) async {
    final data = await _storage.read(key: _callUpKey(clubId, division));
    if (data == null) return [];
    return List<String>.from(json.decode(data));
  }

  Future<bool> isPlayerCalledUp(String clubId, String division, String playerId) async {
    final list = await getCallUpList(clubId, division);
    return list.contains(playerId);
  }
}
