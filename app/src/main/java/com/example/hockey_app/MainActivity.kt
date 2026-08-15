package com.example.hockey_app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SecurityUpdateWarning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.hockey_app.ui.navigation.AppNavigation
import com.example.hockey_app.ui.theme.HockeyPlusTheme
import com.example.hockey_app.utils.SecurityUtils
import dagger.hilt.android.AndroidEntryPoint
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.handleDeeplinks
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var supabaseClient: SupabaseClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Only process the exact callback shape registered in the manifest.
        handleSupabaseDeepLink(intent)
        
        // Seguridad: Prevenir capturas de pantalla
        SecurityUtils.setSecureWindow(this)
        
        val isRooted = SecurityUtils.isDeviceRooted()
        
        enableEdgeToEdge()
        setContent {
            HockeyPlusTheme {
                if (isRooted) {
                    CompromisedDeviceScreen()
                } else {
                    AppNavigation()
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleSupabaseDeepLink(intent)
    }

    private fun handleSupabaseDeepLink(intent: Intent) {
        val isSupabaseCallback =
            intent.action == Intent.ACTION_VIEW &&
                intent.data?.scheme == "hockeyapp" &&
                intent.data?.host == "login-callback"

        if (isSupabaseCallback) {
            supabaseClient.handleDeeplinks(intent)
        }
    }
}

@Composable
fun CompromisedDeviceScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.errorContainer
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.SecurityUpdateWarning,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Dispositivo No Seguro",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Por razones de seguridad, esta aplicación no puede ejecutarse en dispositivos con Root o Jailbreak.",
                textAlign = TextAlign.Center
            )
        }
    }
}
