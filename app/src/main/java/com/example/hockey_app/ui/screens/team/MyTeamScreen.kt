package com.example.hockey_app.ui.screens.team

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hockey_app.data.models.CallUpModel
import com.example.hockey_app.data.models.TrainingPlanModel
import com.example.hockey_app.ui.screens.register.SectionHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTeamScreen(
    onMenuClick: () -> Unit,
    onConfigureClub: () -> Unit,
    onNavigateToTacticalBoard: () -> Unit,
    viewModel: TeamViewModel = hiltViewModel(),
) {
    val user by viewModel.user.collectAsState()
    val trainingPlan by viewModel.trainingPlan.collectAsState()
    val callUp by viewModel.callUp.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MI EQUIPO", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadData() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { padding ->
        if (isLoading && user == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (user == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error al cargar datos del usuario.")
            }
        } else {
            val hasClub = (user!!.club_id != "0") && (user!!.club_nombre != "Sin Club")

            if (!hasClub) {
                NoClubContent(onConfigureClub)
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(scrollState)
                ) {
                    // Club Header
                    TeamHeader(user!!.club_nombre, "${user!!.categoria} • ${user!!.division ?: "General"}")

                    Column(modifier = Modifier.padding(24.dp)) {
                        SectionHeader("ESTADO DE CITACIÓN")
                        Spacer(modifier = Modifier.height(12.dp))
                        CallUpCard(callUp, user!!.categoria)

                        Spacer(modifier = Modifier.height(32.dp))

                        SectionHeader("PLANIFICACIÓN FÍSICA")
                        Spacer(modifier = Modifier.height(12.dp))
                        TrainingCard(trainingPlan, user!!.division ?: "General")

                        Spacer(modifier = Modifier.height(32.dp))

                        SectionHeader("HERRAMIENTAS")
                        Spacer(modifier = Modifier.height(12.dp))
                        ToolItem("Pizarra Táctica", "Repasá las jugadas del equipo.", Icons.Default.AutoGraph, onNavigateToTacticalBoard)
                        
                        Spacer(modifier = Modifier.height(40.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun NoClubContent(onConfigureClub: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(30.dp)
        ) {
            Icon(Icons.Default.SportsHockey, contentDescription = null, modifier = Modifier.size(80.dp), tint = Color.LightGray)
            Spacer(modifier = Modifier.height(20.dp))
            Text("¿Aún no tenés club?", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                "Completá tu perfil para ver las citaciones de tu equipo y el plan de entrenamiento.",
                textAlign = TextAlign.Center,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(30.dp))
            Button(
                onClick = onConfigureClub,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("CONFIGURAR MI CLUB")
            }
        }
    }
}

@Composable
fun TeamHeader(clubName: String, subtitle: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(start = 30.dp, end = 30.dp, bottom = 40.dp, top = 10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.SportsHockey, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(30.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(clubName.uppercase(), color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black)
                Text(subtitle, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun CallUpCard(callUp: CallUpModel?, categoria: String) {
    val isCalledUp = callUp != null
    val color = if (isCalledUp) MaterialTheme.colorScheme.secondary else Color.Gray
    val icon = if (isCalledUp) Icons.Default.CheckCircle else Icons.Default.Info
    val text = if (isCalledUp) "¡ESTÁS CONVOCADO!" else "PENDIENTE DE CITACIÓN"

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(color.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text, fontWeight = FontWeight.Black, color = color, fontSize = 10.sp, letterSpacing = 1.sp)
                    Text(
                        if (isCalledUp && !callUp.rival_nombre.isNullOrEmpty()) "vs ${callUp.rival_nombre}" else "Próxima Fecha - $categoria",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
            
            if (isCalledUp) {
                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(thickness = 0.5.dp)
                Spacer(modifier = Modifier.height(16.dp))
                InfoRow(Icons.Default.LocationOn, "Lugar: ${callUp.lugar ?: "Cancha del Club"}")
                Spacer(modifier = Modifier.height(8.dp))
                InfoRow(Icons.Default.AccessTime, "Horario: ${callUp.horario_citacion ?: "A confirmar"} hs (Citación)")
            }
        }
    }
}

@Composable
fun TrainingCard(plan: TrainingPlanModel?, division: String) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("INDICACIONES ${division.uppercase()}", fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary, fontSize = 10.sp, letterSpacing = 1.sp)
                    Text("Plan Semanal", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFAFAFA), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = plan?.plan_detalle ?: "No hay trabajos físicos asignados para esta semana por el staff técnico.",
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                    color = if (plan == null) Color.Gray else Color.Black
                )
            }
        }
    }
}

@Composable
fun InfoRow(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Gray)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun ToolItem(title: String, subtitle: String, icon: ImageVector, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        ListItem(
            headlineContent = { Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp) },
            supportingContent = { Text(subtitle, fontSize = 11.sp) },
            leadingContent = { Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp)) },
            trailingContent = { Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, modifier = Modifier.size(12.dp)) }
        )
    }
}
