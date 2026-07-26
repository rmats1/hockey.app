import '../models/partido_model.dart';

class PartidosData {
  // Partidos del Torneo Apertura Damas 1ra (Fecha 1)
  static final List<PartidoModel> partidosAperturaDamas1ra = [
    PartidoModel(
      id: 'p1',
      torneoId: 't1',
      clubLocalId: '1', // Alumni
      clubVisitanteId: '4', // Belgrano
      golesLocal: 3,
      golesVisitante: 1,
      fecha: DateTime(2025, 3, 8),
      hora: '15:30',
      cancha: 'Club Alumni',
      estado: 'finalizado',
      jornada: 'Fecha 1',
    ),
    PartidoModel(
      id: 'p2',
      torneoId: 't1',
      clubLocalId: '92', // River
      clubVisitanteId: '42', // CUBA
      golesLocal: 2,
      golesVisitante: 2,
      fecha: DateTime(2025, 3, 8),
      hora: '16:00',
      cancha: 'River Plate',
      estado: 'finalizado',
      jornada: 'Fecha 1',
    ),
    PartidoModel(
      id: 'p3',
      torneoId: 't1',
      clubLocalId: '103', // SIC
      clubVisitanteId: '10', // Banco Nación
      golesLocal: 4,
      golesVisitante: 0,
      fecha: DateTime(2025, 3, 8),
      hora: '17:00',
      cancha: 'SIC',
      estado: 'finalizado',
      jornada: 'Fecha 1',
    ),
    PartidoModel(
      id: 'p4',
      torneoId: 't1',
      clubLocalId: '33', // Matreros
      clubVisitanteId: '1', // Alumni
      golesLocal: 0,
      golesVisitante: 5,
      fecha: DateTime(2025, 3, 15),
      hora: '15:30',
      cancha: 'Los Matreros',
      estado: 'finalizado',
      jornada: 'Fecha 2',
    ),
    PartidoModel(
      id: 'p5',
      torneoId: 't1',
      clubLocalId: '4', // Belgrano
      clubVisitanteId: '92', // River
      golesLocal: 3,
      golesVisitante: 1,
      fecha: DateTime(2025, 3, 15),
      hora: '16:00',
      cancha: 'Belgrano Athletic',
      estado: 'finalizado',
      jornada: 'Fecha 2',
    ),
    // Próximos partidos
    PartidoModel(
      id: 'p6',
      torneoId: 't1',
      clubLocalId: '4', // Belgrano
      clubVisitanteId: '103', // SIC
      golesLocal: 0,
      golesVisitante: 0,
      fecha: DateTime(2025, 6, 28),
      hora: '15:30',
      cancha: 'Belgrano Athletic',
      estado: 'programado',
      jornada: 'Fecha 7',
    ),
    PartidoModel(
      id: 'p7',
      torneoId: 't1',
      clubLocalId: '1', // Alumni
      clubVisitanteId: '42', // CUBA
      golesLocal: 0,
      golesVisitante: 0,
      fecha: DateTime(2025, 6, 28),
      hora: '16:00',
      cancha: 'Club Alumni',
      estado: 'programado',
      jornada: 'Fecha 7',
    ),
  ];

  // Partidos Caballeros 1ra
  static final List<PartidoModel> partidosAperturaCaballeros1ra = [
    PartidoModel(
      id: 'p8',
      torneoId: 't8',
      clubLocalId: '4', // Belgrano
      clubVisitanteId: '42', // CUBA
      golesLocal: 4,
      golesVisitante: 2,
      fecha: DateTime(2025, 3, 8),
      hora: '20:00',
      cancha: 'Belgrano Athletic',
      estado: 'finalizado',
      jornada: 'Fecha 1',
    ),
    PartidoModel(
      id: 'p9',
      torneoId: 't8',
      clubLocalId: '103', // SIC
      clubVisitanteId: '1', // Alumni
      golesLocal: 2,
      golesVisitante: 1,
      fecha: DateTime(2025, 3, 8),
      hora: '21:00',
      cancha: 'SIC',
      estado: 'finalizado',
      jornada: 'Fecha 1',
    ),
    PartidoModel(
      id: 'p10',
      torneoId: 't8',
      clubLocalId: '4', // Belgrano
      clubVisitanteId: '103', // SIC
      golesLocal: 5,
      golesVisitante: 1,
      fecha: DateTime(2025, 6, 28),
      hora: '20:00',
      cancha: 'Belgrano Athletic',
      estado: 'programado',
      jornada: 'Fecha 7',
    ),
  ];

  // Obtener partidos de un torneo
  static List<PartidoModel> getPartidos(String torneoId) {
    if (torneoId == 't1') return partidosAperturaDamas1ra;
    if (torneoId == 't8') return partidosAperturaCaballeros1ra;
    return [];
  }

  // Obtener próximos partidos de un club
  static List<PartidoModel> getProximosPartidos(String clubId) {
    final todos = [...partidosAperturaDamas1ra, ...partidosAperturaCaballeros1ra];
    return todos
        .where((p) =>
            (p.clubLocalId == clubId || p.clubVisitanteId == clubId) &&
            p.estado == 'programado')
        .toList();
  }

  // Obtener últimos resultados de un club
  static List<PartidoModel> getUltimosPartidos(String clubId) {
    final todos = [...partidosAperturaDamas1ra, ...partidosAperturaCaballeros1ra];
    return todos
        .where((p) =>
            (p.clubLocalId == clubId || p.clubVisitanteId == clubId) &&
            p.estado == 'finalizado')
        .toList()
      ..sort((a, b) => b.fecha.compareTo(a.fecha));
  }
}
