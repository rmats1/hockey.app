import '../models/prediccion_model.dart';

class PrediccionesData {
  static final List<PrediccionModel> predicciones = [
    PrediccionModel(
      id: 'pr1', partidoId: 'p6', userId: 'j1', userName: 'María González',
      golesLocal: 3, golesVisitante: 1,
      fecha: DateTime(2025, 6, 25),
    ),
    PrediccionModel(
      id: 'pr2', partidoId: 'p6', userId: 'j4', userName: 'Lucía Fernández',
      golesLocal: 2, golesVisitante: 2,
      fecha: DateTime(2025, 6, 26),
    ),
    PrediccionModel(
      id: 'pr3', partidoId: 'p6', userId: 'j20', userName: 'Juan Pérez',
      golesLocal: 4, golesVisitante: 0,
      fecha: DateTime(2025, 6, 26),
    ),
  ];

  static List<PrediccionModel> getByPartido(String partidoId) {
    return predicciones.where((p) => p.partidoId == partidoId).toList();
  }
}
