import '../models/torneo_model.dart';

class TorneosData {
  static final List<TorneoModel> torneos = [
    // ========== DAMAS ==========
    TorneoModel(
      id: 't1',
      nombre: 'Apertura Damas 1ra División',
      categoria: 'Damas',
      division: '1ra',
      temporada: '2025',
      fechaInicio: DateTime(2025, 3, 1),
      fechaFin: DateTime(2025, 6, 30),
      estado: 'en_curso',
      clubesParticipantes: ['1', '4', '10', '33', '42', '92', '103'],
      descripcion: 'Torneo apertura de la primera división damas de la AHBA',
    ),
    TorneoModel(
      id: 't2',
      nombre: 'Apertura Damas Intermedia',
      categoria: 'Damas',
      division: 'Intermedia',
      temporada: '2025',
      fechaInicio: DateTime(2025, 3, 1),
      fechaFin: DateTime(2025, 6, 30),
      estado: 'en_curso',
      clubesParticipantes: ['1', '4', '10', '33', '42', '92', '103'],
      descripcion: 'Torneo apertura intermedia damas',
    ),
    TorneoModel(
      id: 't3',
      nombre: 'Clausura Damas 1ra División',
      categoria: 'Damas',
      division: '1ra',
      temporada: '2025',
      fechaInicio: DateTime(2025, 8, 1),
      fechaFin: DateTime(2025, 11, 30),
      estado: 'proximo',
      clubesParticipantes: ['1', '4', '10', '33', '42', '92', '103'],
      descripcion: 'Torneo clausura de la primera división damas',
    ),
    TorneoModel(
      id: 't4',
      nombre: 'Apertura Damas 2da División',
      categoria: 'Damas',
      division: '2da',
      temporada: '2025',
      fechaInicio: DateTime(2025, 3, 15),
      fechaFin: DateTime(2025, 7, 15),
      estado: 'en_curso',
      clubesParticipantes: ['2', '5', '7', '8', '12', '22', '88'],
      descripcion: 'Torneo apertura segunda división damas',
    ),
    TorneoModel(
      id: 't5',
      nombre: 'Apertura Damas 5ta División',
      categoria: 'Damas',
      division: '5ta',
      temporada: '2025',
      fechaInicio: DateTime(2025, 4, 1),
      fechaFin: DateTime(2025, 8, 1),
      estado: 'en_curso',
      clubesParticipantes: ['14', '20', '30', '45', '60', '75'],
      descripcion: 'Torneo apertura quinta división damas',
    ),
    TorneoModel(
      id: 't6',
      nombre: 'Apertura Damas 6ta División',
      categoria: 'Damas',
      division: '6ta',
      temporada: '2025',
      fechaInicio: DateTime(2025, 4, 1),
      fechaFin: DateTime(2025, 8, 1),
      estado: 'en_curso',
      clubesParticipantes: ['14', '20', '30', '45', '60', '75'],
      descripcion: 'Torneo apertura sexta división damas',
    ),
    TorneoModel(
      id: 't7',
      nombre: 'Apertura Damas 7ma División',
      categoria: 'Damas',
      division: '7ma',
      temporada: '2025',
      fechaInicio: DateTime(2025, 4, 1),
      fechaFin: DateTime(2025, 8, 1),
      estado: 'en_curso',
      clubesParticipantes: ['14', '20', '30', '45', '60', '75'],
      descripcion: 'Torneo apertura séptima división damas',
    ),

    // ========== CABALLEROS ==========
    TorneoModel(
      id: 't8',
      nombre: 'Apertura Caballeros 1ra División',
      categoria: 'Caballeros',
      division: '1ra',
      temporada: '2025',
      fechaInicio: DateTime(2025, 3, 1),
      fechaFin: DateTime(2025, 6, 30),
      estado: 'en_curso',
      clubesParticipantes: ['1', '4', '10', '33', '42', '92', '103'],
      descripcion: 'Torneo apertura primera división caballeros',
    ),
    TorneoModel(
      id: 't9',
      nombre: 'Apertura Caballeros Intermedia',
      categoria: 'Caballeros',
      division: 'Intermedia',
      temporada: '2025',
      fechaInicio: DateTime(2025, 3, 1),
      fechaFin: DateTime(2025, 6, 30),
      estado: 'en_curso',
      clubesParticipantes: ['1', '4', '10', '33', '42', '92', '103'],
      descripcion: 'Torneo apertura intermedia caballeros',
    ),
    TorneoModel(
      id: 't10',
      nombre: 'Clausura Caballeros 1ra División',
      categoria: 'Caballeros',
      division: '1ra',
      temporada: '2025',
      fechaInicio: DateTime(2025, 8, 1),
      fechaFin: DateTime(2025, 11, 30),
      estado: 'proximo',
      clubesParticipantes: ['1', '4', '10', '33', '42', '92', '103'],
      descripcion: 'Torneo clausura primera división caballeros',
    ),
    TorneoModel(
      id: 't11',
      nombre: 'Apertura Caballeros 2da División',
      categoria: 'Caballeros',
      division: '2da',
      temporada: '2025',
      fechaInicio: DateTime(2025, 3, 15),
      fechaFin: DateTime(2025, 7, 15),
      estado: 'en_curso',
      clubesParticipantes: ['2', '5', '7', '8', '12', '22', '88'],
      descripcion: 'Torneo apertura segunda división caballeros',
    ),
    TorneoModel(
      id: 't12',
      nombre: 'Apertura Caballeros 5ta División',
      categoria: 'Caballeros',
      division: '5ta',
      temporada: '2025',
      fechaInicio: DateTime(2025, 4, 1),
      fechaFin: DateTime(2025, 8, 1),
      estado: 'en_curso',
      clubesParticipantes: ['14', '20', '30', '45', '60', '75'],
      descripcion: 'Torneo apertura quinta división caballeros',
    ),
    TorneoModel(
      id: 't13',
      nombre: 'Apertura Caballeros 6ta División',
      categoria: 'Caballeros',
      division: '6ta',
      temporada: '2025',
      fechaInicio: DateTime(2025, 4, 1),
      fechaFin: DateTime(2025, 8, 1),
      estado: 'en_curso',
      clubesParticipantes: ['14', '20', '30', '45', '60', '75'],
      descripcion: 'Torneo apertura sexta división caballeros',
    ),

    // ========== TORNEOS FINALIZADOS 2024 ==========
    TorneoModel(
      id: 't14',
      nombre: 'Apertura 2024 Damas 1ra',
      categoria: 'Damas',
      division: '1ra',
      temporada: '2024',
      fechaInicio: DateTime(2024, 3, 1),
      fechaFin: DateTime(2024, 6, 30),
      estado: 'finalizado',
      clubesParticipantes: ['1', '4', '10', '33', '42', '92'],
      descripcion: 'Torneo apertura 2024 finalizado',
    ),
    TorneoModel(
      id: 't15',
      nombre: 'Clausura 2024 Damas 1ra',
      categoria: 'Damas',
      division: '1ra',
      temporada: '2024',
      fechaInicio: DateTime(2024, 8, 1),
      fechaFin: DateTime(2024, 11, 30),
      estado: 'finalizado',
      clubesParticipantes: ['1', '4', '10', '33', '42', '92'],
      descripcion: 'Torneo clausura 2024 finalizado',
    ),
  ];

  // Filtrar torneos por categoría
  static List<TorneoModel> filtrarPorCategoria(String categoria) {
    return torneos.where((t) => t.categoria == categoria).toList();
  }

  // Filtrar torneos por división
  static List<TorneoModel> filtrarPorDivision(String division) {
    return torneos.where((t) => t.division == division).toList();
  }

  // Filtrar torneos por estado
  static List<TorneoModel> filtrarPorEstado(String estado) {
    return torneos.where((t) => t.estado == estado).toList();
  }

  // Obtener torneos en curso
  static List<TorneoModel> get enCurso => filtrarPorEstado('en_curso');

  // Obtener torneos próximos
  static List<TorneoModel> get proximos => filtrarPorEstado('proximo');

  // Obtener torneos finalizados
  static List<TorneoModel> get finalizados => filtrarPorEstado('finalizado');
}
