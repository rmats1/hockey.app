import '../models/goleador_model.dart';

class GoleadoresData {
  static final List<GoleadorModel> goleadoresAperturaDamas1ra = [
    GoleadorModel(
      posicion: 1,
      jugadorId: 'j1',
      jugadorNombre: 'María González',
      clubId: '4',
      goles: 12,
      division: '1ra',
    ),
    GoleadorModel(
      posicion: 2,
      jugadorId: 'j2',
      jugadorNombre: 'Lucía Fernández',
      clubId: '1',
      goles: 10,
      division: '1ra',
    ),
    GoleadorModel(
      posicion: 3,
      jugadorId: 'j3',
      jugadorNombre: 'Sofía Martínez',
      clubId: '92',
      goles: 8,
      division: '1ra',
    ),
    GoleadorModel(
      posicion: 4,
      jugadorId: 'j4',
      jugadorNombre: 'Camila López',
      clubId: '42',
      goles: 7,
      division: '1ra',
    ),
    GoleadorModel(
      posicion: 5,
      jugadorId: 'j5',
      jugadorNombre: 'Valentina Rodríguez',
      clubId: '103',
      goles: 6,
      division: '1ra',
    ),
    GoleadorModel(
      posicion: 6,
      jugadorId: 'j6',
      jugadorNombre: 'Florencia Sánchez',
      clubId: '10',
      goles: 5,
      division: '1ra',
    ),
    GoleadorModel(
      posicion: 7,
      jugadorId: 'j7',
      jugadorNombre: 'Agustina Romero',
      clubId: '33',
      goles: 4,
      division: '1ra',
    ),
    GoleadorModel(
      posicion: 8,
      jugadorId: 'j8',
      jugadorNombre: 'Julieta Acosta',
      clubId: '92',
      goles: 4,
      division: '1ra',
    ),
    GoleadorModel(
      posicion: 9,
      jugadorId: 'j9',
      jugadorNombre: 'Catalina Méndez',
      clubId: '4',
      goles: 3,
      division: '1ra',
    ),
    GoleadorModel(
      posicion: 10,
      jugadorId: 'j10',
      jugadorNombre: 'Isabella Torres',
      clubId: '1',
      goles: 3,
      division: '1ra',
    ),
  ];

  static final List<GoleadorModel> goleadoresAperturaCaballeros1ra = [
    GoleadorModel(
      posicion: 1,
      jugadorId: 'j20',
      jugadorNombre: 'Juan Pérez',
      clubId: '4',
      goles: 15,
      division: '1ra',
    ),
    GoleadorModel(
      posicion: 2,
      jugadorId: 'j21',
      jugadorNombre: 'Martín Suárez',
      clubId: '42',
      goles: 11,
      division: '1ra',
    ),
    GoleadorModel(
      posicion: 3,
      jugadorId: 'j22',
      jugadorNombre: 'Lucas Díaz',
      clubId: '103',
      goles: 9,
      division: '1ra',
    ),
    GoleadorModel(
      posicion: 4,
      jugadorId: 'j23',
      jugadorNombre: 'Diego Ramírez',
      clubId: '1',
      goles: 8,
      division: '1ra',
    ),
    GoleadorModel(
      posicion: 5,
      jugadorId: 'j24',
      jugadorNombre: 'Sebastián Castro',
      clubId: '92',
      goles: 6,
      division: '1ra',
    ),
    GoleadorModel(
      posicion: 6,
      jugadorId: 'j25',
      jugadorNombre: 'Federico Silva',
      clubId: '4',
      goles: 5,
      division: '1ra',
    ),
    GoleadorModel(
      posicion: 7,
      jugadorId: 'j26',
      jugadorNombre: 'Tomás González',
      clubId: '42',
      goles: 4,
      division: '1ra',
    ),
    GoleadorModel(
      posicion: 8,
      jugadorId: 'j27',
      jugadorNombre: 'Nicolás Fernández',
      clubId: '103',
      goles: 4,
      division: '1ra',
    ),
  ];

  static List<GoleadorModel> getGoleadores(String torneoId) {
    if (torneoId == 't1') return goleadoresAperturaDamas1ra;
    if (torneoId == 't8') return goleadoresAperturaCaballeros1ra;
    return [];
  }

  static List<GoleadorModel> getTopGoleadores(String torneoId, {int limit = 10}) {
    final todos = getGoleadores(torneoId);
    return todos.take(limit).toList();
  }
}
