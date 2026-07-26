class NovedadModel {
  final String id;
  final String titulo;
  final String resumen;
  final String contenido;
  final String categoria; // 'torneo', 'club', 'seleccion', 'general'
  final String? imagenUrl;
  final DateTime fechaPublicacion;
  final String autor;
  final bool destacada;

  NovedadModel({
    required this.id,
    required this.titulo,
    required this.resumen,
    required this.contenido,
    required this.categoria,
    required this.fechaPublicacion,
    required this.autor,
    this.imagenUrl,
    this.destacada = false,
  });
}
