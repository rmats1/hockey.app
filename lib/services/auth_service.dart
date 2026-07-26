import 'package:supabase_flutter/supabase_flutter.dart';
import '../models/user_model.dart';

class AuthService {
  final _supabase = Supabase.instance.client;

  // ========== SESIÓN ACTUAL ==========

  Future<UserModel?> getCurrentUser() async {
    try {
      final session = _supabase.auth.currentSession;
      if (session == null) return null;
      
      final response = await _supabase
          .from('profiles')
          .select()
          .eq('id', session.user.id)
          .single();
      
      return UserModel.fromMap(response);
    } catch (e) {
      return null;
    }
  }

  Future<bool> isLoggedIn() async {
    return _supabase.auth.currentSession != null;
  }

  Future<void> logout() async {
    await _supabase.auth.signOut();
  }

  // ========== REGISTRO Y LOGIN REAL CON SUPABASE ==========

  Future<String?> signUp({
    required String email,
    required String password,
    required UserModel userMetadata,
  }) async {
    try {
      final response = await _supabase.auth.signUp(
        email: email,
        password: password,
      );

      if (response.user != null) {
        // Guardar el perfil en la tabla 'profiles'
        final profileData = userMetadata.toMap();
        profileData['id'] = response.user!.id; // Sobrescribir con el ID real de Auth
        
        await _supabase.from('profiles').insert(profileData);
        return null; // Éxito
      }
      return 'Error desconocido al registrar';
    } on AuthException catch (e) {
      return e.message;
    } catch (e) {
      return e.toString();
    }
  }

  Future<String?> signIn({
    required String email,
    required String password,
  }) async {
    try {
      await _supabase.auth.signInWithPassword(
        email: email,
        password: password,
      );
      return null; // Éxito
    } on AuthException catch (e) {
      return e.message;
    } catch (e) {
      return e.toString();
    }
  }

  /// Método temporal para guardar el usuario actual en memoria si fuera necesario
  /// En Supabase v2, esto se maneja automáticamente por la sesión persistente.
  Future<void> saveCurrentUser(UserModel user) async {
    // No es estrictamente necesario con Supabase Auth ya que la sesión persiste sola,
    // pero lo mantenemos por compatibilidad con main.dart si se usa para tests.
  }
}
