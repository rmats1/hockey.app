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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
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
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

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
    val state by viewModel.state.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableIntStateOf(0) }
    
    val accentColor = if (torneo.rama.contains("Fem", ignoreCase = true) || torneo.rama.contains("Dama", ignoreCase = true)) {
        Color(0xFFFF4081)
    } else {
        Color(0xFF2979FF)
    }

    val tabs = if (initialMode == TorneoDetalleMode.POSICIONES) listOf("POSICIONES") else listOf("FIXTURE", "GOLEADORES")

    LaunchedEffect(torneo.id) {
        viewModel.loadData(torneo.id)
    }

    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.shadow(8.dp),
                color = MaterialTheme.colorScheme.primary
            ) {
                Column {
                    TopAppBar(
                        title = {
                            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(torneo.nombre.uppercase(), fontSize = 14.sp, fontWeight = FontWeight.Black, color = Color.White, letterSpacing = 0.5.sp)
                                Text("TEMPORADA 2026", fontSize = 9.sp, color = Color.White.copy(alpha = 0.7f), fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = onBack) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                        actions = { Spacer(modifier = Modifier.width(48.dp)) } 
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
                                text = { Text(title, fontSize = 12.sp, fontWeight = FontWeight.Black, letterSpacing = 1.5.sp) }
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
                is TorneoDetalleState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = accentColor)
                is TorneoDetalleState.Error -> Text(s.message, modifier = Modifier.align(Alignment.Center), color = MaterialTheme.colorScheme.error)
                is TorneoDetalleState.Success -> {
                    if (initialMode == TorneoDetalleMode.POSICIONES) {
                        TablaList(s.posiciones, s.performancePoints, s.performanceLabels, s.targetTeam, accentColor)
                    } else {
                        if (selectedTab == 0) FixtureList(s.partidos, onMatchClick, accentColor) else GoleadoresList(s.goleadores, accentColor)
                    }
                }
            }
        }
    }
}

@Composable
fun TablaList(
    posiciones: ImmutableList<PosicionAHBA>,
    stats: ImmutableList<Float> = persistentListOf(),
    labels: ImmutableList<String> = persistentListOf(),
    teamName: String = "",
    accentColor: Color
) {
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        if (stats.isNotEmpty()) {
            item {
                StatsWidget(points = stats, labels = labels, teamName = teamName)
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
        items(posiciones) { p ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(16.dp), ambientColor = accentColor.copy(alpha = 0.1f)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.fillMaxHeight().width(5.dp).background(accentColor))
                    ListItem(
                        leadingContent = {
                            Box(
                                modifier = Modifier.size(32.dp).background(accentColor.copy(alpha = 0.1f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(p.puesto.toString(), color = accentColor, fontSize = 13.sp, fontWeight = FontWeight.Black)
                            }
                        },
                        headlineContent = { Text(p.clubNombre.uppercase(), fontWeight = FontWeight.Black, fontSize = 13.sp, letterSpacing = 0.5.sp) },
                        trailingContent = { 
                            Column(horizontalAlignment = Alignment.End) {
                                Text("${p.puntos}", fontWeight = FontWeight.Black, fontSize = 18.sp, color = Color.Black)
                                Text("PTS", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                            }
                        },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                    )
                }
            }
        }
    }
}

@Composable
fun FixtureList(partidos: ImmutableList<PartidoAHBA>, onMatchClick: (String) -> Unit = {}, accentColor: Color) {
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        items(partidos) { p ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onMatchClick(p.id) }
                    .shadow(6.dp, RoundedCornerShape(20.dp), ambientColor = accentColor.copy(alpha = 0.15f)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                    Box(modifier = Modifier.fillMaxHeight().width(6.dp).background(accentColor))
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Surface(color = accentColor.copy(alpha = 0.1f), shape = CircleShape) {
                                Text("FECHA ${p.numeroFecha}", modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp), fontSize = 9.sp, fontWeight = FontWeight.Black, color = accentColor)
                            }
                            Text(p.fechaHora ?: "", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.ExtraBold)
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            TeamInfo(p.nombreLocal, p.escudoLocal, Modifier.weight(1f))
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 8.dp)
                                    .background(Color(0xFFF8F9FA), RoundedCornerShape(12.dp))
                                    .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 16.dp, vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                val score = if (p.jugado) "${p.golesLocal ?: 0} - ${p.golesVisitante ?: 0}" else "VS"
                                Text(score, fontWeight = FontWeight.Black, fontSize = 20.sp, color = if(p.jugado) Color.Black else Color.LightGray)
                            }
                            TeamInfo(p.nombreVisitante, p.escudoVisitante, Modifier.weight(1f))
                        }
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
fun GoleadoresList(goleadores: ImmutableList<GoleadorAHBA>, accentColor: Color) {
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        items(goleadores) { g ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(5.dp, RoundedCornerShape(20.dp), ambientColor = accentColor.copy(alpha = 0.15f)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                    Box(modifier = Modifier.fillMaxHeight().width(6.dp).background(accentColor))
                    ListItem(
                        modifier = Modifier.padding(vertical = 8.dp),
                        leadingContent = {
                            Box(contentAlignment = Alignment.BottomEnd) {
                                Box(
                                    modifier = Modifier.size(52.dp).clip(CircleShape).background(accentColor.copy(alpha = 0.08f)).border(1.dp, accentColor.copy(alpha = 0.1f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (!g.fotoUrl.isNullOrEmpty()) {
                                        AsyncImage(model = g.fotoUrl, contentDescription = null, contentScale = ContentScale.Crop)
                                    } else {
                                        Text(if (g.nombreCompleto.isNotEmpty()) g.nombreCompleto[0].toString() else "?", fontWeight = FontWeight.Black, color = accentColor, fontSize = 22.sp)
                                    }
                                }
                                Surface(modifier = Modifier.size(18.dp), color = Color.White, shape = CircleShape, shadowElevation = 2.dp) {
                                    Box(contentAlignment = Alignment.Center) { Text("🏑", fontSize = 10.sp) }
                                }
                            }
                        },
                        headlineContent = { Text(g.nombreCompleto.uppercase(), fontWeight = FontWeight.Black, fontSize = 14.sp, letterSpacing = 0.5.sp) },
                        supportingContent = { Text(g.clubNombre.uppercase(), fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.ExtraBold) },
                        trailingContent = {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(g.goles.toString(), fontWeight = FontWeight.Black, fontSize = 26.sp, color = accentColor)
                                Text("GOLES", fontSize = 8.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.secondary)
                            }
                        },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                    )
                }
            }
        }
    }
}
