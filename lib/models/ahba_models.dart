/// Resumen de un torneo (para listas y búsquedas)
class TorneoResumen {
  final String id;
  final String nombre;
  final String rama; // 'F' = Femenino, 'M' = Masculino
  final String categoria;
  final String division;
  final String temporada;

  const TorneoResumen({
    required this.id,
    required this.nombre,
    required this.rama,
    required this.categoria,
    required this.division,
    required this.temporada,
  });

  factory TorneoResumen.fromJson(Map<String, dynamic> json) {
    final ramaKey = json['ramaKey'] as String?;
    final ramaNombre = json['rama'] as String? ?? '';
    return TorneoResumen(
      id: json['id']?.toString() ?? '',
      nombre: (json['nombre'] as String? ?? '').trim(),
      rama: ramaKey ?? (ramaNombre.startsWith('M') ? 'M' : 'F'),
      categoria: (json['categoria'] as String? ?? '').trim(),
      division: (json['division'] as String? ?? '').trim(),
      temporada: (json['temporada'] as String? ?? '').trim(),
    );
  }

  String get ramaLabel => rama == 'M' ? 'Masculino' : 'Femenino';

  @override
  String toString() => nombre;
}

/// Partido dentro de un torneo
class PartidoAHBA {
  final String id;
  final String nombreLocal;
  final String nombreVisitante;
  final String? escudoLocal;
  final String? escudoVisitante;
  final int? golesLocal;
  final int? golesVisitante;
  final String? horario;
  final String numeroFecha;
  final bool jugado;

  const PartidoAHBA({
    required this.id,
    required this.nombreLocal,
    required this.nombreVisitante,
    this.escudoLocal,
    this.escudoVisitante,
    this.golesLocal,
    this.golesVisitante,
    this.horario,
    required this.numeroFecha,
    required this.jugado,
  });

  factory PartidoAHBA.fromJson(Map<String, dynamic> json) {
    int? pG(dynamic v) => v == null || v == '' ? null : int.tryParse(v.toString());
    return PartidoAHBA(
      id: json['id']?.toString() ?? '',
      nombreLocal: (json['nombreLocal'] ?? json['equipo_local'] ?? '').toString().trim(),
      nombreVisitante: (json['nombreVisitante'] ?? json['equipo_visita'] ?? '').toString().trim(),
      escudoLocal: json['escudoImagePathLocal'] ?? json['escudo_local'],
      escudoVisitante: json['escudoImagePathVisitante'] ?? json['escudo_visita'],
      golesLocal: pG(json['golesLocal'] ?? json['goles_local']),
      golesVisitante: pG(json['golesVisitante'] ?? json['goles_visita']),
      horario: json['horario'] ?? json['fecha'],
      numeroFecha: (json['numeroFecha'] ?? json['numero_fecha'] ?? '1').toString(),
      jugado: json['played'] == true || json['jugado'] == true,
    );
  }
}

/// Posición en la tabla
class PosicionAHBA {
  final int puesto;
  final String clubNombre;
  final String? escudoUrl;
  final int puntos;

  const PosicionAHBA({
    required this.puesto,
    required this.clubNombre,
    this.escudoUrl,
    required this.puntos,
  });

  factory PosicionAHBA.fromJson(Map<String, dynamic> json) {
    return PosicionAHBA(
      puesto: int.tryParse((json['puesto'] ?? json['posicion'] ?? '0').toString()) ?? 0,
      clubNombre: (json['club'] ?? json['equipo'] ?? '').toString().trim(),
      escudoUrl: json['club_escudo'] ?? json['escudo'],
      puntos: int.tryParse((json['puntos'] ?? '0').toString()) ?? 0,
    );
  }
}

/// Goleador (Corregido para coincidir con la API Real)
class GoleadorAHBA {
  final String nombreCompleto;
  final String clubNombre;
  final String? fotoUrl;
  final int goles;

  const GoleadorAHBA({
    required this.nombreCompleto,
    required this.clubNombre,
    this.fotoUrl,
    required this.goles,
  });

  factory GoleadorAHBA.fromJson(Map<String, dynamic> json) {
    final nombre = json['nombre'] ?? json['jug_nombre'] ?? json['jugador_nombre'] ?? '';
    final apellido = json['apellido'] ?? json['jug_apellido'] ?? '';
    final club = json['clubNombre'] ?? json['club_nombre'] ?? json['club'] ?? '';
    
    return GoleadorAHBA(
      nombreCompleto: '$nombre $apellido'.trim().toUpperCase(),
      clubNombre: club.toString().trim(),
      fotoUrl: json['fotoUrl'] ?? json['foto'] ?? json['jug_foto'] ?? json['foto_url'],
      goles: int.tryParse((json['goles'] ?? json['goals'] ?? '0').toString()) ?? 0,
    );
  }
}

/// Torneo completo
class TorneoCompleto {
  final String id;
  final List<PartidoAHBA> todosLosPartidos;
  final List<PosicionAHBA> tablaGeneral;
  final List<GoleadorAHBA> goleadores;

  const TorneoCompleto({
    required this.id,
    required this.todosLosPartidos,
    required this.tablaGeneral,
    required this.goleadores,
  });

  factory TorneoCompleto.fromJson(Map<String, dynamic> json) {
    final List<dynamic> rawGoleadores = json['goleadores'] as List? ?? [];
    final List<PartidoAHBA> allPartidos = [];
    final List<PosicionAHBA> firstTabla = [];

    if (json['fases'] is List) {
      for (var fase in json['fases']) {
        if (fase['zonas'] is List) {
          for (var zona in fase['zonas']) {
            if (zona['partidos'] is List) {
              for (var p in zona['partidos']) {
                allPartidos.add(PartidoAHBA.fromJson(p));
              }
            }
            if (firstTabla.isEmpty && zona['tabla'] is List) {
              for (var t in zona['tabla']) {
                firstTabla.add(PosicionAHBA.fromJson(t));
              }
            }
          }
        }
      }
    }

    return TorneoCompleto(
      id: json['id']?.toString() ?? '',
      todosLosPartidos: allPartidos,
      tablaGeneral: firstTabla,
      goleadores: rawGoleadores.map((g) => GoleadorAHBA.fromJson(g)).toList(),
    );
  }
}
