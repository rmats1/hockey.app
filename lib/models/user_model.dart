import 'club_model.dart';

class UserModel {
  final String id;
  final String email;
  final String nombre;
  final String userType; // 'jugador' o 'cuerpo_tecnico'
  final String rama; // 'Damas' o 'Caballeros'
  final String categoria; // '1ra', 'Intermedia', '5ta', etc.
  final String? division; // 'A', 'B', 'C', etc.
  final Club club;
  final int? numeroCamiseta;
  final String? posicion;
  final String? rolCuerpoTecnico;
  final DateTime? fechaNacimiento;
  final DateTime fechaRegistro;
  final String? fotoPath;

  UserModel({
    required this.id,
    required this.email,
    required this.nombre,
    required this.userType,
    required this.rama,
    required this.categoria,
    this.division,
    required this.club,
    this.numeroCamiseta,
    this.posicion,
    this.rolCuerpoTecnico,
    this.fechaNacimiento,
    required this.fechaRegistro,
    this.fotoPath,
  });

  Map<String, dynamic> toMap() {
    return {
      'id': id,
      'email': email,
      'nombre': nombre,
      'userType': userType,
      'rama': rama,
      'categoria': categoria,
      'division': division,
      'clubId': club.id,
      'clubNombre': club.nombre,
      'clubNombreCorto': club.nombreCorto,
      'clubEscudo': club.escudoUrl,
      'numeroCamiseta': numeroCamiseta,
      'posicion': posicion,
      'rolCuerpoTecnico': rolCuerpoTecnico,
      'fechaNacimiento': fechaNacimiento?.toIso8601String(),
      'fechaRegistro': fechaRegistro.toIso8601String(),
      'fotoPath': fotoPath,
    };
  }

  factory UserModel.fromMap(Map<String, dynamic> map) {
    return UserModel(
      id: map['id'],
      email: map['email'],
      nombre: map['nombre'],
      userType: map['userType'],
      rama: map['rama'] ?? (map['categoria'] == 'Damas' || map['categoria'] == 'Caballeros' ? map['categoria'] : 'Damas'),
      categoria: map['categoria'] ?? map['division'] ?? '1ra',
      division: map['division'],
      club: Club(
        id: map['clubId'],
        nombre: map['clubNombre'],
        nombreCorto: map['clubNombreCorto'],
        escudoUrl: map['clubEscudo'],
      ),
      numeroCamiseta: map['numeroCamiseta'],
      posicion: map['posicion'],
      rolCuerpoTecnico: map['rolCuerpoTecnico'],
      fechaNacimiento: map['fechaNacimiento'] != null
          ? DateTime.parse(map['fechaNacimiento'])
          : null,
      fechaRegistro: DateTime.parse(map['fechaRegistro'] ?? DateTime.now().toIso8601String()),
      fotoPath: map['fotoPath'],
    );
  }

  UserModel copyWith({
    String? fotoPath,
  }) {
    return UserModel(
      id: id,
      email: email,
      nombre: nombre,
      userType: userType,
      rama: rama,
      categoria: categoria,
      division: division,
      club: club,
      numeroCamiseta: numeroCamiseta,
      posicion: posicion,
      rolCuerpoTecnico: rolCuerpoTecnico,
      fechaNacimiento: fechaNacimiento,
      fechaRegistro: fechaRegistro,
      fotoPath: fotoPath ?? this.fotoPath,
    );
  }
}
