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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.border
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
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
    val user by viewModel.user.collectAsStateWithLifecycle()
    val trainingPlan by viewModel.trainingPlan.collectAsStateWithLifecycle()
    val callUp by viewModel.callUp.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.shadow(8.dp),
                color = MaterialTheme.colorScheme.primary
            ) {
                TopAppBar(
                    title = { Text("MI EQUIPO", fontWeight = FontWeight.Black, fontSize = 16.sp, letterSpacing = 1.sp) },
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
                        containerColor = Color.Transparent,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White,
                        actionIconContentColor = Color.White
                    )
                )
            }
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
            val accentColor = if (user!!.rama.contains("Fem", true) || user!!.rama.contains("Dama", true)) Color(0xFFFF4081) else Color(0xFF2979FF)
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
                    TeamHeader(
                        clubName = user!!.club_nombre, 
                        subtitle = "${user!!.categoria} • ${user!!.division ?: "General"}",
                        escudoUrl = user!!.club_escudo,
                        accentColor = accentColor
                    )

                    Column(modifier = Modifier.padding(24.dp)) {
                        SectionHeader("ESTADO DE CITACIÓN")
                        Spacer(modifier = Modifier.height(12.dp))
                        CallUpCard(callUp, user!!.categoria, accentColor)

                        Spacer(modifier = Modifier.height(32.dp))

                        SectionHeader("PLANIFICACIÓN FÍSICA")
                        Spacer(modifier = Modifier.height(12.dp))
                        TrainingCard(trainingPlan, user!!.division ?: "General", accentColor)

                        Spacer(modifier = Modifier.height(32.dp))

                        SectionHeader("HERRAMIENTAS")
                        Spacer(modifier = Modifier.height(12.dp))
                        ToolItem("Pizarra Táctica", "Repasá las jugadas del equipo.", Icons.Default.AutoGraph, accentColor, onNavigateToTacticalBoard)
                        
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
fun TeamHeader(clubName: String, subtitle: String, escudoUrl: String?, accentColor: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(MaterialTheme.colorScheme.primary, Color(0xFF0F2B48))
                )
            )
            .padding(start = 30.dp, end = 30.dp, bottom = 40.dp, top = 10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .shadow(12.dp, RoundedCornerShape(18.dp))
                    .background(Color.White, RoundedCornerShape(18.dp))
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                if (escudoUrl != null) {
                    AsyncImage(
                        model = escudoUrl,
                        contentDescription = "Escudo del Club",
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.SportsHockey, 
                        contentDescription = null, 
                        tint = accentColor, 
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(20.dp))
            Column {
                Text(clubName.uppercase(), color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black, letterSpacing = 0.5.sp)
                Text(subtitle.uppercase(), color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            }
        }
    }
}

@Composable
fun CallUpCard(callUp: CallUpModel?, categoria: String, accentColor: Color) {
    val isCalledUp = callUp != null
    val statusColor = if (isCalledUp) accentColor else Color.Gray
    val icon = if (isCalledUp) Icons.Default.CheckCircle else Icons.Default.Info
    val text = if (isCalledUp) "¡ESTÁS CONVOCADO!" else "PENDIENTE DE CITACIÓN"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(24.dp), ambientColor = statusColor.copy(alpha = 0.15f)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Box(modifier = Modifier.fillMaxHeight().width(6.dp).background(statusColor))
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(statusColor.copy(alpha = 0.08f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, contentDescription = null, tint = statusColor, modifier = Modifier.size(22.dp))
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(text, fontWeight = FontWeight.Black, color = statusColor, fontSize = 10.sp, letterSpacing = 1.sp)
                        Text(
                            if (isCalledUp && !callUp.rival_nombre.isNullOrEmpty()) "VS ${callUp.rival_nombre.uppercase()}" else "PRÓXIMA FECHA - $categoria",
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            color = Color(0xFF1A1A1A)
                        )
                    }
                }
                
                if (isCalledUp) {
                    Spacer(modifier = Modifier.height(20.dp))
                    HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFF5F5F5))
                    Spacer(modifier = Modifier.height(16.dp))
                    InfoRow(Icons.Default.LocationOn, "Lugar: ${callUp.lugar ?: "Cancha del Club"}")
                    Spacer(modifier = Modifier.height(8.dp))
                    InfoRow(Icons.Default.AccessTime, "Horario: ${callUp.horario_citacion ?: "A confirmar"} hs (Citación)")
                }
            }
        }
    }
}

@Composable
fun TrainingCard(plan: TrainingPlanModel?, division: String, accentColor: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(24.dp), ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Box(modifier = Modifier.fillMaxHeight().width(6.dp).background(MaterialTheme.colorScheme.primary))
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text("INDICACIONES ${division.uppercase()}", fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary, fontSize = 10.sp, letterSpacing = 1.sp)
                        Text("PLAN SEMANAL", fontWeight = FontWeight.Black, fontSize = 15.sp, color = Color(0xFF1A1A1A))
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF8F9FA), RoundedCornerShape(16.dp))
                        .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        text = plan?.plan_detalle ?: "No hay trabajos físicos asignados para esta semana por el staff técnico.",
                        fontSize = 13.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (plan == null) Color.Gray else Color(0xFF333333)
                    )
                }
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
fun ToolItem(title: String, subtitle: String, icon: ImageVector, accentColor: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        ListItem(
            headlineContent = { Text(title.uppercase(), fontWeight = FontWeight.Black, fontSize = 14.sp, letterSpacing = 0.5.sp) },
            supportingContent = { Text(subtitle, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Color.Gray) },
            leadingContent = { 
                Box(
                    modifier = Modifier.size(40.dp).background(accentColor.copy(alpha = 0.08f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(22.dp)) 
                }
            },
            trailingContent = { Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color.LightGray) },
            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
        )
    }
}
