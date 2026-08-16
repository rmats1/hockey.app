package com.example.hockey_app.ui.screens.fixture

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hockey_app.data.models.ComentarioModel
import com.example.hockey_app.domain.auth.AuthRepository
import com.example.hockey_app.domain.competition.CompetitionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CommentsViewModel @Inject constructor(
    private val supabaseService: CompetitionRepository,
    private val authService: AuthRepository
) : ViewModel() {

    private val _comentarios = MutableStateFlow<List<ComentarioModel>>(emptyList())
    val comentarios: StateFlow<List<ComentarioModel>> = _comentarios

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun cargarComentarios(partidoId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _comentarios.value = supabaseService.getComentarios(partidoId)
            _isLoading.value = false
        }
    }

    fun agregarComentario(partidoId: String, texto: String, onSuccess: () -> Unit) {
        if (texto.isBlank()) return
        viewModelScope.launch {
            val user = authService.getCurrentUser() ?: authService.getLocalUser()
            if (user == null) {
                _error.value = "Debes iniciar sesión para comentar"
                return@launch
            }

            val comentario = ComentarioModel(
                id = UUID.randomUUID().toString(),
                partido_id = partidoId,
                user_id = user.id,
                user_name = user.nombre,
                texto = texto.trim(),
                fecha = Instant.now().toString()
            )

            val ok = supabaseService.postComentario(comentario)

            if (ok) {
                cargarComentarios(partidoId)
                onSuccess()
            } else {
                _error.value = "No se pudo enviar el comentario"
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsScreen(
    partidoId: String,
    titulo: String,
    onBack: () -> Unit,
    viewModel: CommentsViewModel = hiltViewModel()
) {
    var textInput by remember { mutableStateOf("") }
    val comentarios by viewModel.comentarios.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(partidoId) {
        viewModel.cargarComentarios(partidoId)
    }

    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(titulo, fontWeight = FontWeight.Bold, fontSize = 15.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else if (comentarios.isEmpty()) {
                    Text(
                        "Sé el primero en comentar",
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(comentarios) { comentario ->
                            ComentarioCard(comentario)
                        }
                    }
                }
            }

            // Input Bar
            Surface(
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = textInput,
                        onValueChange = { textInput = it },
                        placeholder = { Text("Escribí un comentario...", fontSize = 13.sp) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(24.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF5F5F5),
                            unfocusedContainerColor = Color(0xFFF5F5F5),
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            viewModel.agregarComentario(partidoId, textInput) {
                                textInput = ""
                            }
                        }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Enviar",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ComentarioCard(c: ComentarioModel) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        c.user_name?.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(c.user_name ?: "Usuario", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(modifier = Modifier.weight(1f))
                Text(formatDate(c.fecha), fontSize = 10.sp, color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(c.texto ?: "", fontSize = 13.sp, lineHeight = 18.sp)
        }
    }
}

private fun formatDate(dateStr: String?): String {
    if (dateStr == null) return ""
    return try {
        val parts = dateStr.split("T")
        if (parts.isNotEmpty()) parts[0] else ""
    } catch (e: Exception) {
        ""
    }
}
