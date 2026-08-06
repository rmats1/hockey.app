package com.example.hockey_app.ui.screens.profile

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private data class Pregunta(val pregunta: String, val respuesta: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AyudaScreen(
    onBack: () -> Unit
) {
    val preguntas = listOf(
        Pregunta(
            "¿Cómo me registro?",
            "Tocá \"Registrate\" en la pantalla de login, completá tus datos y elegí tu club. Necesitás un email válido y una contraseña de al menos 6 caracteres."
        ),
        Pregunta(
            "¿Cómo inicio con huella?",
            "Primero registrate con email. Después, cada vez que quieras entrar, podés usar tu huella dactilar si tu dispositivo lo soporta."
        ),
        Pregunta(
            "¿Puedo cambiar de club?",
            "Por ahora no. Una vez que elegiste tu club al registrarte, queda fijo. En futuras versiones se podrá editar."
        ),
        Pregunta(
            "¿Los datos son reales?",
            "Los datos de torneos, partidos, posiciones y goleadores son de ejemplo para demostración. En producción se conectarían a la base de datos oficial de la AHBA."
        ),
        Pregunta(
            "¿Cómo funcionan los filtros?",
            "En cada pantalla hay chips arriba que te permiten filtrar por categoría, división, estado, etc. Tocá el que quieras y la lista se actualiza."
        ),
        Pregunta(
            "¿Qué pasa si no aparece mi club?",
            "Tenemos cargados los 140 clubes principales de la AHBA. Si el tuyo no aparece, contactanos para agregarlo."
        ),
    )
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Soporte", fontWeight = FontWeight.Bold) },
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
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.HelpCenter, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(28.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("¿Cómo podemos ayudarte?", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Preguntas frecuentes y soporte técnico", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                    }
                }
            }

            Column(modifier = Modifier.padding(20.dp)) {
                // FAQ Section
                SectionTitle("PREGUNTAS FRECUENTES")
                Spacer(modifier = Modifier.height(16.dp))
                preguntas.forEachIndexed { index, pregunta ->
                    FaqCard(index + 1, pregunta)
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Contact Section
                SectionTitle("CONTACTO DIRECTO")
                Spacer(modifier = Modifier.height(16.dp))
                ContactCard(
                    onEmailClick = {
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:soporte@hockeyahba.com.ar")
                        }
                        context.startActivity(intent)
                    }
                )
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.width(4.dp).height(16.dp).clip(CircleShape).background(MaterialTheme.colorScheme.secondary))
        Spacer(modifier = Modifier.width(10.dp))
        Text(title, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp, letterSpacing = 1.sp)
    }
}

@Composable
private fun FaqCard(index: Int, pregunta: Pregunta) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(28.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("$index", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(pregunta.pregunta, fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.weight(1f))
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = if (expanded) MaterialTheme.colorScheme.primary else Color.Gray
                    )
                }
            }
            if (expanded) {
                Text(
                    pregunta.respuesta,
                    modifier = Modifier.padding(start = 56.dp, end = 20.dp, bottom = 20.dp),
                    fontSize = 13.sp,
                    color = Color.Gray,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
private fun ContactCard(onEmailClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.AlternateEmail, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text("¿Tenés otra consulta?", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Escribinos y te responderemos a la brevedad.",
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                color = Color.Gray,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(20.dp))
            OutlinedButton(
                onClick = onEmailClick,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("soporte@hockeyahba.com.ar", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }
}
