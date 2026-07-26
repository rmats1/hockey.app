import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import '../models/club_model.dart';
import '../models/user_model.dart';
import '../utils/colors.dart';
import '../utils/constants.dart';
import '../services/auth_service.dart';
import 'widgets/club_search_field.dart';
import 'home_screen.dart';

class RegisterScreen extends StatefulWidget {
  const RegisterScreen({super.key});

  @override
  State<RegisterScreen> createState() => _RegisterScreenState();
}

class _RegisterScreenState extends State<RegisterScreen> {
  final _emailController = TextEditingController();
  final _passwordController = TextEditingController();
  final _confirmPasswordController = TextEditingController();
  final _nameController = TextEditingController();
  final _numeroCamisetaController = TextEditingController();
  
  final _formKey = GlobalKey<FormState>();
  final _authService = AuthService();
  
  String _userType = 'jugador';
  String _rama = 'Damas';
  String _categoria = 'Primera';
  String? _division;
  String? _posicion;
  String? _rolCuerpoTecnico;
  
  bool _obscurePassword = true;
  bool _obscureConfirmPassword = true;
  bool _isLoading = false;
  
  Club? _selectedClub;
  DateTime? _fechaNacimiento;

  @override
  void dispose() {
    _emailController.dispose();
    _passwordController.dispose();
    _confirmPasswordController.dispose();
    _nameController.dispose();
    _numeroCamisetaController.dispose();
    super.dispose();
  }

  Future<void> _register() async {
    if (_selectedClub == null) {
      _showSnackBar('Por favor seleccioná tu club', isError: true);
      return;
    }
    if (_userType == 'jugador' && _division == null) {
      _showSnackBar('Por favor seleccioná tu división', isError: true);
      return;
    }
    
    if (_formKey.currentState!.validate()) {
      setState(() => _isLoading = true);
      
      final user = UserModel(
        id: DateTime.now().millisecondsSinceEpoch.toString(),
        email: _emailController.text.trim(),
        nombre: _nameController.text.trim(),
        userType: _userType,
        rama: _rama,
        categoria: _categoria,
        division: _division,
        club: _selectedClub!,
        numeroCamiseta: _userType == 'jugador' ? int.tryParse(_numeroCamisetaController.text) : null,
        posicion: _userType == 'jugador' ? _posicion : null,
        rolCuerpoTecnico: _userType == 'cuerpo_tecnico' ? _rolCuerpoTecnico : null,
        fechaNacimiento: _fechaNacimiento,
        fechaRegistro: DateTime.now(),
      );
      
      final success = await _authService.registerUser(user);
      if (!mounted) return;
      
      if (success) {
        await _authService.saveCurrentUser(user);
        setState(() => _isLoading = false);
        _showSuccessDialog();
      } else {
        setState(() => _isLoading = false);
        _showSnackBar('Este email ya está registrado', isError: true);
      }
    }
  }

  void _showSuccessDialog() {
    showDialog(
      context: context,
      barrierDismissible: false,
      builder: (context) => AlertDialog(
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(24)),
        title: const Row(children: [Icon(Icons.check_circle, color: AppColors.primary, size: 30), SizedBox(width: 10), Text('¡Bienvenido!')]),
        content: Text('Tu cuenta ha sido creada exitosamente.', style: GoogleFonts.montserrat()),
        actions: [
          ElevatedButton(
            onPressed: () => Navigator.pushReplacement(context, MaterialPageRoute(builder: (context) => const HomeScreen())),
            child: const Text('EMPEZAR'),
          ),
        ],
      ),
    );
  }

  void _showSnackBar(String message, {bool isError = false}) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(content: Text(message, style: GoogleFonts.montserrat()), backgroundColor: isError ? AppColors.error : AppColors.primary, behavior: SnackBarBehavior.floating),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      appBar: AppBar(
        title: Text('Registro', style: GoogleFonts.montserrat(fontWeight: FontWeight.bold)),
        backgroundColor: Colors.white,
        foregroundColor: AppColors.textPrimary,
        elevation: 0,
      ),
      body: SafeArea(
        child: SingleChildScrollView(
          padding: const EdgeInsets.all(30.0),
          child: Form(
            key: _formKey,
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                Text('CREAR CUENTA', style: GoogleFonts.montserrat(fontSize: 24, fontWeight: FontWeight.w900, color: AppColors.primary, letterSpacing: -1)),
                const SizedBox(height: 8),
                const Text('Unite a la comunidad del hockey de Buenos Aires', style: TextStyle(color: Colors.grey, fontSize: 13)),
                const SizedBox(height: 32),

                _buildSectionHeader('ROL Y RAMA'),
                const SizedBox(height: 16),
                Row(
                  children: [
                    _buildSelectionBtn('JUGADOR/A', 'jugador', Icons.sports_hockey, _userType == 'jugador', (v) => setState(() => _userType = v)),
                    const SizedBox(width: 12),
                    _buildSelectionBtn('C. TÉCNICO', 'cuerpo_tecnico', Icons.groups_rounded, _userType == 'cuerpo_tecnico', (v) => setState(() => _userType = v)),
                  ],
                ),
                const SizedBox(height: 12),
                Row(
                  children: [
                    _buildSmallSelect('DAMAS', 'Damas', _rama == 'Damas', (v) => setState(() => _rama = v)),
                    const SizedBox(width: 8),
                    _buildSmallSelect('CABALLEROS', 'Caballeros', _rama == 'Caballeros', (v) => setState(() => _rama = v)),
                  ],
                ),
                
                const SizedBox(height: 24),
                _buildSectionHeader('TU CLUB'),
                const SizedBox(height: 16),
                ClubSearchField(
                  selectedClub: _selectedClub,
                  onClubSelected: (club) => setState(() => _selectedClub = club),
                  userType: _userType,
                ),

                const SizedBox(height: 24),
                _buildSectionHeader('CATEGORÍA Y DIVISIÓN'),
                const SizedBox(height: 12),
                _buildDropdownField('Categoría (Torneo)', Icons.emoji_events, AppConstants.categorias, _categoria, (v) => setState(() => _categoria = v!)),
                const SizedBox(height: 12),
                _buildDivisionSelector(),

                if (_userType == 'jugador') ...[
                  const SizedBox(height: 12),
                  _buildModernField(_numeroCamisetaController, 'Número de camiseta', Icons.numbers_rounded, type: TextInputType.number),
                  const SizedBox(height: 16),
                  _buildDropdownField('Posición', Icons.sports, AppConstants.posicionesJugador, _posicion, (v) => setState(() => _posicion = v)),
                ],

                if (_userType == 'cuerpo_tecnico') ...[
                  const SizedBox(height: 12),
                  _buildDropdownField('Rol', Icons.work_rounded, AppConstants.rolesCuerpoTecnico, _rolCuerpoTecnico, (v) => setState(() => _rolCuerpoTecnico = v)),
                ],

                const SizedBox(height: 32),
                _buildSectionHeader('DATOS PERSONALES'),
                const SizedBox(height: 16),
                _buildModernField(_nameController, 'Nombre completo', Icons.person_outline),
                const SizedBox(height: 16),
                _buildModernField(_emailController, 'Email', Icons.alternate_email_rounded, type: TextInputType.emailAddress),
                const SizedBox(height: 16),
                _buildModernField(_passwordController, 'Contraseña', Icons.lock_outline_rounded, obscure: _obscurePassword, isPassword: true, onToggle: () => setState(() => _obscurePassword = !_obscurePassword)),
                const SizedBox(height: 16),
                _buildModernField(_confirmPasswordController, 'Confirmar contraseña', Icons.lock_clock_outlined, obscure: _obscureConfirmPassword, isPassword: true, onToggle: () => setState(() => _obscureConfirmPassword = !_obscureConfirmPassword)),
                
                const SizedBox(height: 40),
                SizedBox(
                  height: 55,
                  child: ElevatedButton(
                    onPressed: _isLoading ? null : _register,
                    style: ElevatedButton.styleFrom(backgroundColor: AppColors.primary, foregroundColor: Colors.white, elevation: 8, shadowColor: AppColors.primary.withOpacity(0.4)),
                    child: _isLoading ? const CircularProgressIndicator(color: Colors.white) : Text('CREAR MI CUENTA', style: GoogleFonts.montserrat(fontWeight: FontWeight.bold, letterSpacing: 1)),
                  ),
                ),
                const SizedBox(height: 30),
              ],
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildSectionHeader(String title) {
    return Text(title, style: GoogleFonts.montserrat(fontSize: 10, fontWeight: FontWeight.w800, color: Colors.grey, letterSpacing: 1));
  }

  Widget _buildSelectionBtn(String label, String value, IconData icon, bool isSelected, Function(String) onTap) {
    return Expanded(
      child: GestureDetector(
        onTap: () => onTap(value),
        child: AnimatedContainer(
          duration: const Duration(milliseconds: 200),
          padding: const EdgeInsets.symmetric(vertical: 20),
          decoration: BoxDecoration(
            color: isSelected ? AppColors.primary : Colors.grey.shade50,
            borderRadius: BorderRadius.circular(20),
            border: Border.all(color: isSelected ? AppColors.primary : Colors.grey.shade200, width: 2),
          ),
          child: Column(
            children: [
              Icon(icon, color: isSelected ? Colors.white : Colors.grey.shade400, size: 28),
              const SizedBox(height: 10),
              Text(label, style: GoogleFonts.montserrat(color: isSelected ? Colors.white : Colors.grey.shade600, fontSize: 10, fontWeight: FontWeight.w800)),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildSmallSelect(String label, String value, bool isSelected, Function(String) onTap) {
    return Expanded(
      child: GestureDetector(
        onTap: () => onTap(value),
        child: Container(
          padding: const EdgeInsets.symmetric(vertical: 10),
          decoration: BoxDecoration(color: isSelected ? AppColors.secondary : Colors.grey.shade50, borderRadius: BorderRadius.circular(12), border: Border.all(color: isSelected ? AppColors.secondary : Colors.grey.shade200)),
          child: Center(child: Text(label, style: GoogleFonts.montserrat(color: isSelected ? Colors.white : Colors.grey.shade600, fontSize: 10, fontWeight: FontWeight.w800))),
        ),
      ),
    );
  }

  Widget _buildDivisionSelector() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Text('División (A, B, C...)', style: TextStyle(fontSize: 11, fontWeight: FontWeight.bold, color: Colors.grey)),
        const SizedBox(height: 8),
        Wrap(
          spacing: 8, runSpacing: 8,
          children: AppConstants.divisiones.map((d) {
            final sel = _division == d;
            return GestureDetector(
              onTap: () => setState(() => _division = d),
              child: Container(
                padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 8),
                decoration: BoxDecoration(color: sel ? AppColors.primary : Colors.white, borderRadius: BorderRadius.circular(20), border: Border.all(color: sel ? AppColors.primary : Colors.grey.shade300)),
                child: Text(d, style: TextStyle(color: sel ? Colors.white : Colors.grey.shade700, fontSize: 12, fontWeight: sel ? FontWeight.bold : FontWeight.normal)),
              ),
            );
          }).toList(),
        ),
      ],
    );
  }

  Widget _buildModernField(TextEditingController controller, String label, IconData icon, {bool obscure = false, bool isPassword = false, VoidCallback? onToggle, TextInputType type = TextInputType.text}) {
    return Container(
      decoration: BoxDecoration(color: Colors.grey.shade50, borderRadius: BorderRadius.circular(15), border: Border.all(color: Colors.grey.shade200)),
      child: TextFormField(
        controller: controller,
        obscureText: obscure,
        keyboardType: type,
        style: GoogleFonts.montserrat(fontSize: 14, fontWeight: FontWeight.w500),
        decoration: InputDecoration(
          labelText: label,
          labelStyle: const TextStyle(color: Colors.grey, fontSize: 13),
          prefixIcon: Icon(icon, color: AppColors.primary, size: 20),
          suffixIcon: isPassword ? IconButton(icon: Icon(obscure ? Icons.visibility_off : Icons.visibility, color: Colors.grey, size: 18), onPressed: onToggle) : null,
          border: InputBorder.none,
          contentPadding: const EdgeInsets.symmetric(vertical: 15, horizontal: 20),
        ),
        validator: (v) => v == null || v.isEmpty ? 'Campo obligatorio' : null,
      ),
    );
  }

  Widget _buildDropdownField(String label, IconData icon, List<String> items, String? value, Function(String?) onChange) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 12),
      decoration: BoxDecoration(color: Colors.grey.shade50, borderRadius: BorderRadius.circular(15), border: Border.all(color: Colors.grey.shade200)),
      child: DropdownButtonFormField<String>(
        value: value,
        decoration: InputDecoration(
          labelText: label,
          labelStyle: const TextStyle(color: Colors.grey, fontSize: 13),
          prefixIcon: Icon(icon, color: AppColors.primary, size: 20),
          border: InputBorder.none,
        ),
        items: items.map((i) => DropdownMenuItem(value: i, child: Text(i, style: const TextStyle(fontSize: 14)))).toList(),
        onChanged: onChange,
        validator: (v) => v == null ? 'Campo obligatorio' : null,
      ),
    );
  }
}
