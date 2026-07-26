class TorneoModel {
  final String id;
  final String nombre;
  final String categoria; // 'Damas' o 'Caballeros'
  final String division; // '1ra', 'Intermedia', etc.
  final String temporada; // '2025'
  final DateTime fechaInicio;
  final DateTime fechaFin;
  final String estado; // 'en_curso', 'finalizado', 'proximo'
  final List<String> clubesParticipantes; // IDs de clubes
  final String? descripcion;
  final String? imagenUrl;

  TorneoModel({
    required this.id,
    required this.nombre,
    required this.categoria,
    required this.division,
    required this.temporada,
    required this.fechaInicio,
    required this.fechaFin,
    required this.estado,
    required this.clubesParticipantes,
    this.descripcion,
    this.imagenUrl,
  });
}
