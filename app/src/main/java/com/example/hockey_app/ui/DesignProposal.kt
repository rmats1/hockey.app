package com.example.hockey_app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Preview(showBackground = true, backgroundColor = 0xFFF5F5F5)
@Composable
fun PremiumDesignProposal() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Concept 1: Damas
        ProposalCard(
            title = "DAMAS A - PRIMERA",
            subtitle = "DIVISIÓN A",
            rama = "Femenino"
        )
        
        // Concept 2: Caballeros
        ProposalCard(
            title = "CABALLEROS B - CAMPEONATO",
            subtitle = "DIVISIÓN B",
            rama = "Masculino"
        )
    }
}

@Composable
fun ProposalCard(title: String, subtitle: String, rama: String) {
    val accentColor = if (rama == "Femenino") Color(0xFFFF4081) else Color(0xFF2979FF)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(20.dp), ambientColor = accentColor.copy(alpha = 0.4f)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Lateral accent bar
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(8.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(accentColor, accentColor.copy(alpha = 0.6f))
                        )
                    )
            )
            
            Row(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title, 
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        letterSpacing = 0.5.sp,
                        color = Color(0xFF1A1A1A)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = accentColor.copy(alpha = 0.1f),
                            shape = CircleShape
                        ) {
                            Text(
                                text = rama.uppercase(),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 3.dp),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = accentColor
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = subtitle, 
                            fontSize = 11.sp, 
                            color = Color.Gray, 
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos, 
                    contentDescription = null, 
                    tint = Color(0xFFD1D1D1), 
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
