package com.example.hockey_app.ui.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.SportsHockey
import com.example.hockey_app.ui.components.HockeySticksLogo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

@Composable
fun LoginScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val email by viewModel.email.collectAsStateWithLifecycle()
    val password by viewModel.password.collectAsStateWithLifecycle()
    val state by viewModel.state.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    var showConfetti by remember { mutableStateOf(false) }

    LaunchedEffect(state) {
        if (state is LoginState.Success) {
            showConfetti = true
            delay(1500)
            onNavigateToHome()
        } else if (state is LoginState.Error) {
            snackbarHostState.showSnackbar((state as LoginState.Error).message)
            viewModel.resetError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.White
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 30.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(60.dp))

                // Premium Logo
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .shadow(25.dp, CircleShape, ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)
                            ),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SportsHockey,
                        contentDescription = null,
                        modifier = Modifier.size(60.dp),
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))

                Text(
                    text = "Hockey AHBA",
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = (-1.5).sp
                )
                Text(
                    text = "ASOCIACIÓN DE HOCKEY DE BS. AS.",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Gray,
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(80.dp))

                // Email Field
                OutlinedTextField(
                    value = email,
                    onValueChange = viewModel::onEmailChange,
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.AlternateEmail, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                    shape = RoundedCornerShape(15.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color(0xFFEEEEEE)
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Password Field
                OutlinedTextField(
                    value = password,
                    onValueChange = viewModel::onPasswordChange,
                    label = { Text("Contraseña") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                    shape = RoundedCornerShape(15.dp),
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color(0xFFEEEEEE)
                    )
                )

                Spacer(modifier = Modifier.height(30.dp))

                // Login Button
                Button(
                    onClick = { viewModel.login() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp)
                        .shadow(10.dp, RoundedCornerShape(15.dp), ambientColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(15.dp),
                    enabled = state !is LoginState.Loading
                ) {
                    if (state is LoginState.Loading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text(
                            text = "INICIAR SESIÓN",
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Register Link
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "¿No tienes cuenta? ", color = Color.Gray, fontSize = 14.sp)
                    TextButton(onClick = onNavigateToRegister) {
                        Text(
                            text = "Regístrate",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            if (showConfetti) {
                KonfettiView(
                    modifier = Modifier.fillMaxSize(),
                    parties = listOf(
                        Party(
                            speed = 0f,
                            maxSpeed = 30f,
                            damping = 0.9f,
                            spread = 360,
                            colors = listOf(0xfce18a, 0xff726d, 0xf41331, 0xd82653),
                            emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(100),
                            position = Position.Relative(0.5, 0.3)
                        )
                    )
                )
            }
        }
    }
}
