import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import '../../utils/colors.dart';

class TacticalBoardScreen extends StatefulWidget {
  const TacticalBoardScreen({super.key});

  @override
  State<TacticalBoardScreen> createState() => _TacticalBoardScreenState();
}

class _TacticalBoardScreenState extends State<TacticalBoardScreen> {
  final List<PlayerNode> _nodes = [];
  Color _selectedColor = Colors.red;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColors.background,
      appBar: AppBar(
        title: Text('Pizarra Táctica', style: GoogleFonts.montserrat(fontWeight: FontWeight.bold)),
        backgroundColor: AppColors.primary,
        foregroundColor: Colors.white,
        elevation: 0,
        actions: [
          IconButton(
            icon: const Icon(Icons.delete_outline),
            onPressed: () => setState(() => _nodes.clear()),
            tooltip: 'Limpiar pizarra',
          ),
        ],
      ),
      body: Column(
        children: [
          // Área de Campo
          Expanded(
            child: Container(
              margin: const EdgeInsets.all(16),
              decoration: BoxDecoration(
                color: Colors.green.shade800,
                borderRadius: BorderRadius.circular(24),
                border: Border.all(color: Colors.white, width: 4),
                boxShadow: [
                  BoxShadow(color: Colors.black.withOpacity(0.1), blurRadius: 20, offset: const Offset(0, 10))
                ],
              ),
              child: ClipRRect(
                borderRadius: BorderRadius.circular(20),
                child: Stack(
                  children: [
                    // Líneas del campo (Dibujo simplificado)
                    _buildFieldLines(),
                    
                    // Capa para agregar nuevos nodos
                    GestureDetector(
                      onTapDown: (details) {
                        setState(() {
                          _nodes.add(PlayerNode(
                            id: DateTime.now().toString(),
                            offset: details.localPosition,
                            color: _selectedColor,
                          ));
                        });
                      },
                    ),

                    // Nodos (Jugadores movibles)
                    ..._nodes.map((node) => _buildDraggableNode(node)),
                  ],
                ),
              ),
            ),
          ),
          
          // Barra de herramientas
          Container(
            padding: const EdgeInsets.all(24),
            decoration: BoxDecoration(
              color: Colors.white,
              borderRadius: const BorderRadius.vertical(top: Radius.circular(32)),
              boxShadow: [
                BoxShadow(color: Colors.black.withOpacity(0.05), blurRadius: 10, offset: const Offset(0, -5))
              ],
            ),
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                Row(
                  children: [
                    Text('COLOR DE EQUIPO', style: GoogleFonts.montserrat(fontSize: 10, fontWeight: FontWeight.w800, color: Colors.grey, letterSpacing: 1)),
                    const Spacer(),
                    _colorBtn(Colors.red),
                    _colorBtn(Colors.blue),
                    _colorBtn(Colors.white),
                    _colorBtn(Colors.black),
                  ],
                ),
                const SizedBox(height: 16),
                const Text(
                  'TOCA PARA AGREGAR • ARRASTRA PARA MOVER • MANTÉN PARA ELIMINAR',
                  textAlign: TextAlign.center,
                  style: TextStyle(fontSize: 9, color: Colors.grey, fontWeight: FontWeight.bold),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildFieldLines() {
    return Stack(
      children: [
        // Línea central
        Center(child: Container(width: double.infinity, height: 2, color: Colors.white24)),
        // Círculo central
        Center(
          child: Container(
            width: 100, height: 100,
            decoration: BoxDecoration(shape: BoxShape.circle, border: Border.all(color: Colors.white24, width: 2)),
          ),
        ),
        // Áreas
        Positioned(top: 0, left: 0, right: 0, child: _buildArea(true)),
        Positioned(bottom: 0, left: 0, right: 0, child: _buildArea(false)),
      ],
    );
  }

  Widget _buildArea(bool top) {
    return Center(
      child: Container(
        width: 180, height: 80,
        decoration: BoxDecoration(
          border: Border.all(color: Colors.white24, width: 2),
          borderRadius: BorderRadius.vertical(
            bottom: top ? const Radius.circular(90) : Radius.zero,
            top: top ? Radius.zero : const Radius.circular(90),
          ),
        ),
      ),
    );
  }

  Widget _buildDraggableNode(PlayerNode node) {
    return Positioned(
      left: node.offset.dx - 20,
      top: node.offset.dy - 20,
      child: GestureDetector(
        onPanUpdate: (details) {
          setState(() {
            node.offset += details.delta;
          });
        },
        onLongPress: () {
          setState(() {
            _nodes.remove(node);
          });
        },
        child: Container(
          width: 40, height: 40,
          decoration: BoxDecoration(
            color: node.color,
            shape: BoxShape.circle,
            border: Border.all(color: Colors.white, width: 2),
            boxShadow: [
              BoxShadow(color: Colors.black.withOpacity(0.2), blurRadius: 5, offset: const Offset(0, 2))
            ],
          ),
          child: Center(
            child: Icon(
              Icons.sports_hockey_rounded, 
              size: 20, 
              color: node.color == Colors.white ? Colors.black : Colors.white,
            ),
          ),
        ),
      ),
    );
  }

  Widget _colorBtn(Color color) {
    final isSel = _selectedColor == color;
    return GestureDetector(
      onTap: () => setState(() => _selectedColor = color),
      child: Container(
        margin: const EdgeInsets.only(left: 12),
        width: 32, height: 32,
        decoration: BoxDecoration(
          color: color, 
          shape: BoxShape.circle,
          border: Border.all(color: isSel ? AppColors.secondary : Colors.grey.shade300, width: isSel ? 3 : 1),
        ),
      ),
    );
  }
}

class PlayerNode {
  final String id;
  Offset offset;
  Color color;
  PlayerNode({required this.id, required this.offset, required this.color});
}
