import 'package:flutter_test/flutter_test.dart';
import 'package:hockey_app/models/ahba_models.dart';
import 'package:hockey_app/models/club_model.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  group('AHBA Model Serialization Tests', () {
    test('TorneoResumen fromJson parses correctamente', () {
      final jsonMap = {
        'id': '00000001',
        'nombre': ' Damas A ',
        'ramaKey': 'F',
        'categoria': ' Primera ',
        'division': ' A ',
        'temporada': ' 2025 ',
      };

      final torneo = TorneoResumen.fromJson(jsonMap);
      expect(torneo.id, '00000001');
      expect(torneo.nombre, 'Damas A');
      expect(torneo.rama, 'F');
      expect(torneo.ramaLabel, 'Femenino');
      expect(torneo.categoria, 'Primera');
      expect(torneo.division, 'A');
    });

    test('ClubModel fromJson parses correctamente', () {
      final jsonMap = {
        'clubId': '001',
        'nombre': 'Club Ciudad de Buenos Aires',
        'nombreCorto': 'Muni',
        'escudoImagePath': 'http://example.com/logo.png',
      };

      final club = ClubModel.fromJson(jsonMap);
      expect(club.clubId, '001');
      expect(club.nombre, 'Club Ciudad de Buenos Aires');
      expect(club.nombreCorto, 'Muni');
    });

    test('PartidoAHBA resultado y ganador parsea correctamente', () {
      final jsonMap = {
        'id': '101',
        'idClubLocal': '001',
        'idClubVisitante': '002',
        'nombreLocal': 'Muni',
        'nombreVisitante': 'Geba',
        'golesLocal': 3,
        'golesVisitante': 1,
        'numeroFecha': '5',
        'played': true,
        'playing': false,
      };

      final partido = PartidoAHBA.fromJson(jsonMap);
      expect(partido.jugado, isTrue);
      expect(partido.golesLocal, 3);
      expect(partido.golesVisitante, 1);
      expect(partido.resultado, '3 - 1');
      expect(partido.ganadorId, '001');
    });
  });
}
