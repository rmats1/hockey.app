import 'package:supabase_flutter/supabase_flutter.dart';

class SupabaseService {
  static final SupabaseService instance = SupabaseService._();
  SupabaseService._();

  final _supabase = Supabase.instance.client;

  Future<List<dynamic>> getPosiciones(String torneoId) async {
    try {
      final data = await _supabase
          .from('posiciones')
          .select()
          .eq('torneo_id', torneoId)
          .order('posicion', ascending: true);
      return data as List<dynamic>;
    } catch (e) {
      return [];
    }
  }

  Future<List<dynamic>> getPartidos(String torneoId) async {
    try {
      final data = await _supabase
          .from('partidos')
          .select()
          .eq('torneo_id', torneoId)
          .order('numero_fecha', ascending: true);
      return data as List<dynamic>;
    } catch (e) {
      return [];
    }
  }

  Future<List<dynamic>> getGoleadores(String torneoId) async {
    try {
      // Intentar una búsqueda más flexible para asegurar que traemos datos
      final data = await _supabase
          .from('goleadores')
          .select()
          .eq('torneo_id', torneoId)
          .order('goles', ascending: false);
          
      if ((data as List).isEmpty) {
        // Reintento con ID sin ceros por si acaso
        final shortId = int.tryParse(torneoId)?.toString();
        if (shortId != null && shortId != torneoId) {
           final retry = await _supabase
            .from('goleadores')
            .select()
            .eq('torneo_id', shortId)
            .order('goles', ascending: false);
           return retry as List<dynamic>;
        }
      }
      return data as List<dynamic>;
    } catch (e) {
      return [];
    }
  }

  Future<List<dynamic>> getNoticias() async {
    try {
      final data = await _supabase
          .from('noticias')
          .select()
          .order('fecha_publicacion', ascending: false)
          .limit(20);
      return data as List<dynamic>;
    } catch (e) {
      return [];
    }
  }

  /// Específico para el Dashboard del perfil
  Future<Map<String, dynamic>?> getMiResumen(String clubNombre, String rama, String categoria, String? division) async {
    try {
      // 1. Buscar mi posición
      final pos = await _supabase
          .from('posiciones')
          .select()
          .ilike('equipo', '%$clubNombre%')
          .eq('genero', rama == 'Damas' ? 'Femenino' : 'Masculino')
          .ilike('categoria', '%$categoria%')
          .limit(1)
          .maybeSingle();

      if (pos == null) return null;

      final tId = pos['torneo_id'];

      // 2. Buscar mis partidos
      final partidos = await _supabase
          .from('partidos')
          .select()
          .eq('torneo_id', tId)
          .or('equipo_local.ilike.%$clubNombre%,equipo_visita.ilike.%$clubNombre%')
          .order('numero_fecha', ascending: true);

      return {
        'posicion': pos,
        'partidos': partidos,
        'torneo_nombre': pos['torneo_nombre']
      };
    } catch (e) {
      return null;
    }
  }
}
