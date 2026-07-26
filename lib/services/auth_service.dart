import 'package:flutter/foundation.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import '../models/user_model.dart';
import 'dart:convert';

class AuthService {
  // Configuración de almacenamiento seguro
  static const _storage = FlutterSecureStorage(
    aOptions: AndroidOptions(encryptedSharedPreferences: true),
    iOptions: IOSOptions(accessibility: KeychainAccessibility.first_unlock),
  );

  static const String _userKey = 'secure_current_user';
  static const String _isLoggedInKey = 'secure_is_logged_in';
  static const String _usersKey = 'secure_registered_users';

  // ========== SESIÓN ACTUAL ==========

  Future<void> saveCurrentUser(UserModel user) async {
    try {
      final userJson = json.encode(user.toMap());
      await _storage.write(key: _userKey, value: userJson);
      await _storage.write(key: _isLoggedInKey, value: 'true');
    } catch (e) {
      debugPrint('Security Error: Failed to save user securely - $e');
    }
  }

  Future<UserModel?> getCurrentUser() async {
    try {
      final isLoggedIn = await _storage.read(key: _isLoggedInKey);
      if (isLoggedIn != 'true') return null;
      
      final userString = await _storage.read(key: _userKey);
      if (userString == null) return null;
      
      return UserModel.fromMap(json.decode(userString));
    } catch (e) {
      debugPrint('Security Error: Failed to read user securely - $e');
      return null;
    }
  }

  Future<bool> isLoggedIn() async {
    final isLoggedIn = await _storage.read(key: _isLoggedInKey);
    return isLoggedIn == 'true';
  }

  Future<void> logout() async {
    await _storage.delete(key: _userKey);
    await _storage.write(key: _isLoggedInKey, value: 'false');
  }

  // Actualizar foto del usuario de forma segura
  Future<void> updateUserPhoto(String fotoPath) async {
    final user = await getCurrentUser();
    if (user == null) return;
    
    final updatedUser = user.copyWith(fotoPath: fotoPath);
    await saveCurrentUser(updatedUser);
    
    // Actualizar en la base de datos "local" segura
    final users = await getAllUsers();
    final index = users.indexWhere((u) => u.id == user.id);
    if (index != -1) {
      users[index] = updatedUser;
      await _saveAllUsers(users);
    }
  }

  // ========== REGISTRO DE USUARIOS SEGURO ==========

  Future<bool> registerUser(UserModel user) async {
    final users = await getAllUsers();
    
    if (users.any((u) => u.email == user.email)) {
      return false;
    }
    
    users.add(user);
    await _saveAllUsers(users);
    return true;
  }

  Future<List<UserModel>> getAllUsers() async {
    try {
      final usersString = await _storage.read(key: _usersKey);
      if (usersString == null) return [];
      
      final usersList = json.decode(usersString) as List;
      return usersList
          .map((userMap) => UserModel.fromMap(userMap))
          .toList();
    } catch (e) {
      return [];
    }
  }

  Future<void> _saveAllUsers(List<UserModel> users) async {
    final usersJson = users.map((u) => u.toMap()).toList();
    await _storage.write(key: _usersKey, value: json.encode(usersJson));
  }

  Future<UserModel?> findUserByEmail(String email) async {
    final users = await getAllUsers();
    try {
      return users.firstWhere((u) => u.email == email);
    } catch (e) {
      return null;
    }
  }
}
