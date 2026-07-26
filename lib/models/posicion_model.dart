class PosicionModel {
  final int posicion;
  final String clubId;
  final int puntos;
  final int partidosJugados;
  final int partidosGanados;
  final int partidosEmpatados;
  final int partidosPerdidos;
  final int golesAFavor;
  final int golesEnContra;
  final int diferenciaGoles;

  PosicionModel({
    required this.posicion,
    required this.clubId,
    required this.puntos,
    required this.partidosJugados,
    required this.partidosGanados,
    required this.partidosEmpatados,
    required this.partidosPerdidos,
    required this.golesAFavor,
    required this.golesEnContra,
    required this.diferenciaGoles,
  });

  // Calcular puntos (3 por ganado, 1 por empatado)
  factory PosicionModel.calcular({
    required int posicion,
    required String clubId,
    required int jugados,
    required int ganados,
    required int empatados,
    required int golesFavor,
    required int golesContra,
  }) {
    final perdidos = jugados - ganados - empatados;
    final puntos = (ganados * 3) + empatados;
    return PosicionModel(
      posicion: posicion,
      clubId: clubId,
      puntos: puntos,
      partidosJugados: jugados,
      partidosGanados: ganados,
      partidosEmpatados: empatados,
      partidosPerdidos: perdidos,
      golesAFavor: golesFavor,
      golesEnContra: golesContra,
      diferenciaGoles: golesFavor - golesContra,
    );
  }
}
