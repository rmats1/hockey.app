package com.example.hockey_app.ui.screens.club

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.SportsHockey
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hockey_app.data.models.ClubModel

import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompareClubsScreen(
    onBack: () -> Unit,
    viewModel: CompareClubsViewModel = hiltViewModel()
) {
    val clubes by viewModel.clubes.collectAsState()
    var club1 by remember { mutableStateOf<ClubModel?>(null) }
    var club2 by remember { mutableStateOf<ClubModel?>(null) }
    var selectingForClub by remember { mutableStateOf<Int?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Comparar Clubes", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
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
        ModalBottomSheet(
            onDismissRequest = { selectingForClub = null }
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Seleccionar Club",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                LazyColumn(modifier = Modifier.height(400.dp)) {
                    items(clubes) { club ->
                        ListItem(
                            headlineContent = { Text(club.nombre, fontWeight = FontWeight.SemiBold) },
                            leadingContent = { Icon(Icons.Default.SportsHockey, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
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
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.SportsHockey, contentDescription = null, modifier = Modifier.size(32.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(6.dp))
            Text(label, fontSize = 10.sp, color = Color.Gray)
            Text(
                selectedClub?.nombre ?: "Elegir...",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ComparisonCard(c1: ClubModel, c2: ClubModel) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            StatRow("Rendimiento", "Ofensivo", "Defensivo")
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            StatRow("Puntos Promedio", "2.1", "1.8")
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            StatRow("Goles a Favor", "24", "19")
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            StatRow("Goles en Contra", "12", "15")
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            StatRow("Efectividad", "70%", "60%")
        }
    }
}

@Composable
private fun StatRow(label: String, val1: String, val2: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            val1,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            label,
            modifier = Modifier.width(100.dp),
            textAlign = TextAlign.Center,
            fontSize = 11.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Medium
        )
        Text(
            val2,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}
