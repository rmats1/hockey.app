package com.example.hockey_app.ui.screens.torneos

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StatsWidget(
    points: List<Float>,
    labels: List<String>,
    teamName: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "RENDIMIENTO: $teamName".uppercase(),
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.secondary,
                letterSpacing = 1.sp
            )
            Text(
                text = "ÚLTIMOS PARTIDOS",
                fontSize = 9.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                if (points.isEmpty()) {
                    Text("No hay datos suficientes", modifier = Modifier.align(Alignment.Center), color = Color.LightGray)
                } else {
                    val primaryColor = MaterialTheme.colorScheme.primary
                    val secondaryColor = MaterialTheme.colorScheme.secondary
                    val errorColor = MaterialTheme.colorScheme.error

                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val canvasWidth = size.width
                        val canvasHeight = size.height
                        val barWidth = 40.dp.toPx()
                        val spacing = (canvasWidth - (barWidth * points.size)) / (points.size + 1)
                        val maxPoints = 3f

                        points.forEachIndexed { index, point ->
                            val barHeight = (point / maxPoints) * canvasHeight
                            val x = spacing + (index * (barWidth + spacing))
                            val y = canvasHeight - barHeight
                            
                            val color = when(point) {
                                3f -> primaryColor
                                1f -> secondaryColor
                                else -> errorColor
                            }

                            drawRoundRect(
                                color = color,
                                topLeft = Offset(x, y),
                                size = Size(barWidth, barHeight),
                                cornerRadius = CornerRadius(4.dp.toPx())
                            )
                        }
                    }
                    
                    // Labels
                    Row(
                        modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        labels.forEach { label ->
                            Text(label, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}
