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
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hockey_app.data.models.ComentarioModel
import com.example.hockey_app.data.models.PartidoAHBA
import com.example.hockey_app.ui.screens.torneos.TeamInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchDetailScreen(
    matchId: String,
    onBack: () -> Unit,
    viewModel: MatchDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var commentText by remember { mutableStateOf("") }

    LaunchedEffect(matchId) {
        viewModel.loadMatch(matchId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("DETALLE DEL PARTIDO", fontWeight = FontWeight.Black, fontSize = 16.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding).background(Color(0xFFF5F5F5))) {
            when (val s = state) {
                is MatchDetailState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is MatchDetailState.Error -> Text(s.message, modifier = Modifier.align(Alignment.Center), color = MaterialTheme.colorScheme.error)
                is MatchDetailState.Success -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        MatchScoreboard(s.partido)
                        
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            item { PredictionSection(s.partido) { l, v -> viewModel.postPrediccion(matchId, l, v) } }
                            
                            item { 
                                Text("COMENTARIOS", fontWeight = FontWeight.Black, fontSize = 12.sp, color = Color.Gray, letterSpacing = 1.sp)
                            }
                            
                            items(s.comentarios) { comentario ->
                                CommentItem(comentario)
                            }
                        }
                        
                        // Comment Input
                        CommentInput(
                            text = commentText,
                            onTextChange = { commentText = it },
                            onSend = {
                                if (commentText.isNotBlank()) {
                                    viewModel.postComentario(matchId, commentText)
                                    commentText = ""
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MatchScoreboard(partido: PartidoAHBA) {
    Surface(
        color = MaterialTheme.colorScheme.primary,
        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp),
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "FECHA ${partido.numeroFecha} • ${partido.horario ?: "A confirmar"}",
                color = Color.White.copy(alpha = 0.7f),
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TeamInfo(partido.nombreLocal, partido.escudoLocal, Modifier.weight(1f))
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val score = if (partido.jugado) "${partido.golesLocal} - ${partido.golesVisitante}" else "VS"
                    Text(score, color = Color.White, fontWeight = FontWeight.Black, fontSize = 36.sp)
                    if (partido.jugado) {
                        Surface(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "FINALIZADO",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
                
                TeamInfo(partido.nombreVisitante, partido.escudoVisitante, Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun PredictionSection(partido: PartidoAHBA, onPredict: (Int, Int) -> Unit) {
    if (partido.jugado) return
    
    var localGoles by remember { mutableStateOf("0") }
    var visitGoles by remember { mutableStateOf("0") }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("PRODE: ¿QUIÉN GANA?", fontWeight = FontWeight.Black, fontSize = 12.sp, letterSpacing = 0.5.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                PredictionInput(localGoles) { localGoles = it }
                Text("-", modifier = Modifier.padding(horizontal = 16.dp), fontWeight = FontWeight.Black, fontSize = 20.sp)
                PredictionInput(visitGoles) { visitGoles = it }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { onPredict(localGoles.toIntOrNull() ?: 0, visitGoles.toIntOrNull() ?: 0) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("ENVIAR PREDICCIÓN", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun PredictionInput(value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = { if (it.length <= 2 && it.all { c -> c.isDigit() }) onValueChange(it) },
        modifier = Modifier.width(60.dp),
        textStyle = androidx.compose.ui.text.TextStyle(textAlign = TextAlign.Center, fontWeight = FontWeight.Black, fontSize = 18.sp),
        shape = RoundedCornerShape(12.dp),
        singleLine = true
    )
}

@Composable
fun CommentItem(comentario: ComentarioModel) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Text(comentario.user_name.take(1).uppercase(), fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(comentario.user_name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text("hace poco", fontSize = 10.sp, color = Color.Gray)
            }
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(topStart = 0.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
                shadowElevation = 1.dp
            ) {
                Text(
                    text = comentario.texto,
                    modifier = Modifier.padding(12.dp),
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
fun CommentInput(text: String, onTextChange: (String) -> Unit, onSend: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 16.dp
    ) {
        Row(
            modifier = Modifier.padding(12.dp).navigationBarsPadding().imePadding(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = text,
                onValueChange = onTextChange,
                placeholder = { Text("Escribir un comentario...", fontSize = 14.sp) },
                modifier = Modifier.weight(1f),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                maxLines = 3
            )
            IconButton(onClick = onSend) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
