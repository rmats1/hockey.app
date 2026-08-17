package com.example.hockey_app.ui.screens.club

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.SportsHockey
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.border
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hockey_app.data.models.ClubModel
import coil.compose.AsyncImage

import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompareClubsScreen(
    onBack: () -> Unit,
    viewModel: CompareClubsViewModel = hiltViewModel()
) {
    val clubes by viewModel.clubes.collectAsStateWithLifecycle()
    var club1 by remember { mutableStateOf<ClubModel?>(null) }
    var club2 by remember { mutableStateOf<ClubModel?>(null) }
    var selectingForClub by remember { mutableStateOf<Int?>(null) }

    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.shadow(8.dp),
                color = MaterialTheme.colorScheme.primary
            ) {
                TopAppBar(
                    title = { Text("COMPARAR CLUBES", fontWeight = FontWeight.Black, fontSize = 16.sp, letterSpacing = 1.sp) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
            }
        },
        containerColor = Color(0xFFF5F5F5)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ClubSelectorBox(
                    label = "Club 1",
                    selectedClub = club1,
                    modifier = Modifier.weight(1f),
                    onClick = { selectingForClub = 1 }
                )

                Text(
                    "VS",
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                ClubSelectorBox(
                    label = "Club 2",
                    selectedClub = club2,
                    modifier = Modifier.weight(1f),
                    onClick = { selectingForClub = 2 }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (club1 != null && club2 != null) {
                ComparisonCard(club1!!, club2!!)
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Compare,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Seleccioná dos clubes para comparar sus estadísticas cara a cara.",
                            color = Color.Gray,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }

    if (selectingForClub != null) {
        var searchQuery by remember { mutableStateOf("") }
        val filteredClubes = remember(searchQuery, clubes) {
            if (searchQuery.isBlank()) clubes
            else clubes.filter { it.nombre.contains(searchQuery, ignoreCase = true) }
        }

        ModalBottomSheet(
            onDismissRequest = { selectingForClub = null }
        ) {
            Column(modifier = Modifier.padding(16.dp).fillMaxHeight(0.8f)) {
                Text(
                    "Seleccionar Club",
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar club...") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = { Icon(Icons.Default.SportsHockey, contentDescription = null) },
                    singleLine = true
                )

                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(filteredClubes) { club ->
                        ListItem(
                            headlineContent = { Text(club.nombre, fontWeight = FontWeight.SemiBold) },
                            leadingContent = {
                                AsyncImage(
                                    model = club.escudoUrl,
                                    contentDescription = "Escudo de ${club.nombre}",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.size(40.dp)
                                )
                            },
                            modifier = Modifier.clickable {
                                if (selectingForClub == 1) club1 = club else club2 = club
                                selectingForClub = null
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ClubSelectorBox(
    label: String,
    selectedClub: ClubModel?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val accentColor = MaterialTheme.colorScheme.primary

    Card(
        modifier = modifier
            .shadow(6.dp, RoundedCornerShape(20.dp), ambientColor = accentColor.copy(alpha = 0.1f))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(Color(0xFFF8F9FA), CircleShape)
                    .border(1.dp, Color(0xFFEEEEEE), CircleShape)
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                if (selectedClub?.escudoUrl != null) {
                    AsyncImage(
                        model = selectedClub.escudoUrl,
                        contentDescription = "Escudo de ${selectedClub.nombre}",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(Icons.Default.SportsHockey, contentDescription = null, modifier = Modifier.size(32.dp), tint = accentColor)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(label.uppercase(), fontSize = 9.sp, color = Color.Gray, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                selectedClub?.nombre?.uppercase() ?: "ELEGIR...",
                fontWeight = FontWeight.Black,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = if (selectedClub != null) Color.Black else Color.LightGray
            )
        }
    }
}

@Composable
private fun ComparisonCard(c1: ClubModel, c2: ClubModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(10.dp, RoundedCornerShape(24.dp), ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            StatRow("RENDIMIENTO", "OFENSIVO", "DEFENSIVO")
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp, color = Color(0xFFF5F5F5))
            StatRow("PUNTOS PROMEDIO", "2.1", "1.8")
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp, color = Color(0xFFF5F5F5))
            StatRow("GOLES A FAVOR", "24", "19")
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp, color = Color(0xFFF5F5F5))
            StatRow("GOLES EN CONTRA", "12", "15")
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp, color = Color(0xFFF5F5F5))
            StatRow("EFECTIVIDAD", "70%", "60%")
        }
    }
}

@Composable
private fun StatRow(label: String, val1: String, val2: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            val1,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Black,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            label,
            modifier = Modifier.width(120.dp),
            textAlign = TextAlign.Center,
            fontSize = 10.sp,
            color = Color.Gray,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 0.5.sp
        )
        Text(
            val2,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Black,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}
