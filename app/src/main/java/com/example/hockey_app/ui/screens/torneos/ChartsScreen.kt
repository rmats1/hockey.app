package com.example.hockey_app.ui.screens.torneos

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.hockey_app.data.models.PosicionAHBA

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChartsScreen(
    onBack: () -> Unit,
    viewModel: TorneosViewModel = hiltViewModel()
) {
    var selectedRama by remember { mutableStateOf("Damas") }
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val posiciones by viewModel.posiciones.collectAsStateWithLifecycle()

    LaunchedEffect(selectedRama) {
        val torneoId = if (selectedRama == "Damas") "t1" else "t8"
        viewModel.loadEstadisticas(torneoId)
    }

    val maxPuntos = posiciones.maxOfOrNull { it.puntos } ?: 1
    val maxGoles = posiciones.maxOfOrNull { it.golesAFavor } ?: 1

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Visualización", fontWeight = FontWeight.Bold) },
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
            // Category Filter
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
                    CircularProgressIndicator()
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Puntos por Club
                    SectionCard("PUNTOS POR CLUB", Icons.Default.BarChart) {
                        posiciones.forEach { pos ->
                            BarChartItem(
                                label = pos.clubNombre,
                                value = pos.puntos,
                                max = maxPuntos,
                                barColor = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // Goles a favor
                    SectionCard("GOLES A FAVOR", Icons.Default.SportsSoccer) {
                        posiciones.forEach { pos ->
                            BarChartItem(
                                label = pos.clubNombre,
                                value = pos.golesAFavor,
                                max = maxGoles,
                                barColor = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    title,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp,
                    letterSpacing = 1.sp
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            content()
        }
    }
}

@Composable
private fun BarChartItem(
    label: String,
    value: Int,
    max: Int,
    barColor: Color
) {
    val progressTarget = if (max > 0) value.toFloat() / max.toFloat() else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progressTarget,
        animationSpec = tween(durationMillis = 800),
        label = "bar"
    )

    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Text("$value", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = barColor)
        }
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(Color(0xFFEEEEEE))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animatedProgress.coerceIn(0.02f, 1f))
                    .clip(RoundedCornerShape(5.dp))
                    .background(barColor)
            )
        }
    }
}
