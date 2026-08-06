package com.example.hockey_app.ui.screens.register

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.hockey_app.data.constants.AppConstants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

    LaunchedEffect(state) {
        if (state is RegisterState.Success) {
            onNavigateToHome()
        } else if (state is RegisterState.Error) {
            snackbarHostState.showSnackbar((state as RegisterState.Error).message)
            viewModel.resetError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registro", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
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
                .padding(30.dp)
        ) {
            Text(
                text = "CREAR CUENTA",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = (-1).sp
            )
            Text(
                text = "Unite a la comunidad del hockey de Buenos Aires",
                fontSize = 13.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))

            SectionHeader("ROL Y RAMA")
            Spacer(modifier = Modifier.height(16.dp))

            val userType by viewModel.userType.collectAsState()
            Row {
                SelectionButton(
                    label = "JUGADOR/A",
                    icon = Icons.Default.SportsHockey,
                    isSelected = userType == "jugador",
                    modifier = Modifier.weight(1f),
                    onSelect = { viewModel.userType.value = "jugador" }
                )
                Spacer(modifier = Modifier.width(12.dp))
                SelectionButton(
                    label = "C. TÉCNICO",
                    icon = Icons.Default.Groups,
                    isSelected = userType == "cuerpo_tecnico",
                    modifier = Modifier.weight(1f),
                    onSelect = { viewModel.userType.value = "cuerpo_tecnico" }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            val rama by viewModel.rama.collectAsState()
            Row {
                SmallSelectButton(
                    label = "DAMAS",
                    isSelected = rama == "Damas",
                    modifier = Modifier.weight(1f),
                    onSelect = { viewModel.rama.value = "Damas" }
                )
                Spacer(modifier = Modifier.width(8.dp))
                SmallSelectButton(
                    label = "CABALLEROS",
                    isSelected = rama == "Caballeros",
                    modifier = Modifier.weight(1f),
                    onSelect = { viewModel.rama.value = "Caballeros" }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            SectionHeader("TU CLUB")
            Spacer(modifier = Modifier.height(16.dp))

            ClubSearchField(viewModel)

            Spacer(modifier = Modifier.height(24.dp))
            SectionHeader("CATEGORÍA Y DIVISIÓN")
            Spacer(modifier = Modifier.height(12.dp))

            val categoria by viewModel.categoria.collectAsState()
            DropdownField(
                label = "Categoría (Torneo)",
                icon = Icons.Default.EmojiEvents,
                items = AppConstants.CATEGORIAS,
                selectedItem = categoria,
                onItemSelected = { viewModel.categoria.value = it }
            )

            Spacer(modifier = Modifier.height(12.dp))
            DivisionSelector(viewModel)

            if (userType == "jugador") {
                Spacer(modifier = Modifier.height(12.dp))
                val numeroCamiseta by viewModel.numeroCamiseta.collectAsState()
                ModernTextField(
                    value = numeroCamiseta,
                    onValueChange = { viewModel.numeroCamiseta.value = it },
                    label = "Número de camiseta",
                    icon = Icons.Default.Numbers,
                    keyboardType = KeyboardType.Number
                )
                Spacer(modifier = Modifier.height(16.dp))
                val posicion by viewModel.posicion.collectAsState()
                DropdownField(
                    label = "Posición",
                    icon = Icons.Default.Sports,
                    items = AppConstants.POSICIONES_JUGADOR,
                    selectedItem = posicion ?: "",
                    onItemSelected = { viewModel.posicion.value = it }
                )
            } else {
                Spacer(modifier = Modifier.height(12.dp))
                val rol by viewModel.rolCuerpoTecnico.collectAsState()
                DropdownField(
                    label = "Rol",
                    icon = Icons.Default.Work,
                    items = AppConstants.ROLES_CUERPO_TECNICO,
                    selectedItem = rol ?: "",
                    onItemSelected = { viewModel.rolCuerpoTecnico.value = it }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
            SectionHeader("DATOS PERSONALES")
            Spacer(modifier = Modifier.height(16.dp))

            val name by viewModel.name.collectAsState()
            ModernTextField(name, { viewModel.name.value = it }, "Nombre completo", Icons.Default.PersonOutline)
            Spacer(modifier = Modifier.height(16.dp))

            val email by viewModel.email.collectAsState()
            ModernTextField(email, { viewModel.email.value = it }, "Email", Icons.Default.AlternateEmail, KeyboardType.Email)
            Spacer(modifier = Modifier.height(16.dp))

            val password by viewModel.password.collectAsState()
            var passVisible by remember { mutableStateOf(value = false) }
            ModernTextField(
                value = password,
                onValueChange = { viewModel.password.value = it },
                label = "Contraseña",
                icon = Icons.Default.Lock,
                keyboardType = KeyboardType.Password,
                isPassword = true,
                passwordVisible = passVisible,
                onTogglePassword = { passVisible = !passVisible }
            )
            Spacer(modifier = Modifier.height(16.dp))

            val confirmPassword by viewModel.confirmPassword.collectAsState()
            var confirmPassVisible by remember { mutableStateOf(value = false) }
            ModernTextField(
                value = confirmPassword,
                onValueChange = { viewModel.confirmPassword.value = it },
                label = "Confirmar contraseña",
                icon = Icons.Default.LockClock,
                keyboardType = KeyboardType.Password,
                isPassword = true,
                passwordVisible = confirmPassVisible,
                onTogglePassword = { confirmPassVisible = !confirmPassVisible }
            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = viewModel::register,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(15.dp),
                enabled = state !is RegisterState.Loading
            ) {
                if (state is RegisterState.Loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("CREAR MI CUENTA", fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                }
            }
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 10.sp,
        fontWeight = FontWeight.ExtraBold,
        color = Color.Gray,
        letterSpacing = 1.sp
    )
}

@Composable
fun SelectionButton(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onSelect: () -> Unit
) {
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFFEEEEEE)
    val bgColor = if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFFFAFAFA)
    val contentColor = if (isSelected) Color.White else Color.Gray

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .border(2.dp, borderColor, RoundedCornerShape(20.dp))
            .clickable(onClick = onSelect)
            .padding(vertical = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = null, tint = if (isSelected) Color.White else Color.LightGray, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.height(10.dp))
            Text(label, color = contentColor, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
fun SmallSelectButton(
    label: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onSelect: () -> Unit
) {
    val bgColor = if (isSelected) MaterialTheme.colorScheme.secondary else Color(0xFFFAFAFA)
    val borderColor = if (isSelected) MaterialTheme.colorScheme.secondary else Color(0xFFEEEEEE)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(onClick = onSelect)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.White else Color.Gray,
            fontSize = 10.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
fun ModernTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onTogglePassword: (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 13.sp) },
        modifier = Modifier.fillMaxWidth(),
        leadingIcon = { Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp)) },
        trailingIcon = {
            if (isPassword && (onTogglePassword != null)) {
                IconButton(onClick = onTogglePassword) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        },
        shape = RoundedCornerShape(15.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = Color(0xFFFAFAFA),
            focusedContainerColor = Color(0xFFFAFAFA),
            unfocusedBorderColor = Color(0xFFEEEEEE)
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownField(
    label: String,
    icon: ImageVector,
    items: List<String>,
    selectedItem: String,
    onItemSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selectedItem,
            onValueChange = {},
            readOnly = true,
            label = { Text(label, fontSize = 13.sp) },
            leadingIcon = { Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
            shape = RoundedCornerShape(15.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFFAFAFA),
                focusedContainerColor = Color(0xFFFAFAFA),
                unfocusedBorderColor = Color(0xFFEEEEEE)
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item, fontSize = 14.sp) },
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun DivisionSelector(viewModel: RegisterViewModel) {
    val selectedDivision by viewModel.division.collectAsState()

    Column {
        Text("División (A, B, C...)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AppConstants.DIVISIONES.forEach { d ->
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
                    Text(
                        text = d,
                        color = if (isSelected) Color.White else Color.DarkGray,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Composable
fun ClubSearchField(viewModel: RegisterViewModel) {
    val allClubes by viewModel.clubes.collectAsState()
    val selectedClub by viewModel.selectedClub.collectAsState()
    var query by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    val filteredClubes = remember(query, allClubes) {
        if (query.isBlank()) allClubes else allClubes.filter { it.nombre.contains(query, ignoreCase = true) }
    }

    Column {
        Text("¿De qué club sos?", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = query,
            onValueChange = {
                query = it
                expanded = true
            },
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
            shape = RoundedCornerShape(15.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFFAFAFA),
                focusedContainerColor = Color(0xFFFAFAFA),
                unfocusedBorderColor = Color(0xFFEEEEEE)
            )
        )

        AnimatedVisibility(visible = expanded && query.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp),
                shape = RoundedCornerShape(15.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                LazyColumn {
                    items(filteredClubes.take(20)) { club ->
                        ListItem(
                            headlineContent = { Text(club.nombre, fontWeight = FontWeight.SemiBold, fontSize = 13.sp) },
                            leadingContent = {
                                if (club.escudoUrl != null) {
                                    AsyncImage(
                                        model = club.escudoUrl,
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp),
                                        contentScale = ContentScale.Fit
                                    )
                                } else {
                                    Icon(Icons.Default.SportsHockey, contentDescription = null, modifier = Modifier.size(20.dp))
                                }
                            },
                            modifier = Modifier.clickable {
                                viewModel.selectedClub.value = club
                                query = club.nombre
                                expanded = false
                            }
                        )
                    }
                }
            }
        }

        if (selectedClub != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (selectedClub!!.escudoUrl != null) {
                    AsyncImage(
                        model = selectedClub!!.escudoUrl,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Icon(Icons.Default.CheckCircle, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp), contentDescription = null)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Club: ${selectedClub!!.nombre}",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}
