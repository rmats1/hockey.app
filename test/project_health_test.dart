import 'package:flutter_test/flutter_test.dart';
import 'package:hockey_app/models/ahba_models.dart';
import 'package:hockey_app/services/data_service.dart';

void main() {
  group('Project Health Checks', () {
    test('DataService Singleton Integrity', () {
      final instance1 = DataService.instance;
      final instance2 = DataService.instance;
      expect(instance1, same(instance2));
    });

    test('AHBA Model Parsing Test', () {
      final jsonSample = {
        'id': '3747',
        'nombre': 'Caballeros C2',
        'rama': 'M',
        'categoria': 'Intermedia',
        'division': 'C',
        'temporada': '2026'
      };
      
      final model = TorneoResumen.fromJson(jsonSample);
      expect(model.id, '3747');
      expect(model.nombre, 'Caballeros C2');
      expect(model.temporada, '2026');
    });
  });
}
