import '../models/comentario_model.dart';

class ComentariosData {
  static final List<ComentarioModel> comentarios = [
    ComentarioModel(
      id: 'c1', partidoId: 'p1', userId: 'j1', userName: 'María González',
      texto: '¡Increíble partido! Belgrano jugó muy bien pero Alumni se llevó la victoria 🏑',
      fecha: DateTime(2025, 3, 8, 16, 30), likes: 12,
    ),
    ComentarioModel(
      id: 'c2', partidoId: 'p1', userId: 'j4', userName: 'Lucía Fernández',
      texto: 'Gracias al equipo por el esfuerzo. ¡Vamos por más! 💪',
      fecha: DateTime(2025, 3, 8, 17, 0), likes: 8,
    ),
    ComentarioModel(
      id: 'c3', partidoId: 'p2', userId: 'j6', userName: 'Sofía Martínez',
      texto: 'Empate justo. Cuervos y Millonarios siempre dan batalla 🔥',
      fecha: DateTime(2025, 3, 8, 18, 0), likes: 15,
    ),
    ComentarioModel(
      id: 'c4', partidoId: 'p8', userId: 'j20', userName: 'Juan Pérez',
      texto: '¡Goleada histórica! 4-2 y seguimos líderes 🏆',
      fecha: DateTime(2025, 3, 8, 21, 30), likes: 25,
    ),
    ComentarioModel(
      id: 'c5', partidoId: 'p1', userId: 'j2', userName: 'Camila López',
      texto: 'El próximo partido lo ganamos 💪⚽',
      fecha: DateTime(2025, 3, 9, 10, 15), likes: 5,
    ),
  ];

  static List<ComentarioModel> getByPartido(String partidoId) {
    return comentarios.where((c) => c.partidoId == partidoId).toList()
      ..sort((a, b) => b.fecha.compareTo(a.fecha));
  }
}
