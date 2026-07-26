import '../models/jugador_model.dart';
import '../models/club_model.dart';
import 'clubes_ahba.dart';

class JugadoresData {
  static Club _getClub(String id) {
    return ClubesAhba.clubes.firstWhere((c) => c.id == id);
  }

  static final List<JugadorModel> jugadores = [
    // Belgrano Athletic Damas
    JugadorModel(
      id: 'j1', nombre: 'María González',
      club: _getClub('4'), categoria: 'Damas', division: '1ra',
      numeroCamiseta: 9, posicion: 'Delantera', goles: 12, partidos: 6,
      fechaNacimiento: DateTime(1998, 3, 15),
    ),
    JugadorModel(
      id: 'j2', nombre: 'Camila López',
      club: _getClub('4'), categoria: 'Damas', division: '1ra',
      numeroCamiseta: 7, posicion: 'Mediocampista', goles: 7, partidos: 6,
      fechaNacimiento: DateTime(2000, 7, 22),
    ),
    JugadorModel(
      id: 'j3', nombre: 'Catalina Méndez',
      club: _getClub('4'), categoria: 'Damas', division: '1ra',
      numeroCamiseta: 5, posicion: 'Defensora', goles: 3, partidos: 6,
      fechaNacimiento: DateTime(1999, 11, 8),
    ),
    // Alumni Damas
    JugadorModel(
      id: 'j4', nombre: 'Lucía Fernández',
      club: _getClub('1'), categoria: 'Damas', division: '1ra',
      numeroCamiseta: 10, posicion: 'Delantera', goles: 10, partidos: 6,
      fechaNacimiento: DateTime(1997, 5, 12),
    ),
    JugadorModel(
      id: 'j5', nombre: 'Isabella Torres',
      club: _getClub('1'), categoria: 'Damas', division: '1ra',
      numeroCamiseta: 8, posicion: 'Mediocampista', goles: 3, partidos: 6,
      fechaNacimiento: DateTime(2001, 9, 3),
    ),
    // River Damas
    JugadorModel(
      id: 'j6', nombre: 'Sofía Martínez',
      club: _getClub('92'), categoria: 'Damas', division: '1ra',
      numeroCamiseta: 11, posicion: 'Delantera', goles: 8, partidos: 6,
      fechaNacimiento: DateTime(1998, 1, 28),
    ),
    // Belgrano Athletic Caballeros
    JugadorModel(
      id: 'j20', nombre: 'Juan Pérez',
      club: _getClub('4'), categoria: 'Caballeros', division: '1ra',
      numeroCamiseta: 9, posicion: 'Delantero', goles: 15, partidos: 6,
      fechaNacimiento: DateTime(1995, 4, 18),
    ),
    JugadorModel(
      id: 'j21', nombre: 'Federico Silva',
      club: _getClub('4'), categoria: 'Caballeros', division: '1ra',
      numeroCamiseta: 6, posicion: 'Mediocampista', goles: 5, partidos: 6,
      fechaNacimiento: DateTime(1996, 8, 30),
    ),
    // CUBA Caballeros
    JugadorModel(
      id: 'j22', nombre: 'Martín Suárez',
      club: _getClub('42'), categoria: 'Caballeros', division: '1ra',
      numeroCamiseta: 10, posicion: 'Delantero', goles: 11, partidos: 6,
      fechaNacimiento: DateTime(1994, 12, 5),
    ),
    JugadorModel(
      id: 'j23', nombre: 'Tomás González',
      club: _getClub('42'), categoria: 'Caballeros', division: '1ra',
      numeroCamiseta: 4, posicion: 'Defensor', goles: 4, partidos: 6,
      fechaNacimiento: DateTime(1997, 2, 14),
    ),
    // SIC Caballeros
    JugadorModel(
      id: 'j24', nombre: 'Lucas Díaz',
      club: _getClub('103'), categoria: 'Caballeros', division: '1ra',
      numeroCamiseta: 8, posicion: 'Mediocampista', goles: 9, partidos: 6,
      fechaNacimiento: DateTime(1995, 6, 20),
    ),
    JugadorModel(
      id: 'j25', nombre: 'Nicolás Fernández',
      club: _getClub('103'), categoria: 'Caballeros', division: '1ra',
      numeroCamiseta: 2, posicion: 'Defensor', goles: 4, partidos: 6,
      fechaNacimiento: DateTime(1996, 10, 11),
    ),
  ];

  // Buscar jugadores por nombre
  static List<JugadorModel> buscarPorNombre(String query) {
    if (query.isEmpty) return jugadores;
    return jugadores
        .where((j) => j.nombre.toLowerCase().contains(query.toLowerCase()))
        .toList();
  }

  // Jugadores de un club
  static List<JugadorModel> getJugadoresPorClub(String clubId) {
    return jugadores.where((j) => j.club.id == clubId).toList();
  }

  // Jugadores por categoría
  static List<JugadorModel> getJugadoresPorCategoria(String categoria) {
    return jugadores.where((j) => j.categoria == categoria).toList();
  }
}
