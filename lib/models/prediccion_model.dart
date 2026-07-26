class PrediccionModel {
  final String id;
  final String partidoId;
  final String userId;
  final String userName;
  final int golesLocal;
  final int golesVisitante;
  final DateTime fecha;

  PrediccionModel({
    required this.id,
    required this.partidoId,
    required this.userId,
    required this.userName,
    required this.golesLocal,
    required this.golesVisitante,
    required this.fecha,
  });
}
