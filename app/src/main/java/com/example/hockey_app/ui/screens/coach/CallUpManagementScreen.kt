package com.example.hockey_app.ui.screens.coach

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hockey_app.data.models.UserModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CallUpManagementScreen(
    onBack: () -> Unit,
    viewModel: CallUpManagementViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val selectedIds by viewModel.selectedPlayerIds.collectAsStateWithLifecycle()
    val isSaving by viewModel.isSaving.collectAsStateWithLifecycle()

    var rival by remember { mutableStateOf("") }
    var lugar by remember { mutableStateOf("") }
    var horario by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("GESTIÓN DE CONVOCATORIA", fontWeight = FontWeight.Black, fontSize = 16.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
                    }
                },
                actions = {
                    if (!isSaving) {
                        IconButton(onClick = { viewModel.saveCallUps(lugar, horario, rival, onBack) }) {
                            Icon(Icons.Default.Save, contentDescription = "Guardar", tint = Color.White)
                        }
                    } else {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp).padding(4.dp), color = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).background(Color(0xFFF5F5F5))) {
            // Match info inputs
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("DATOS DEL PARTIDO", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = rival,
                        onValueChange = { rival = it },
                        label = { Text("Nombre del Rival") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        OutlinedTextField(
                            value = lugar,
                            onValueChange = { lugar = it },
                            label = { Text("Lugar") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = horario,
                            onValueChange = { horario = it },
                            label = { Text("Horario") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                }
            }

            Text(
                "SELECCIONAR JUGADORES (${selectedIds.size})",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                fontWeight = FontWeight.Black,
                fontSize = 12.sp,
                color = Color.Gray
            )

            Box(modifier = Modifier.weight(1f)) {
                when (val s = state) {
                    is CallUpManagementState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    is CallUpManagementState.Error -> Text(s.message, modifier = Modifier.align(Alignment.Center))
                    is CallUpManagementState.Success -> {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(s.jugadores) { jugador ->
                                PlayerCallUpItem(
                                    jugador = jugador,
                                    isSelected = selectedIds.contains(jugador.id),
                                    onClick = { viewModel.togglePlayerSelection(jugador.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PlayerCallUpItem(jugador: UserModel, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.05f) else Color.White
        ),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
    ) {
        ListItem(
            headlineContent = { Text(jugador.nombre, fontWeight = FontWeight.Bold) },
            supportingContent = { Text(jugador.posicion ?: "Sin posición", fontSize = 12.sp) },
            leadingContent = {
                Box(
                    modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.LightGray.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = Color.Gray)
                }
            },
            trailingContent = {
                if (isSelected) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                }
            },
            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
        )
    }
}
