package com.example.hockey_app.ui.screens.torneos

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ImageNotSupported
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.hockey_app.data.models.GoleadorAHBA
import com.example.hockey_app.data.models.PartidoAHBA
import com.example.hockey_app.data.models.PosicionAHBA
import com.example.hockey_app.data.models.TorneoResumen

enum class TorneoDetalleMode { POSICIONES, FIXTURE }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TorneoDetalleScreen(
    torneo: TorneoResumen,
    initialMode: TorneoDetalleMode = TorneoDetalleMode.FIXTURE,
    onBack: () -> Unit,
    onMatchClick: (String) -> Unit = {},
    viewModel: TorneoDetalleViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    
    val tabs = if (initialMode == TorneoDetalleMode.POSICIONES) listOf("POSICIONES") else listOf("FIXTURE", "GOLEADORES")

    LaunchedEffect(torneo.id) {
        viewModel.loadData(torneo.id)
    }

    Scaffold(
        topBar = {
            Surface(color = MaterialTheme.colorScheme.primary, shadowElevation = 4.dp) {
                Column {
                    TopAppBar(
                        title = {
                            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(torneo.nombre.uppercase(), fontSize = 13.sp, fontWeight = FontWeight.Black, color = Color.White)
                                Text("TEMPORADA 2026", fontSize = 9.sp, color = Color.White.copy(alpha = 0.7f), fontWeight = FontWeight.Bold)
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = onBack) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                        actions = { Spacer(modifier = Modifier.width(48.dp)) } // Balance title centering
                    )
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Color.Transparent,
                        contentColor = Color.White,
                        indicator = { tabPositions ->
                            if (selectedTab < tabPositions.size) {
                                TabRowDefaults.SecondaryIndicator(
                                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                    color = MaterialTheme.colorScheme.secondary,
                                    height = 4.dp
                                )
                            }
                        },
                        divider = {}
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                text = { Text(title, fontSize = 13.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp) }
                            )
                        }
                    }
                }
            }
        },
        containerColor = Color(0xFFF5F5F5)
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val s = state) {
                is TorneoDetalleState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is TorneoDetalleState.Error -> Text(s.message, modifier = Modifier.align(Alignment.Center), color = MaterialTheme.colorScheme.error)
                is TorneoDetalleState.Success -> {
                    if (initialMode == TorneoDetalleMode.POSICIONES) {
                        TablaList(s.posiciones, s.performancePoints, s.performanceLabels, s.targetTeam)
                    } else {
                        if (selectedTab == 0) FixtureList(s.partidos, onMatchClick) else GoleadoresList(s.goleadores)
                    }
                }
            }
        }
    }
}

@Composable
fun TablaList(
    posiciones: List<PosicionAHBA>,
    stats: List<Float> = emptyList(),
    labels: List<String> = emptyList(),
    teamName: String = ""
) {
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (stats.isNotEmpty()) {
            item {
                StatsWidget(points = stats, labels = labels, teamName = teamName)
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
        items(posiciones) { p ->
            Card(
                shape = RoundedCornerShape(15.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                ListItem(
                    leadingContent = {
                        Box(
                            modifier = Modifier.size(30.dp).background(MaterialTheme.colorScheme.primary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(p.puesto.toString(), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    },
                    headlineContent = { Text(p.clubNombre, fontWeight = FontWeight.Black, fontSize = 13.sp) },
                    trailingContent = { Text("${p.puntos} PTS", fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary) }
                )
            }
        }
    }
}

@Composable
fun FixtureList(partidos: List<PartidoAHBA>, onMatchClick: (String) -> Unit = {}) {
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(partidos) { p ->
            Card(
                modifier = Modifier.fillMaxWidth().clickable { onMatchClick(p.id) },
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("FECHA ${p.numeroFecha}", fontSize = 10.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                        Text(p.horario ?: "", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TeamInfo(p.nombreLocal, p.escudoLocal, Modifier.weight(1f))
                        Box(
                            modifier = Modifier.padding(horizontal = 12.dp).background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp)).padding(horizontal = 16.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            val score = if (p.jugado) "${p.golesLocal ?: 0} - ${p.golesVisitante ?: 0}" else "vs"
                            Text(score, fontWeight = FontWeight.Black, fontSize = 20.sp)
                        }
                        TeamInfo(p.nombreVisitante, p.escudoVisitante, Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun TeamInfo(name: String, escudo: String?, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier.size(55.dp).background(Color.White, CircleShape).border(1.dp, Color(0xFFEEEEEE), CircleShape).padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            if (!escudo.isNullOrEmpty()) {
                AsyncImage(model = escudo, contentDescription = null, contentScale = ContentScale.Fit)
            } else {
                Text("🏑", fontSize = 24.sp)
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(name.uppercase(), textAlign = TextAlign.Center, fontSize = 11.sp, fontWeight = FontWeight.Black, maxLines = 2, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
fun GoleadoresList(goleadores: List<GoleadorAHBA>) {
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(goleadores) { g ->
            Card(
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                ListItem(
                    modifier = Modifier.padding(vertical = 10.dp),
                    leadingContent = {
                        Box(contentAlignment = Alignment.BottomEnd) {
                            Box(
                                modifier = Modifier.size(50.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (!g.fotoUrl.isNullOrEmpty()) {
                                    AsyncImage(model = g.fotoUrl, contentDescription = null, contentScale = ContentScale.Crop)
                                } else {
                                    Text(if (g.nombreCompleto.isNotEmpty()) g.nombreCompleto[0].toString() else "?", fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary, fontSize = 22.sp)
                                }
                            }
                            Box(modifier = Modifier.size(18.dp).background(Color.White, CircleShape), contentAlignment = Alignment.Center) {
                                Text("🏑", fontSize = 12.sp)
                            }
                        }
                    },
                    headlineContent = { Text(g.nombreCompleto.uppercase(), fontWeight = FontWeight.Black, fontSize = 14.sp) },
                    supportingContent = { Text(g.clubNombre, fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold) },
                    trailingContent = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(g.goles.toString(), fontWeight = FontWeight.Black, fontSize = 24.sp, color = MaterialTheme.colorScheme.primary)
                            Text("GOLES", fontSize = 8.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.secondary)
                        }
                    }
                )
            }
        }
    }
}
