package com.example.hockey_app.ui.screens.torneos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hockey_app.data.models.GoleadorAHBA
import com.example.hockey_app.data.models.PosicionAHBA

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstadisticasScreen(
    onBack: () -> Unit,
    viewModel: TorneosViewModel = hiltViewModel()
) {
    var selectedRama by remember { mutableStateOf("Damas") }
    var selectedTab by remember { mutableStateOf(0) }
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val goleadores by viewModel.goleadores.collectAsStateWithLifecycle()
    val posiciones by viewModel.posiciones.collectAsStateWithLifecycle()

    LaunchedEffect(selectedRama) {
        val torneoId = if (selectedRama == "Damas") "t1" else "t8"
        viewModel.loadEstadisticas(torneoId)
    }

    val totalGoles = posiciones.sumOf { it.golesAFavor }
    val totalPartidos = posiciones.sumOf { it.partidosJugados }
    val promedioGoles = if (totalPartidos > 0) totalGoles.toDouble() / totalPartidos else 0.0
    val maxGoleador = goleadores.firstOrNull()?.goles ?: 0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Estadísticas", fontWeight = FontWeight.Bold) },
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
            // Rama selector
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    listOf("Damas", "Caballeros").forEach { rama ->
                        val sel = selectedRama == rama
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(15.dp),
                            color = if (sel) Color.White else Color.White.copy(alpha = 0.05f),
                            onClick = { selectedRama = rama }
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(12.dp)) {
                                Text(
                                    rama.uppercase(),
                                    color = if (sel) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.7f),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 11.sp,
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                    }
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Stats summary grid
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            StatCard("GOLES", "$totalGoles", Icons.Default.SportsSoccer, MaterialTheme.colorScheme.primary, Modifier.weight(1f))
                            StatCard("PARTIDOS", "$totalPartidos", Icons.Default.Sports, Color(0xFF2196F3), Modifier.weight(1f))
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            StatCard("PROMEDIO", "%.1f".format(promedioGoles), Icons.Default.ShowChart, Color(0xFF4CAF50), Modifier.weight(1f))
                            StatCard("MÁX. GOLES", "$maxGoleador", Icons.Default.Star, MaterialTheme.colorScheme.secondary, Modifier.weight(1f))
                        }
                    }

                    // Tabs
                    item {
                        TabRow(
                            selectedTabIndex = selectedTab,
                            containerColor = Color.White,
                            contentColor = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clip(RoundedCornerShape(16.dp))
                        ) {
                            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                                Text("Top Goleadores", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                                Text("Tabla de Posiciones", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }

                    if (selectedTab == 0) {
                        if (goleadores.isEmpty()) {
                            item { EmptyState("No hay datos de goleadores") }
                        } else {
                            itemsIndexed(goleadores.take(10)) { idx, goleador ->
                                GoleadorRow(idx + 1, goleador)
                            }
                        }
                    } else {
                        if (posiciones.isEmpty()) {
                            item { EmptyState("No hay tabla de posiciones disponible") }
                        } else {
                            itemsIndexed(posiciones) { idx, pos ->
                                PosicionRow(idx + 1, pos)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, icon: ImageVector, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(label, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = Color.Gray, letterSpacing = 0.5.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, fontSize = 26.sp, fontWeight = FontWeight.Black, color = color)
        }
    }
}

@Composable
private fun GoleadorRow(position: Int, goleador: GoleadorAHBA) {
    val medalColors = listOf(Color(0xFFFFD700), Color(0xFFC0C0C0), Color(0xFFCD7F32))
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(if (position <= 3) medalColors[position - 1].copy(alpha = 0.15f) else Color(0xFFF5F5F5)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "$position°",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 11.sp,
                    color = if (position <= 3) medalColors[position - 1] else Color.Gray
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(goleador.nombreCompleto, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(goleador.clubNombre, fontSize = 11.sp, color = Color.Gray)
            }
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text("${goleador.goles}", fontWeight = FontWeight.Black, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
private fun PosicionRow(position: Int, pos: PosicionAHBA) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "$position",
                modifier = Modifier.width(24.dp),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp,
                color = if (position == 1) Color(0xFFFFD700) else Color.Gray
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(pos.clubNombre, fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCell("PJ", "${pos.partidosJugados}")
                StatCell("GF", "${pos.golesAFavor}", color = MaterialTheme.colorScheme.primary)
                StatCell("GC", "${pos.golesEnContra}", color = Color(0xFFF44336))
                StatCell("Pts", "${pos.puntos}", color = MaterialTheme.colorScheme.secondary, bold = true)
            }
        }
    }
}

@Composable
private fun StatCell(label: String, value: String, color: Color = Color.Gray, bold: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(32.dp)) {
        Text(value, fontSize = 13.sp, fontWeight = if (bold) FontWeight.ExtraBold else FontWeight.Normal, color = color)
        Text(label, fontSize = 9.sp, color = Color.Gray)
    }
}

@Composable
private fun EmptyState(message: String) {
    Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
        Text(message, color = Color.Gray, fontSize = 13.sp)
    }
}
