package com.example.hockey_app.ui.screens.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Logout
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.hockey_app.data.models.UserModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onMenuClick: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToCompareClubs: () -> Unit,
    onNavigateToFavoriteClubs: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val user by viewModel.user.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                viewModel.uploadPhoto(uri)
            }
        }
    )

    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.shadow(8.dp),
                color = MaterialTheme.colorScheme.primary
            ) {
                TopAppBar(
                    title = { Text("MI PERFIL", fontWeight = FontWeight.Black, fontSize = 16.sp, letterSpacing = 1.sp) },
                    navigationIcon = {
                        IconButton(onClick = onMenuClick) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.logout { onNavigateToLogin() } }) {
                            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout")
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
        if (user == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            val accentColor = if (user!!.rama.contains("Fem", true) || user!!.rama.contains("Dama", true)) Color(0xFFFF4081) else Color(0xFF2979FF)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(scrollState)
            ) {
                // Header with Photos
                ProfileHeader(user!!, accentColor = accentColor, onEditPhoto = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                })

                Spacer(modifier = Modifier.height(30.dp))

                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    SectionHeader("ESTADÍSTICAS Y COMPARACIÓN")
                    Spacer(modifier = Modifier.height(12.dp))
                    InfoCard(
                        listOf(
                            InfoItem("Comparar Clubes", "Cara a cara con otros equipos", Icons.Default.Compare, onNavigateToCompareClubs),
                            InfoItem("Mis Favoritos", "Clubes seguidos", Icons.Default.Star, onNavigateToFavoriteClubs),
                            InfoItem("Configuración", "Ajustes de la cuenta", Icons.Default.Settings, onNavigateToSettings)
                        ),
                        accentColor = accentColor
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    SectionHeader("INFORMACIÓN PERSONAL")
                    Spacer(modifier = Modifier.height(12.dp))
                    InfoCard(
                        listOf(
                            InfoItem("Nombre Completo", user!!.nombre, Icons.Default.Person),
                            InfoItem("Email", user!!.email, Icons.Default.AlternateEmail)
                        ),
                        accentColor = accentColor
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    SectionHeader("DATOS DEL CLUB")
                    Spacer(modifier = Modifier.height(12.dp))
                    InfoCard(
                        listOf(
                            InfoItem("Club", user!!.club_nombre, Icons.Default.SportsHockey),
                            InfoItem("Rama", user!!.rama, Icons.Default.People),
                            InfoItem("Categoría", user!!.categoria, Icons.Default.EmojiEvents),
                            InfoItem("División", user!!.division ?: "-", Icons.Default.MilitaryTech),
                            InfoItem("Número Camiseta", if (user!!.numero_camiseta != null) "#${user!!.numero_camiseta}" else "-", Icons.Default.Numbers)
                        ),
                        accentColor = accentColor
                    )
                    
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
fun ProfileHeader(user: UserModel, accentColor: Color, onEditPhoto: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(MaterialTheme.colorScheme.primary, Color(0xFF0F2B48))
                )
            )
            .padding(bottom = 30.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // Profile Photo / Placeholder
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .shadow(12.dp, CircleShape)
                        .background(Color.White, CircleShape)
                        .border(3.dp, Color.White, CircleShape)
                        .clickable { onEditPhoto() },
                    contentAlignment = Alignment.Center
                ) {
                    if (!user.foto_url.isNullOrEmpty()) {
                        AsyncImage(
                            model = user.foto_url,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize().clip(CircleShape)
                        )
                    } else {
                        Icon(Icons.Default.AddAPhoto, contentDescription = null, tint = accentColor, modifier = Modifier.size(30.dp))
                    }
                }

                Spacer(modifier = Modifier.width(20.dp))

                // Club Badge
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .shadow(12.dp, CircleShape)
                        .background(Color.White, CircleShape)
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (!user.club_escudo.isNullOrEmpty()) {
                        AsyncImage(
                            model = user.club_escudo,
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text("🏑", fontSize = 44.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = user.nombre.uppercase(),
                fontWeight = FontWeight.Black,
                fontSize = 20.sp,
                color = Color.White,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                color = Color.White.copy(alpha = 0.15f),
                shape = CircleShape
            ) {
                Text(
                    text = (if (user.user_type == "jugador") "JUGADOR/A DE HOCKEY" else "CUERPO TÉCNICO"),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.5.sp
                )
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 9.sp,
        fontWeight = FontWeight.Black,
        color = Color.Gray,
        letterSpacing = 2.sp
    )
}

@Composable
fun InfoCard(items: List<InfoItem>, accentColor: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(24.dp), ambientColor = accentColor.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Box(modifier = Modifier.fillMaxHeight().width(6.dp).background(accentColor))
            Column {
                items.forEachIndexed { index, item ->
                    InfoTile(item, accentColor)
                    if (index < items.size - 1) {
                        HorizontalDivider(modifier = Modifier.padding(start = 60.dp), thickness = 0.5.dp, color = Color(0xFFF5F5F5))
                    }
                }
            }
        }
    }
}

@Composable
fun InfoTile(item: InfoItem, accentColor: Color) {
    ListItem(
        headlineContent = { Text(item.label, fontSize = 9.sp, color = Color.Gray, fontWeight = FontWeight.Black, letterSpacing = 0.5.sp) },
        supportingContent = { Text(item.value.uppercase(), fontWeight = FontWeight.Black, fontSize = 14.sp, color = Color(0xFF1A1A1A)) },
        leadingContent = {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accentColor.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(item.icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(20.dp))
            }
        },
        trailingContent = if (item.onClick != null) {
            { Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color.LightGray) }
        } else null,
        modifier = if (item.onClick != null) Modifier.clickable(onClick = item.onClick) else Modifier,
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
}

data class InfoItem(val label: String, val value: String, val icon: ImageVector, val onClick: (() -> Unit)? = null)
