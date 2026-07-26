class PartidoModel {
  final String id;
  final String torneoId;
  final String clubLocalId;
  final String clubVisitanteId;
  final int golesLocal;
  final int golesVisitante;
  final DateTime fecha;
  final String hora;
  final String cancha;
  final String estado; // 'programado', 'en_curso', 'finalizado', 'suspendido'
  final String? jornada; // 'Fecha 1', 'Semifinal', etc.

  PartidoModel({
    required this.id,
    required this.torneoId,
    required this.clubLocalId,
    required this.clubVisitanteId,
    required this.golesLocal,
    required this.golesVisitante,
    required this.fecha,
    required this.hora,
    required this.cancha,
    required this.estado,
    this.jornada,
  });

  // Método para saber si es empate
  bool get esEmpate => golesLocal == golesVisitante;

  // Método para saber el ganador
  String? get ganadorId {
    if (golesLocal > golesVisitante) return clubLocalId;
    if (golesVisitante > golesLocal) return clubVisitanteId;
    return null;
  }
}
