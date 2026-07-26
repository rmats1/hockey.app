class ComentarioModel {
  final String id;
  final String partidoId;
  final String userId;
  final String userName;
  final String texto;
  final DateTime fecha;
  final int likes;

  ComentarioModel({
    required this.id,
    required this.partidoId,
    required this.userId,
    required this.userName,
    required this.texto,
    required this.fecha,
    this.likes = 0,
  });
}
