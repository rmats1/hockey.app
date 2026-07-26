import 'package:flutter/material.dart';

class AppConstants {
  static const String appName = 'Hockey Plus';
  
  static const List<String> ramas = ['Damas', 'Caballeros'];
  
  // Categorías alineadas exactamente con la API AHBA 2026
  static const List<String> categorias = [
    'Primera',
    'Intermedia',
    'Segunda',
    'Cuarta',
    'Quinta',
    'Sexta',
    'Septima',
    'Octava',
    'Novena',
    '10ma'
  ];

  static const List<String> divisiones = ['A', 'B', 'C', 'D', 'E', 'F', 'G'];
  
  static const List<String> posicionesJugador = [
    'Arquera/o',
    'Defensora/or',
    'Mediocampista',
    'Delantera/or',
  ];
  
  static const List<String> rolesCuerpoTecnico = [
    'Head Coach',
    'Asistente',
    'Preparador Físico',
    'Médico',
    'Kinesiólogo',
    'Manager',
  ];

  // Iconos personalizados
  static const IconData bochaHockey = Icons.sports_hockey;
  static const String emojiBocha = '🏑';
}
