package com.example.hockey_app.ui.screens.torneos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TorneosScreen(
    onMenuClick: () -> Unit,
    onTorneoClick: (TorneoResumen) -> Unit,
    viewModel: TorneosViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val busqueda by viewModel.busqueda.collectAsState()
    val filtroRama by viewModel.filtroRama.collectAsState()
    val filtroCategoria by viewModel.filtroCategoria.collectAsState()

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
                    .padding(top = 48.dp, bottom = 20.dp, start = 20.dp, end = 20.dp)
            ) {
                Column {
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
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "TABLA DE POSICIONES",
                                        color = Color.White,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp
                                    )
                                }
                                Text("Temporada Oficial 2026", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp)
                            }
                        }
                        IconButton(onClick = viewModel::refresh) {
                            Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = busqueda,
                        onValueChange = viewModel::onSearchChange,
                        placeholder = { Text("Buscar torneo o división...", color = Color.White.copy(alpha = 0.5f), fontSize = 13.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(20.dp)) },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White.copy(alpha = 0.12f),
                            unfocusedContainerColor = Color.White.copy(alpha = 0.12f),
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            cursorColor = Color.White,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                }
            }

            // Filters
            Column(modifier = Modifier.padding(16.dp)) {
                Row {
                    RamaChip("Damas", filtroRama == "F") { viewModel.onRamaChange(if (it) "F" else "Todas") }
                    Spacer(modifier = Modifier.width(8.dp))
                    RamaChip("Caballeros", filtroRama == "M") { viewModel.onRamaChange(if (it) "M" else "Todas") }
                }

                Spacer(modifier = Modifier.height(8.dp))

                val categorias = listOf("Todas", "Primera", "Intermedia", "Segunda", "Cuarta", "Quinta", "Sexta", "Septima", "Octava", "Novena", "10ma")
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(categorias) { cat ->
                        FilterChip(
                            selected = filtroCategoria == cat,
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
                when (val torneosState = state) {
                    is TorneosState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    is TorneosState.Success -> {
                        if (torneosState.torneos.isEmpty()) {
                            Text("No hay torneos registrados.", modifier = Modifier.align(Alignment.Center), color = Color.Gray)
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(torneosState.torneos) { torneo ->
                                    TorneoCard(torneo) { onTorneoClick(torneo) }
                                }
                            }
                        }
                    }
                    is TorneosState.Error -> {
                        Text(torneosState.message, modifier = Modifier.align(Alignment.Center), color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

@Composable
fun RamaChip(label: String, isSelected: Boolean, onSelectedChange: (Boolean) -> Unit) {
    FilterChip(
        selected = isSelected,
        onClick = { onSelectedChange(!isSelected) },
        label = { Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.secondary,
            selectedLabelColor = Color.White
        ),
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
fun TorneoCard(torneo: TorneoResumen, onClick: () -> Unit) {
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
                        Text("• División ${torneo.division}", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                    }
                }
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(16.dp))
        }
    }
}
