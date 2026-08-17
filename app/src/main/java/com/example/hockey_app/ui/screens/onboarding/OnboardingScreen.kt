package com.example.hockey_app.ui.screens.onboarding

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hockey_app.data.constants.AppConstants
import com.example.hockey_app.data.constants.CompetitionCatalog
import com.example.hockey_app.ui.screens.register.*
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    onSkip: () -> Unit,
    onFinish: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

    LaunchedEffect(state) {
        if (state is OnboardingState.Success) {
            onFinish()
        } else if (state is OnboardingState.Error) {
            snackbarHostState.showSnackbar((state as OnboardingState.Error).message)
            viewModel.resetError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                actions = {
                    TextButton(onClick = onSkip) {
                        Text(
                            text = "SALTAR",
                            color = Color.Gray,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(horizontal = 30.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "¡BIENVENIDO/A!",
                fontSize = 36.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = (-1).sp
            )
            Text(
                text = "Personalizá tu experiencia en Hockey Plus.",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(36.dp))

            SectionHeader("¿CUÁL ES TU ROL?")
            Spacer(modifier = Modifier.height(18.dp))

            val userType by viewModel.userType.collectAsStateWithLifecycle()
            Row {
                SelectionButton(
                    label = "JUGADOR/A",
                    icon = Icons.Default.SportsHockey,
                    isSelected = userType == "jugador",
                    modifier = Modifier.weight(1f),
                    onSelect = { viewModel.userType.value = "jugador" }
                )
                Spacer(modifier = Modifier.width(14.dp))
                SelectionButton(
                    label = "C. TÉCNICO",
                    icon = Icons.Default.Groups,
                    isSelected = userType == "cuerpo_tecnico",
                    modifier = Modifier.weight(1f),
                    onSelect = { viewModel.userType.value = "cuerpo_tecnico" }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            val rama by viewModel.rama.collectAsStateWithLifecycle()
            Row {
                SmallSelectButton(
                    label = "DAMAS",
                    isSelected = rama == "Damas",
                    modifier = Modifier.weight(1f),
                    onSelect = { viewModel.rama.value = "Damas" }
                )
                Spacer(modifier = Modifier.width(10.dp))
                SmallSelectButton(
                    label = "CABALLEROS",
                    isSelected = rama == "Caballeros",
                    modifier = Modifier.weight(1f),
                    onSelect = { viewModel.rama.value = "Caballeros" }
                )
            }

            Spacer(modifier = Modifier.height(36.dp))
            SectionHeader("TU CLUB Y CATEGORÍA")
            Spacer(modifier = Modifier.height(18.dp))

            OnboardingClubSearchField(viewModel)

            Spacer(modifier = Modifier.height(26.dp))
            val categoria by viewModel.categoria.collectAsStateWithLifecycle()
            DropdownField(
                label = "Categoría",
                icon = Icons.Default.EmojiEvents,
                items = CompetitionCatalog.categories(rama),
                selectedItem = categoria,
                onItemSelected = { viewModel.categoria.value = it }
            )

            Spacer(modifier = Modifier.height(18.dp))
            OnboardingDivisionSelector(viewModel)

            if (userType == "jugador") {
                Spacer(modifier = Modifier.height(26.dp))
                val numeroCamiseta by viewModel.numeroCamiseta.collectAsStateWithLifecycle()
                ModernTextField(
                    value = numeroCamiseta,
                    onValueChange = { viewModel.numeroCamiseta.value = it },
                    label = "Número de camiseta",
                    icon = Icons.Default.Numbers,
                    keyboardType = KeyboardType.Number
                )
                Spacer(modifier = Modifier.height(18.dp))
                val posicion by viewModel.posicion.collectAsStateWithLifecycle()
                DropdownField(
                    label = "Posición",
                    icon = Icons.Default.Sports,
                    items = AppConstants.POSICIONES_JUGADOR,
                    selectedItem = posicion ?: "",
                    onItemSelected = { viewModel.posicion.value = it }
                )
            } else {
                Spacer(modifier = Modifier.height(26.dp))
                val rol by viewModel.rolCuerpoTecnico.collectAsStateWithLifecycle()
                DropdownField(
                    label = "Rol en el equipo",
                    icon = Icons.Default.Work,
                    items = AppConstants.ROLES_CUERPO_TECNICO,
                    selectedItem = rol ?: "",
                    onItemSelected = { viewModel.rolCuerpoTecnico.value = it }
                )
            }

            Spacer(modifier = Modifier.height(50.dp))

            Button(
                onClick = viewModel::finishOnboarding,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .shadow(10.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                enabled = state !is OnboardingState.Loading
            ) {
                if (state is OnboardingState.Loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("GUARDAR Y EMPEZAR", fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                TextButton(onClick = onSkip) {
                    Text("Completar después en mi perfil", color = Color.Gray, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun OnboardingDivisionSelector(viewModel: OnboardingViewModel) {
    val rama by viewModel.rama.collectAsStateWithLifecycle()
    val categoria by viewModel.categoria.collectAsStateWithLifecycle()
    val selectedDivision by viewModel.division.collectAsStateWithLifecycle()
    Column {
        Text("División (Opcional)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CompetitionCatalog.divisions(rama, categoria).forEach { d ->
                val isSelected = selectedDivision == d
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.White)
                        .border(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray, CircleShape)
                        .clickable { viewModel.division.value = d }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = d, color = if (isSelected) Color.White else Color.DarkGray, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                }
            }
        }
    }
}

@Composable
fun OnboardingClubSearchField(viewModel: OnboardingViewModel) {
    val allClubes by viewModel.clubes.collectAsStateWithLifecycle()
    val selectedClub by viewModel.selectedClub.collectAsStateWithLifecycle()
    var query by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(value = false) }
    val filteredClubes = remember(query, allClubes) {
        if (query.isBlank()) allClubes else allClubes.filter { it.nombre.contains(query, ignoreCase = true) }
    }
    Column {
        Text("¿De qué club sos?", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = query,
            onValueChange = { query = it; expanded = true },
            placeholder = { Text("Buscá tu club...") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(20.dp)) },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { query = ""; expanded = false }) {
                        Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                }
            },
            shape = RoundedCornerShape(15.dp)
        )
        AnimatedVisibility(visible = expanded && query.isNotEmpty()) {
            Card(modifier = Modifier.fillMaxWidth().heightIn(max = 200.dp), shape = RoundedCornerShape(15.dp), elevation = CardDefaults.cardElevation(4.dp)) {
                LazyColumn {
                    items(filteredClubes.take(20)) { club ->
                        ListItem(
                            headlineContent = { Text(club.nombre, fontWeight = FontWeight.SemiBold, fontSize = 13.sp) },                            leadingContent = {
                                AsyncImage(
                                    model = club.escudoUrl,
                                    contentDescription = "Escudo de ${club.nombre}",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.size(32.dp)
                                )
                            },
                            modifier = Modifier.clickable { viewModel.selectedClub.value = club; query = club.nombre; expanded = false }
                        )
                    }
                }
            }
        }
        if (selectedClub != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "Club: ${selectedClub!!.nombre}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
    }
}
