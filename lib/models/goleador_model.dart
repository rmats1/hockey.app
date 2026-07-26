class GoleadorModel {
  final int posicion;
  final String jugadorId;
  final String jugadorNombre;
  final String clubId;
  final int goles;
  final String? division;

  GoleadorModel({
    required this.posicion,
    required this.jugadorId,
    required this.jugadorNombre,
    required this.clubId,
    required this.goles,
    this.division,
  });
}
