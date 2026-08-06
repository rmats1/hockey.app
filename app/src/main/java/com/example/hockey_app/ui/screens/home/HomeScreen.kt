package com.example.hockey_app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hockey_app.ui.screens.coach.CoachPanelScreen
import com.example.hockey_app.ui.screens.fixture.FixtureScreen
import com.example.hockey_app.ui.screens.news.NewsScreen
import com.example.hockey_app.ui.screens.profile.ProfileScreen
import com.example.hockey_app.ui.screens.team.MyTeamScreen
import com.example.hockey_app.ui.screens.torneos.TorneoDetalleMode
import com.example.hockey_app.ui.screens.torneos.TorneosScreen
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.outlined.Article
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToTorneoDetalle: (com.example.hockey_app.data.models.TorneoResumen, TorneoDetalleMode) -> Unit,
    onNavigateToTacticalBoard: () -> Unit,
    onNavigateToCompareClubs: () -> Unit,
    onNavigateToFavoriteClubs: () -> Unit,
    onNavigateToSearchPlayers: () -> Unit,
    onNavigateToCallUpManagement: () -> Unit,
    onNavigateToPhysicalPlanning: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToNewsDetail: (com.example.hockey_app.data.models.NewsModel) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val user by viewModel.user.collectAsState()
    val currentIndex by viewModel.currentIndex.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    if (user == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            HomeDrawerContent(
                user = user!!,
                currentIndex = currentIndex,
                onItemSelected = { index ->
                    viewModel.onTabSelected(index)
                    scope.launch { drawerState.close() }
                },
                onLogout = {
                    viewModel.logout { onNavigateToLogin() }
                }
            )
        }
    ) {
        Scaffold(
            bottomBar = {
                HomeBottomBar(
                    currentIndex = currentIndex,
                    userType = user!!.user_type,
                    onItemSelected = viewModel::onTabSelected
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Main Content
                when (currentIndex) {
                    0 -> TorneosScreen(
                        onMenuClick = { scope.launch { drawerState.open() } },
                        onTorneoClick = { onNavigateToTorneoDetalle(it, TorneoDetalleMode.POSICIONES) }
                    )
                    1 -> FixtureScreen(
                        onMenuClick = { scope.launch { drawerState.open() } },
                        onTorneoClick = { onNavigateToTorneoDetalle(it, TorneoDetalleMode.FIXTURE) }
                    )
                    2 -> NewsScreen(
                        onMenuClick = { scope.launch { drawerState.open() } },
                        onNewsClick = onNavigateToNewsDetail
                    )
                    3 -> if (user!!.user_type == "cuerpo_tecnico") {
                        CoachPanelScreen(
                            onMenuClick = { scope.launch { drawerState.open() } },
                            onNavigateToTacticalBoard = onNavigateToTacticalBoard,
                            onNavigateToCallUpManagement = onNavigateToCallUpManagement,
                            onNavigateToSearchPlayers = onNavigateToSearchPlayers,
                            onNavigateToPhysicalPlanning = onNavigateToPhysicalPlanning
                        )
                    } else {
                        MyTeamScreen(
                            onMenuClick = { scope.launch { drawerState.open() } },
                            onConfigureClub = { viewModel.onTabSelected(4) },
                            onNavigateToTacticalBoard = onNavigateToTacticalBoard
                        )
                    }
                    4 -> ProfileScreen(
                        onMenuClick = { scope.launch { drawerState.open() } },
                        onNavigateToLogin = onNavigateToLogin,
                        onNavigateToCompareClubs = onNavigateToCompareClubs,
                        onNavigateToFavoriteClubs = onNavigateToFavoriteClubs,
                        onNavigateToSettings = onNavigateToSettings
                    )
                }
            }
        }
    }
}

@Composable
fun HomeBottomBar(
    currentIndex: Int,
    userType: String,
    onItemSelected: (Int) -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = currentIndex == 0,
            onClick = { onItemSelected(0) },
            icon = { Icon(if (currentIndex == 0) Icons.Default.EmojiEvents else Icons.Outlined.EmojiEvents, contentDescription = null) },
            label = { Text("TORNEOS", fontSize = 9.sp, fontWeight = FontWeight.Bold) }
        )
        NavigationBarItem(
            selected = currentIndex == 1,
            onClick = { onItemSelected(1) },
            icon = { Icon(if (currentIndex == 1) Icons.Default.Sports else Icons.Outlined.Sports, contentDescription = null) },
            label = { Text("FIXTURE", fontSize = 9.sp, fontWeight = FontWeight.Bold) }
        )
        NavigationBarItem(
            selected = currentIndex == 2,
            onClick = { onItemSelected(2) },
            icon = { Icon(if (currentIndex == 2) Icons.AutoMirrored.Filled.Article else Icons.AutoMirrored.Outlined.Article, contentDescription = null) },
            label = { Text("NOTICIAS", fontSize = 9.sp, fontWeight = FontWeight.Bold) }
        )
        NavigationBarItem(
            selected = currentIndex == 3,
            onClick = { onItemSelected(3) },
            icon = { 
                val icon = if (userType == "cuerpo_tecnico") {
                    if (currentIndex == 3) Icons.Default.AdminPanelSettings else Icons.Outlined.AdminPanelSettings
                } else {
                    if (currentIndex == 3) Icons.Default.Groups else Icons.Outlined.Groups
                }
                Icon(icon, contentDescription = null)
            },
            label = { 
                Text(
                    if (userType == "cuerpo_tecnico") "TÉCNICO" else "EQUIPO",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        )
        NavigationBarItem(
            selected = currentIndex == 4,
            onClick = { onItemSelected(4) },
            icon = { Icon(if (currentIndex == 4) Icons.Default.Person else Icons.Outlined.Person, contentDescription = null) },
            label = { Text("PERFIL", fontSize = 9.sp, fontWeight = FontWeight.Bold) }
        )
    }
}

@Composable
fun HomeDrawerContent(
    user: com.example.hockey_app.data.models.UserModel,
    currentIndex: Int,
    onItemSelected: (Int) -> Unit,
    onLogout: () -> Unit
) {
    ModalDrawerSheet(
        drawerContainerColor = Color.White,
        drawerShape = RoundedCornerShape(bottomEnd = 40.dp)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(top = 60.dp, bottom = 30.dp, start = 24.dp, end = 24.dp)
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (user.nombre.isNotEmpty()) user.nombre[0].toString().uppercase() else "?",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                Text(user.nombre, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(user.email, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = Color.White.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (user.user_type == "jugador") "JUGADOR/A" else "CUERPO TÉCNICO",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Menu Items
        DrawerItem(Icons.Default.EmojiEvents, "Torneos", currentIndex == 0) { onItemSelected(0) }
        DrawerItem(Icons.Default.Sports, "Fixture", currentIndex == 1) { onItemSelected(1) }
        DrawerItem(Icons.AutoMirrored.Filled.Article, "Novedades", currentIndex == 2) { onItemSelected(2) }
        DrawerItem(
            if (user.user_type == "cuerpo_tecnico") Icons.Default.AdminPanelSettings else Icons.Default.Groups,
            if (user.user_type == "cuerpo_tecnico") "Panel Técnico" else "Mi Equipo",
            currentIndex == 3
        ) { onItemSelected(3) }
        DrawerItem(Icons.Default.Person, "Mi Perfil", currentIndex == 4) { onItemSelected(4) }

        Spacer(modifier = Modifier.weight(1f))
        HorizontalDivider()
        
        NavigationDrawerItem(
            label = { Text("Cerrar sesión", fontWeight = FontWeight.Bold, color = Color.Red) },
            selected = false,
            onClick = onLogout,
            icon = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = Color.Red) },
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun DrawerItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        label = { Text(label, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium) },
        selected = isSelected,
        onClick = onClick,
        icon = { Icon(icon, contentDescription = null) },
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
        colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
            selectedIconColor = MaterialTheme.colorScheme.primary,
            selectedTextColor = MaterialTheme.colorScheme.primary,
            unselectedIconColor = Color.Gray,
            unselectedTextColor = Color.Black
        )
    )
}
