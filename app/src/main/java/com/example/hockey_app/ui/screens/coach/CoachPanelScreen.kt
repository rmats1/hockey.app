package com.example.hockey_app.ui.screens.coach

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoachPanelScreen(
    onMenuClick: () -> Unit,
    onNavigateToTacticalBoard: () -> Unit,
    onNavigateToCallUpManagement: () -> Unit,
    onNavigateToSearchPlayers: () -> Unit,
    onNavigateToPhysicalPlanning: () -> Unit,
    viewModel: CoachViewModel = hiltViewModel(),
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    // UI logic here

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(start = 30.dp, end = 30.dp, bottom = 40.dp, top = 0.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text(
                            "ESTRATEGIA",
                            color = MaterialTheme.colorScheme.secondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "PANEL TÉCNICO",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 24.sp,
                            letterSpacing = (-1).sp
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Box(
                        modifier = Modifier
                            .size(55.dp)
                            .background(Color.White.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = Color.White, modifier = Modifier.size(30.dp))
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    ActionCard(
                        "PIZARRA TÁCTICA",
                        "Diseñá jugadas y posicionamientos.",
                        Icons.Default.Gesture,
                        MaterialTheme.colorScheme.primary,
                        onClick = onNavigateToTacticalBoard
                    )
                }
                item {
                    ActionCard(
                        "PLANIFICACIÓN FÍSICA",
                        "Enviá indicaciones de trabajo al plantel.",
                        Icons.Default.FitnessCenter,
                        Color(0xFF2196F3),
                        onClick = onNavigateToPhysicalPlanning
                    )
                }
                item {
                    ActionCard(
                        "GRUPO DE JUGADORES",
                        "Chat directo en WhatsApp con el equipo.",
                        Icons.Default.ChatBubble,
                        Color(0xFF4CAF50)
                    ) {
                        com.example.hockey_app.utils.IntentUtils.openWhatsApp(context)
                    }
                }
                item {
                    ActionCard(
                        "CONVOCATORIA",
                        "Armá la lista para el próximo partido.",
                        Icons.Default.AssignmentInd,
                        MaterialTheme.colorScheme.secondary,
                        onClick = onNavigateToCallUpManagement
                    )
                }
                item {
                    ActionCard(
                        "RED DE DIRECTORES",
                        "Contacto con otros cuerpos técnicos.",
                        Icons.Default.ConnectWithoutContact,
                        Color(0xFF1976D2),
                        onClick = onNavigateToSearchPlayers
                    )
                }
            }
        }
    }
}

@Composable
fun ActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .shadow(10.dp, RoundedCornerShape(24.dp), ambientColor = Color.Black.copy(alpha = 0.03f)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        ListItem(
            modifier = Modifier.padding(10.dp),
            leadingContent = {
                Box(
                    modifier = Modifier
                        .size(55.dp)
                        .background(color.copy(alpha = 0.1f), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(28.dp))
                }
            },
            headlineContent = {
                Text(title, fontWeight = FontWeight.Black, fontSize = 13.sp, letterSpacing = 0.5.sp)
            },
            supportingContent = {
                Text(subtitle, fontSize = 11.sp, color = Color.Gray, lineHeight = 16.sp)
            },
            trailingContent = {
                Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(14.dp))
            },
            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
        )
    }
}
