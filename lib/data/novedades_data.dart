import '../models/novedad_model.dart';

class NovedadesData {
  static final List<NovedadModel> novedades = [
    NovedadModel(
      id: 'n1',
      titulo: 'Apertura 2025: comenzó el torneo más esperado',
      resumen: 'Los principales clubes de la AHBA ya están jugando la primera fecha del torneo Apertura 2025 de Damas y Caballeros.',
      contenido: 'Con gran expectativa, este fin de semana comenzó el torneo Apertura 2025 de la Asociación de Hockey de Buenos Aires. Los 7 principales clubes de la primera división damas y caballeros se enfrentaron en la primera fecha, con resultados sorprendentes y muy buen nivel de juego.\n\nBelgrano Athletic y Alumni son los líderes en Damas con puntaje perfecto, mientras que en Caballeros Belgrano Athletic manda con autoridad tras golear 4-2 a CUBA.\n\nLa próxima fecha promete emociones fuertes con el clásico River vs Alumni en Damas.',
      categoria: 'torneo',
      fechaPublicacion: DateTime(2025, 3, 10),
      autor: 'Redacción AHBA',
      destacada: true,
    ),
    NovedadModel(
      id: 'n2',
      titulo: 'María González, goleadora implacable',
      resumen: 'La delantera de Belgrano Athletic convirtió 12 goles en 6 partidos y lidera la tabla de goleadoras del Apertura.',
      contenido: 'María González, jugadora de Belgrano Athletic, está teniendo un arranque de temporada espectacular. Con 12 goles en 6 partidos, lidera cómodamente la tabla de goleadoras del torneo Apertura Damas 1ra División.\n\n"Estoy muy contenta con el arranque del equipo. Lo importante es que estamos ganando y yo puedo ayudar con goles. Ojalá sigamos así", declaró la delantera.\n\nEn Caballeros, Juan Pérez de Belgrano Athletic también lidera con 15 goles.',
      categoria: 'general',
      fechaPublicacion: DateTime(2025, 3, 12),
      autor: 'Prensa AHBA',
    ),
    NovedadModel(
      id: 'n3',
      titulo: 'SIC incorporó nuevas jugadoras para el Clausura',
      resumen: 'El club de San Isidro anunció tres refuerzos de cara al segundo semestre del año.',
      contenido: 'San Isidro Club (SIC) confirmó la incorporación de tres jugadoras de gran nivel para reforzar su plantel de cara al torneo Clausura 2025. Se trata de:\n\n- Catalina Méndez (mediocampista, ex Belgrano Athletic)\n- Isabella Torres (delantera, ex Alumni)\n- Agustina Romero (arquera, ex Los Matreros)\n\nEl objetivo del club es pelear el título luego de quedar en la quinta posición en el Apertura.',
      categoria: 'club',
      fechaPublicacion: DateTime(2025, 3, 15),
      autor: 'Prensa SIC',
    ),
    NovedadModel(
      id: 'n4',
      titulo: 'Las Leonas entrenan en Buenos Aires',
      resumen: 'La selección argentina femenina eligió la AHBA como sede de preparación para el Mundial.',
      contenido: 'El seleccionado argentino femenino de hockey, Las Leonas, eligió las instalaciones de la AHBA para realizar su pretemporada de cara al Mundial que se disputará a fin de año.\n\nDurante dos semanas, jugadoras como Rocío Sánchez Moccia, María José Granatto y Delfina Merino entrenarán en doble turno y disputarán algunos amistosos contra equipos de la liga local.\n\n"Es un orgullo que nos hayan elegido. Vamos a tratar de estar a la altura", declaró el presidente de la AHBA.',
      categoria: 'seleccion',
      fechaPublicacion: DateTime(2025, 3, 18),
      autor: 'Redacción AHBA',
      destacada: true,
    ),
    NovedadModel(
      id: 'n5',
      titulo: 'Fixture confirmado para la fecha 7',
      resumen: 'Se viene una fecha clave con el clásico Alumni vs CUBA en Damas.',
      contenido: 'Ya está confirmado el fixture de la fecha 7 del torneo Apertura 2025. Los partidos más destacados son:\n\n**Damas 1ra:**\n- Alumni vs CUBA - Sábado 28/6 16:00\n- Belgrano Athletic vs SIC - Sábado 28/6 15:30\n\n**Caballeros 1ra:**\n- Belgrano Athletic vs SIC - Sábado 28/6 20:00\n\nUna fecha que puede definir varias posiciones de cara a las finales.',
      categoria: 'torneo',
      fechaPublicacion: DateTime(2025, 3, 20),
      autor: 'Redacción AHBA',
    ),
    NovedadModel(
      id: 'n6',
      titulo: 'River Plate cumple 10 años en el hockey femenino',
      resumen: 'El club de Núñez celebra una década de su sección de hockey sobre césped para mujeres.',
      contenido: 'River Plate está celebrando el décimo aniversario de su sección de hockey sobre césped femenino. Lo que comenzó como un proyecto pequeño con apenas 20 jugadoras, hoy cuenta con más de 200 en todas las categorías.\n\n"Este aniversario nos llena de orgullo. Trabajamos para ser cada vez más grandes y competitivos", dijo la entrenadora del club.\n\nPara celebrarlo, se realizará un torneo amistoso el próximo sábado con clubes invitados.',
      categoria: 'club',
      fechaPublicacion: DateTime(2025, 3, 22),
      autor: 'Prensa River',
    ),
    NovedadModel(
      id: 'n7',
      titulo: 'Nuevo sistema de VAR en el hockey argentino',
      resumen: 'La CAH implementará videoarbitraje en los partidos decisivos del año.',
      contenido: 'La Confederación Argentina de Hockey (CAH) anunció que implementará un sistema de videoarbitraje (VAR) en los partidos decisivos de los torneos oficiales a partir de este año.\n\nEl objetivo es reducir errores arbitrales en jugadas clave como goles, corners cortos y tarjetas.\n\n"Estamos a la vanguardia del hockey mundial. Este es un paso importante para el deporte", declaró el presidente de la CAH.',
      categoria: 'general',
      fechaPublicacion: DateTime(2025, 3, 25),
      autor: 'Redacción AHBA',
    ),
    NovedadModel(
      id: 'n8',
      titulo: 'Los Matreros buscan refuerzos',
      resumen: 'El club de Hurlingham quiere sumar jugadoras para pelear el ascenso.',
      contenido: 'Los Matreros, actualmente en el último puesto de la tabla de Damas 1ra, abrieron una convocatoria para sumar jugadoras de cara al torneo Clausura.\n\nEl objetivo del club es reforzar todas las líneas y pelear por mejorar la posición obtenida en el Apertura.\n\nLas interesadas pueden acercarse a entrenar al club de lunes a viernes de 18 a 21hs.',
      categoria: 'club',
      fechaPublicacion: DateTime(2025, 3, 28),
      autor: 'Prensa Los Matreros',
    ),
  ];

  // Obtener novedades destacadas
  static List<NovedadModel> get destacadas =>
      novedades.where((n) => n.destacada).toList();

  // Filtrar por categoría
  static List<NovedadModel> filtrarPorCategoria(String categoria) {
    return novedades.where((n) => n.categoria == categoria).toList();
  }

  // Obtener más recientes
  static List<NovedadModel> get recientes {
    final lista = [...novedades];
    lista.sort((a, b) => b.fechaPublicacion.compareTo(a.fechaPublicacion));
    return lista;
  }

  // Buscar por ID
  static NovedadModel? getById(String id) {
    try {
      return novedades.firstWhere((n) => n.id == id);
    } catch (e) {
      return null;
    }
  }
}
