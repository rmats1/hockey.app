import '../models/posicion_model.dart';

class PosicionesData {
  // Posiciones del Torneo Apertura 2025 Damas 1ra
  static final List<PosicionModel> posicionesAperturaDamas1ra = [
    PosicionModel.calcular(
      posicion: 1,
      clubId: '1', // Alumni
      jugados: 6,
      ganados: 5,
      empatados: 1,
      golesFavor: 18,
      golesContra: 5,
    ),
    PosicionModel.calcular(
      posicion: 2,
      clubId: '4', // Belgrano Athletic
      jugados: 6,
      ganados: 5,
      empatados: 0,
      golesFavor: 22,
      golesContra: 7,
    ),
    PosicionModel.calcular(
      posicion: 3,
      clubId: '92', // River Plate
      jugados: 6,
      ganados: 4,
      empatados: 1,
      golesFavor: 15,
      golesContra: 8,
    ),
    PosicionModel.calcular(
      posicion: 4,
      clubId: '42', // CUBA
      jugados: 6,
      ganados: 3,
      empatados: 2,
      golesFavor: 12,
      golesContra: 10,
    ),
    PosicionModel.calcular(
      posicion: 5,
      clubId: '103', // SIC
      jugados: 6,
      ganados: 2,
      empatados: 2,
      golesFavor: 10,
      golesContra: 12,
    ),
    PosicionModel.calcular(
      posicion: 6,
      clubId: '10', // Banco Nación
      jugados: 6,
      ganados: 1,
      empatados: 2,
      golesFavor: 7,
      golesContra: 15,
    ),
    PosicionModel.calcular(
      posicion: 7,
      clubId: '33', // Los Matreros
      jugados: 6,
      ganados: 0,
      empatados: 2,
      golesFavor: 4,
      golesContra: 21,
    ),
  ];

  // Posiciones del Torneo Apertura 2025 Caballeros 1ra
  static final List<PosicionModel> posicionesAperturaCaballeros1ra = [
    PosicionModel.calcular(
      posicion: 1,
      clubId: '4', // Belgrano Athletic
      jugados: 6,
      ganados: 6,
      empatados: 0,
      golesFavor: 25,
      golesContra: 4,
    ),
    PosicionModel.calcular(
      posicion: 2,
      clubId: '42', // CUBA
      jugados: 6,
      ganados: 5,
      empatados: 0,
      golesFavor: 20,
      golesContra: 8,
    ),
    PosicionModel.calcular(
      posicion: 3,
      clubId: '103', // SIC
      jugados: 6,
      ganados: 4,
      empatados: 1,
      golesFavor: 16,
      golesContra: 9,
    ),
    PosicionModel.calcular(
      posicion: 4,
      clubId: '1', // Alumni
      jugados: 6,
      ganados: 3,
      empatados: 1,
      golesFavor: 13,
      golesContra: 11,
    ),
    PosicionModel.calcular(
      posicion: 5,
      clubId: '92', // River
      jugados: 6,
      ganados: 2,
      empatados: 1,
      golesFavor: 9,
      golesContra: 14,
    ),
    PosicionModel.calcular(
      posicion: 6,
      clubId: '33', // Los Matreros
      jugados: 6,
      ganados: 1,
      empatados: 1,
      golesFavor: 6,
      golesContra: 18,
    ),
    PosicionModel.calcular(
      posicion: 7,
      clubId: '10', // Banco Nación
      jugados: 6,
      ganados: 0,
      empatados: 0,
      golesFavor: 3,
      golesContra: 24,
    ),
  ];

  // Obtener posiciones según el torneo
  static List<PosicionModel> getPosiciones(String torneoId) {
    if (torneoId == 't1') return posicionesAperturaDamas1ra;
    if (torneoId == 't8') return posicionesAperturaCaballeros1ra;
    return [];
  }
}
