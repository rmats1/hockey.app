import 'club_model.dart';

class JugadorModel {
  final String id;
  final String nombre;
  final Club club;
  final String categoria; // 'Damas' o 'Caballeros'
  final String division;
  final int numeroCamiseta;
  final String posicion;
  final int goles;
  final int partidos;
  final DateTime fechaNacimiento;
  final String? fotoPath;

  JugadorModel({
    required this.id,
    required this.nombre,
    required this.club,
    required this.categoria,
    required this.division,
    required this.numeroCamiseta,
    required this.posicion,
    required this.goles,
    required this.partidos,
    required this.fechaNacimiento,
    this.fotoPath,
  });
}
