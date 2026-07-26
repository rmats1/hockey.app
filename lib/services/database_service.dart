// Este archivo se puede eliminar o dejar vacío
// Toda la lógica de base de datos está en auth_service.dart
// Lo dejamos por si en el futuro queremos usar SQLite
import 'package:shared_preferences/shared_preferences.dart';
import '../models/user_model.dart';
import 'dart:convert';

class DatabaseService {
  static const String _usersKey = 'registered_users_db';

  // Obtener todos los usuarios
  Future<List<UserModel>> getAllUsers() async {
    final prefs = await SharedPreferences.getInstance();
    final usersString = prefs.getString(_usersKey);
    
    if (usersString == null) return [];
    
    final usersList = json.decode(usersString) as List;
    return usersList
        .map((userMap) => UserModel.fromMap(userMap))
        .toList();
  }
}
