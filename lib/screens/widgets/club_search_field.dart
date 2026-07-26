import 'package:flutter/material.dart';
import '../../models/club_model.dart';
import '../../services/data_service.dart';

class ClubSearchField extends StatefulWidget {
  final Club? selectedClub;
  final ValueChanged<Club?> onClubSelected;
  final String userType;

  const ClubSearchField({
    super.key,
    required this.selectedClub,
    required this.onClubSelected,
    required this.userType,
  });

  @override
  State<ClubSearchField> createState() => _ClubSearchFieldState();
}

class _ClubSearchFieldState extends State<ClubSearchField> {
  final _controller = TextEditingController();
  final _focusNode = FocusNode();
  List<ClubModel> _allClubes = [];
  List<ClubModel> _filteredClubes = [];
  bool _showSuggestions = false;
  bool _isLoading = true;

  @override
  void initState() {
    super.initState();
    _loadClubes();
  }

  Future<void> _loadClubes() async {
    final list = await DataService.instance.getClubes();
    if (mounted) {
      setState(() {
        _allClubes = list;
        _filteredClubes = list;
        _isLoading = false;
      });
    }
  }

  @override
  void dispose() {
    _controller.dispose();
    _focusNode.dispose();
    super.dispose();
  }

  void _filterClubes(String query) {
    setState(() {
      if (query.isEmpty) {
        _filteredClubes = _allClubes;
      } else {
        _filteredClubes = _allClubes
            .where((club) =>
                club.nombre.toLowerCase().contains(query.toLowerCase()))
            .toList();
      }
      _showSuggestions = true;
    });
  }

  void _selectClub(ClubModel club) {
    setState(() {
      _controller.text = club.nombre;
      _showSuggestions = false;
    });
    // Convert ClubModel to Club (alias)
    widget.onClubSelected(Club(
      id: club.clubId,
      nombre: club.nombre,
      escudoUrl: club.escudoUrl
    ));
    _focusNode.unfocus();
  }

  void _clearSelection() {
    setState(() {
      _controller.clear();
      _filteredClubes = _allClubes;
      _showSuggestions = false;
    });
    widget.onClubSelected(null);
  }

  @override
  Widget build(BuildContext context) {
    if (_isLoading) return const LinearProgressIndicator();

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Text(
          '¿De qué club sos?',
          style: TextStyle(
            fontSize: 14,
            fontWeight: FontWeight.bold,
            color: Colors.grey,
          ),
        ),
        const SizedBox(height: 8),
        TextField(
          controller: _controller,
          focusNode: _focusNode,
          onChanged: _filterClubes,
          onTap: () => setState(() => _showSuggestions = true),
          decoration: InputDecoration(
            hintText: 'Buscá tu club...',
            prefixIcon: const Icon(Icons.search, size: 20),
            suffixIcon: _controller.text.isNotEmpty
                ? IconButton(
                    icon: const Icon(Icons.clear, size: 18),
                    onPressed: _clearSelection,
                  )
                : null,
            border: OutlineInputBorder(borderRadius: BorderRadius.circular(15), borderSide: BorderSide(color: Colors.grey.shade200)),
            enabledBorder: OutlineInputBorder(borderRadius: BorderRadius.circular(15), borderSide: BorderSide(color: Colors.grey.shade200)),
            filled: true,
            fillColor: Colors.grey.shade50,
            contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
          ),
        ),
        if (_showSuggestions) _buildSuggestions(),
        if (widget.selectedClub != null)
          Padding(
            padding: const EdgeInsets.only(top: 12, left: 4),
            child: Row(
              children: [
                if (widget.selectedClub!.escudoUrl != null)
                  Container(
                    width: 24, height: 24,
                    margin: const EdgeInsets.only(right: 10),
                    child: Image.network(widget.selectedClub!.escudoUrl!, fit: BoxFit.contain),
                  )
                else
                  const Icon(Icons.check_circle, color: Color(0xFF1B5E20), size: 20),
                Expanded(
                  child: Text(
                    'Club: ${widget.selectedClub!.nombre}',
                    style: const TextStyle(color: Color(0xFF1B5E20), fontWeight: FontWeight.bold, fontSize: 13),
                  ),
                ),
              ],
            ),
          ),
      ],
    );
  }

  Widget _buildSuggestions() {
    return Container(
      margin: const EdgeInsets.only(top: 4),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(15),
        boxShadow: [BoxShadow(color: Colors.black.withOpacity(0.05), blurRadius: 10, offset: const Offset(0, 4))],
      ),
      constraints: const BoxConstraints(maxHeight: 200),
      child: ListView.builder(
        padding: EdgeInsets.zero,
        shrinkWrap: true,
        itemCount: _filteredClubes.length > 20 ? 20 : _filteredClubes.length,
        itemBuilder: (context, index) {
          final club = _filteredClubes[index];
          return ListTile(
            dense: true,
            leading: club.escudoUrl != null 
              ? Image.network(club.escudoUrl!, width: 24, height: 24, fit: BoxFit.contain, errorBuilder: (_,__,___) => const Icon(Icons.sports_hockey))
              : const Icon(Icons.sports_hockey, size: 20),
            title: Text(club.nombre, style: const TextStyle(fontWeight: FontWeight.w600, fontSize: 13)),
            onTap: () => _selectClub(club),
          );
        },
      ),
    );
  }
}
