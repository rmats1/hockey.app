/// Modelo de club compatible con el JSON de la API AHBA.
/// La clase [ClubModel] es el tipo principal; [Club] es un alias
/// de compatibilidad para el código existente.
class ClubModel {
  final String clubId;
  final String nombre;
  final String? escudoUrl;

  const ClubModel({
    required this.clubId,
    required this.nombre,
    this.escudoUrl,
  });

  /// Compatibilidad: algunos archivos acceden a club.id
  String get id => clubId;

  /// Compatibilidad: algunos archivos acceden a club.nombreCorto
  String get nombreCorto =>
      nombre.length > 14 ? nombre.substring(0, 14).trim() : nombre;

  factory ClubModel.fromJson(Map<String, dynamic> json) {
    final rawNombre = json['club'] as String? ?? json['nombre'] as String? ?? '';
    return ClubModel(
      clubId: json['clubId']?.toString() ?? json['id']?.toString() ?? '',
      nombre: rawNombre.trim(),
      escudoUrl: json['clubEscudo'] as String? ?? json['escudoImagePath'] as String?,
    );
  }

  Map<String, dynamic> toJson() => {
        'clubId': clubId,
        'club': nombre,
        'clubEscudo': escudoUrl,
      };

  @override
  String toString() => nombre;

  @override
  bool operator ==(Object other) =>
      identical(this, other) || other is ClubModel && clubId == other.clubId;

  @override
  int get hashCode => clubId.hashCode;
}

/// Alias de compatibilidad con el código existente.
/// Acepta los parámetros legacy que usaban los modelos anteriores
/// (id, nombre, nombreCorto, division).
class Club extends ClubModel {
  /// Constructor legacy: Club(id: ..., nombre: ..., nombreCorto: ..., division: ...)
  Club({
    /// Acepta tanto 'id' (legacy) como 'clubId' (nuevo)
    String? id,
    String? clubId,
    required super.nombre,
    // Los campos legacy se ignoran en la implementación nueva
    // pero se aceptan para no romper el código existente
    String? nombreCorto,
    String? division,
    super.escudoUrl,
  }) : super(
          clubId: clubId ?? id ?? '',
        );

  factory Club.fromJson(Map<String, dynamic> json) {
    final base = ClubModel.fromJson(json);
    return Club(clubId: base.clubId, nombre: base.nombre, escudoUrl: base.escudoUrl);
  }
}
