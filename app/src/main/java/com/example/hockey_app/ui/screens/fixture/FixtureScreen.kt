package com.example.hockey_app.ui.screens.fixture

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hockey_app.data.models.TorneoResumen
import com.example.hockey_app.ui.screens.torneos.RamaChip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FixtureScreen(
    onMenuClick: () -> Unit,
    onTorneoClick: (TorneoResumen) -> Unit,
    viewModel: FixtureViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val rama by viewModel.rama.collectAsStateWithLifecycle()
    val categoria by viewModel.categoria.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = Color(0xFFF5F5F5)
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(MaterialTheme.colorScheme.primary, Color(0xFF0F2B48))
                        )
                    )
                    .padding(top = 48.dp, bottom = 24.dp, start = 20.dp, end = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onMenuClick) {
                            Icon(Icons.Default.Menu, contentDescription = null, tint = Color.White)
                        }
                        Column {
                            Text(
                                "FIXTURE Y GOLEADORES",
                                color = MaterialTheme.colorScheme.secondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 2.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Temporada 2026",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 22.sp,
                                letterSpacing = (-1).sp
                            )
                        }
                    }
                    IconButton(onClick = viewModel::refresh) {
                        Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }
            }

            // Filters
            Column(modifier = Modifier.padding(16.dp)) {
                Row {
                    RamaChip("Damas", rama == "F") { viewModel.onRamaChange(if (it) "F" else "M") }
                    Spacer(modifier = Modifier.width(8.dp))
                    RamaChip("Caballeros", rama == "M") { viewModel.onRamaChange(if (it) "M" else "F") }
                }

                Spacer(modifier = Modifier.height(8.dp))

                val categorias = listOf("Todas", "Primera", "Intermedia", "Segunda", "Cuarta", "Quinta", "Sexta", "Septima", "Octava", "Novena", "10ma")
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(categorias) { cat ->
                        FilterChip(
                            selected = categoria == cat,
                            onClick = { viewModel.onCategoriaChange(cat) },
                            label = { Text(cat, fontSize = 11.sp) },
                            shape = RoundedCornerShape(20.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            // List
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                when (val fixtureState = state) {
                    is FixtureState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    is FixtureState.Success -> {
                        if (fixtureState.torneos.isEmpty()) {
                            Text("No hay fixtures para esta selección.", modifier = Modifier.align(Alignment.Center), color = Color.Gray)
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(fixtureState.torneos) { torneo ->
                                    FixtureTorneoCard(torneo) { onTorneoClick(torneo) }
                                }
                            }
                        }
                    }
                    is FixtureState.Error -> {
                        Text(fixtureState.message, modifier = Modifier.align(Alignment.Center), color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

@Composable
fun FixtureTorneoCard(torneo: TorneoResumen, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(torneo.nombre, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            torneo.categoria,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    if (torneo.division.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("• ${torneo.division}", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                    }
                }
            }
            Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(16.dp))
        }
    }
}
