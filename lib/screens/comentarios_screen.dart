import 'package:flutter/material.dart';
import '../utils/colors.dart';
import '../data/comentarios_data.dart';

class ComentariosScreen extends StatefulWidget {
  final String partidoId;
  final String titulo;

  const ComentariosScreen({super.key, required this.partidoId, required this.titulo});

  @override
  State<ComentariosScreen> createState() => _ComentariosScreenState();
}

class _ComentariosScreenState extends State<ComentariosScreen> {
  final _controller = TextEditingController();
  final _comentarios = <Map<String, dynamic>>[];

  @override
  void initState() {
    super.initState();
    _cargarComentarios();
  }

  void _cargarComentarios() {
    final data = ComentariosData.getByPartido(widget.partidoId);
    _comentarios.clear();
    _comentarios.addAll(data.map((c) => {
      'user': c.userName,
      'texto': c.texto,
      'fecha': c.fecha,
      'likes': c.likes,
    }));
  }

  void _agregarComentario() {
    if (_controller.text.trim().isEmpty) return;
    setState(() {
      _comentarios.insert(0, {
        'user': 'Vos',
        'texto': _controller.text,
        'fecha': DateTime.now(),
        'likes': 0,
      });
      _controller.clear();
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColors.background,
      appBar: AppBar(
        title: Text(widget.titulo),
        backgroundColor: AppColors.primary,
        foregroundColor: Colors.white,
      ),
      body: Column(
        children: [
          Expanded(
            child: _comentarios.isEmpty
                ? const Center(child: Text('Sé el primero en comentar', style: TextStyle(color: AppColors.textSecondary)))
                : ListView.builder(
                    padding: const EdgeInsets.all(16),
                    itemCount: _comentarios.length,
                    itemBuilder: (context, index) {
                      final c = _comentarios[index];
                      return _buildComentario(c);
                    },
                  ),
          ),
          // Input
          Container(
            padding: const EdgeInsets.all(8),
            decoration: BoxDecoration(
              color: Colors.white,
              boxShadow: [BoxShadow(color: Colors.grey.shade300, blurRadius: 4, offset: const Offset(0, -2))],
            ),
            child: Row(
              children: [
                Expanded(
                  child: TextField(
                    controller: _controller,
                    decoration: InputDecoration(
                      hintText: 'Escribí un comentario...',
                      border: OutlineInputBorder(borderRadius: BorderRadius.circular(24)),
                      contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                    ),
                  ),
                ),
                const SizedBox(width: 8),
                IconButton(
                  icon: const Icon(Icons.send, color: AppColors.primary),
                  onPressed: _agregarComentario,
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildComentario(Map<String, dynamic> c) {
    return Card(
      margin: const EdgeInsets.only(bottom: 8),
      child: Padding(
        padding: const EdgeInsets.all(12),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                CircleAvatar(
                  backgroundColor: AppColors.primary,
                  radius: 14,
                  child: Text(c['user'][0], style: const TextStyle(color: Colors.white, fontSize: 12, fontWeight: FontWeight.bold)),
                ),
                const SizedBox(width: 8),
                Text(c['user'], style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 13)),
              ],
            ),
            const SizedBox(height: 6),
            Text(c['texto'], style: const TextStyle(fontSize: 13)),
            const SizedBox(height: 6),
            Row(
              children: [
                const Icon(Icons.favorite_border, size: 14, color: AppColors.textSecondary),
                const SizedBox(width: 4),
                Text('${c['likes']}', style: const TextStyle(fontSize: 11, color: AppColors.textSecondary)),
                const SizedBox(width: 16),
                Text(_formatearFecha(c['fecha']), style: const TextStyle(fontSize: 11, color: AppColors.textSecondary)),
              ],
            ),
          ],
        ),
      ),
    );
  }

  String _formatearFecha(DateTime f) {
    const meses = ['Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun', 'Jul', 'Ago', 'Sep', 'Oct', 'Nov', 'Dic'];
    return '${f.day} ${meses[f.month - 1]} ${f.hour}:${f.minute.toString().padLeft(2, '0')}';
  }
}
