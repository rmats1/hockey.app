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
      'user_type': userType,
      'rama': rama,
      'categoria': categoria,
      'division': division,
      'club_id': club.id,
      'club_nombre': club.nombre,
      'club_escudo': club.escudoUrl,
      'numero_camiseta': numeroCamiseta,
      'posicion': posicion,
      'rol_cuerpo_tecnico': rolCuerpoTecnico,
      'fecha_nacimiento': fechaNacimiento?.toIso8601String(),
      'fecha_registro': fechaRegistro.toIso8601String(),
      'foto_url': fotoPath,
    };
  }

  factory UserModel.fromMap(Map<String, dynamic> map) {
    return UserModel(
      id: map['id'],
      email: map['email'],
      nombre: map['nombre'],
      userType: map['user_type'] ?? map['userType'] ?? 'jugador',
      rama: map['rama'] ?? 'Damas',
      categoria: map['categoria'] ?? '1ra',
      division: map['division'],
      club: Club(
        id: map['club_id'] ?? map['clubId'] ?? '0',
        nombre: map['club_nombre'] ?? map['clubNombre'] ?? 'Sin Club',
        escudoUrl: map['club_escudo'] ?? map['clubEscudo'],
      ),
      numeroCamiseta: map['numero_camiseta'] ?? map['numeroCamiseta'],
      posicion: map['posicion'],
      rolCuerpoTecnico: map['rol_cuerpo_tecnico'] ?? map['rolCuerpoTecnico'],
      fechaNacimiento: map['fecha_nacimiento'] != null
          ? DateTime.parse(map['fecha_nacimiento'])
          : (map['fechaNacimiento'] != null ? DateTime.parse(map['fechaNacimiento']) : null),
      fechaRegistro: DateTime.parse(map['fecha_registro'] ?? map['fechaRegistro'] ?? DateTime.now().toIso8601String()),
      fotoPath: map['foto_url'] ?? map['fotoPath'],
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
