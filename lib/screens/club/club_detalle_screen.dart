import 'package:flutter/material.dart';
import '../../utils/colors.dart';
import '../../models/club_model.dart';
import 'club_info_tab.dart';
import 'club_partidos_tab.dart';
import 'club_posicion_tab.dart';
import 'club_goleadores_tab.dart';

class ClubDetalleScreen extends StatelessWidget {
  final Club club;
  final String? categoriaUsuario; // 'Damas' o 'Caballeros' (la del usuario)

  const ClubDetalleScreen({
    super.key,
    required this.club,
    this.categoriaUsuario,
  });

  @override
  Widget build(BuildContext context) {
    return DefaultTabController(
      length: 4,
      child: Scaffold(
        backgroundColor: AppColors.background,
        body: NestedScrollView(
          headerSliverBuilder: (context, innerBoxIsScrolled) {
            return [
              // ========== HEADER CON INFO DEL CLUB ==========
              SliverAppBar(
                expandedHeight: 200,
                pinned: true,
                backgroundColor: AppColors.primary,
                foregroundColor: Colors.white,
                flexibleSpace: FlexibleSpaceBar(
                  title: Text(
                    club.nombre,
                    style: const TextStyle(
                      color: Colors.white,
                      fontSize: 14,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                  centerTitle: true,
                  background: Container(
                    decoration: const BoxDecoration(
                      gradient: LinearGradient(
                        begin: Alignment.topCenter,
                        end: Alignment.bottomCenter,
                        colors: [
                          AppColors.primary,
                          AppColors.primaryLight,
                        ],
                      ),
                    ),
                    child: Center(
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          const SizedBox(height: 20),
                          // Escudo del club (placeholder)
                          Container(
                            width: 80,
                            height: 80,
                            decoration: BoxDecoration(
                              color: Colors.white,
                              borderRadius: BorderRadius.circular(16),
                              boxShadow: [
                                BoxShadow(
                                  color: Colors.black.withOpacity(0.2),
                                  blurRadius: 10,
                                  offset: const Offset(0, 4),
                                ),
                              ],
                            ),
                            child: const Icon(
                              Icons.sports_hockey,
                              size: 50,
                              color: AppColors.primary,
                            ),
                          ),
                          const SizedBox(height: 8),
                          Text(
                            club.nombreCorto,
                            style: const TextStyle(
                              color: Colors.white,
                              fontSize: 16,
                              fontWeight: FontWeight.bold,
                            ),
                          ),
                        ],
                      ),
                    ),
                  ),
                ),
                bottom: const TabBar(
                  indicatorColor: Colors.white,
                  labelColor: Colors.white,
                  unselectedLabelColor: Colors.white60,
                  isScrollable: true,
                  tabAlignment: TabAlignment.start,
                  tabs: [
                    Tab(icon: Icon(Icons.info), text: 'Info'),
                    Tab(icon: Icon(Icons.sports), text: 'Partidos'),
                    Tab(icon: Icon(Icons.leaderboard), text: 'Posición'),
                    Tab(icon: Icon(Icons.sports_score), text: 'Goleadores'),
                  ],
                ),
              ),
            ];
          },
          body: TabBarView(
            children: [
              ClubInfoTab(club: club),
              ClubPartidosTab(club: club),
              ClubPosicionTab(club: club, categoriaUsuario: categoriaUsuario),
              ClubGoleadoresTab(club: club),
            ],
          ),
        ),
      ),
    );
  }
}
