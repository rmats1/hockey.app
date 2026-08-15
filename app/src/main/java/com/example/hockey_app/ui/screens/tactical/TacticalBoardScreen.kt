package com.example.hockey_app.ui.screens.tactical

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TacticalBoardScreen(
    onBack: () -> Unit,
    clubEscudo: String? = null
) {
    var paths by remember { mutableStateOf(listOf<PathData>()) }
    var currentPath by remember { mutableStateOf<PathData?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (clubEscudo != null) {
                            coil.compose.AsyncImage(
                                model = clubEscudo,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp).clip(CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text("PIZARRA TÁCTICA", fontWeight = FontWeight.Black, fontSize = 16.sp) 
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { paths = emptyList() }) {
                        Icon(Icons.Default.DeleteSweep, contentDescription = "Clear", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = Color(0xFF004D40) // Darker hockey green
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color(0xFF1B5E20)) // Darker, professional field green
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                currentPath = PathData(Path().apply { moveTo(offset.x, offset.y) })
                            },
                            onDrag = { change, _ ->
                                currentPath?.path?.lineTo(change.position.x, change.position.y)
                                // Force recomposition safely
                                val temp = currentPath
                                currentPath = null
                                currentPath = temp
                            },
                            onDragEnd = {
                                currentPath?.let { paths = paths + it }
                                currentPath = null
                            }
                        )
                    }
            ) {
                // Background Watermark (Behind everything)
                if (clubEscudo != null) {
                    coil.compose.AsyncImage(
                        model = clubEscudo,
                        contentDescription = null,
                        modifier = Modifier
                            .size(240.dp) // Larger watermark
                            .align(Alignment.Center)
                            .alpha(0.12f) // Very subtle
                    )
                }

                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawHockeyField()

                    // Draw saved paths
                    paths.forEach { pathData ->
                        drawPath(
                            path = pathData.path,
                            color = Color(0xFFFFEB3B), // Brighter tactical yellow
                            style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }

                    // Draw current path
                    currentPath?.let { pathData ->
                        drawPath(
                            path = pathData.path,
                            color = Color(0xFFFFEB3B),
                            style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                }
            }
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Dibuja sobre el campo FIH reglamentario",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawHockeyField() {
    // FIH standard field: 91.40 m long x 55.00 m wide.
    // Keep the real aspect ratio so circles and distances are not distorted.
    val fieldScale = minOf(size.width / 55f, size.height / 91.4f)
    val fieldWidth = 55f * fieldScale
    val fieldHeight = 91.4f * fieldScale
    val left = (size.width - fieldWidth) / 2f
    val top = (size.height - fieldHeight) / 2f
    val line = 0.12f * fieldScale
    val white = Color.White.copy(alpha = 0.82f)
    val dashed = Stroke(
        width = line,
        pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
            floatArrayOf(0.7f * fieldScale, 0.7f * fieldScale), 0f
        )
    )

    withTransform({
        translate(left, top)
    }) {
        val centerX = fieldWidth / 2f
        val centerY = fieldHeight / 2f
        val goalLineDistance = 22.9f * fieldScale
        val circleRadius = 14.63f * fieldScale
        val dottedRadius = (14.63f + 5f) * fieldScale
        val penaltySpot = 6.475f * fieldScale
        val goalWidth = 3.66f * fieldScale
        val goalDepth = 1.2f * fieldScale

        drawRect(
            color = white,
            topLeft = Offset.Zero,
            size = Size(fieldWidth, fieldHeight),
            style = Stroke(line)
        )
        drawLine(white, Offset(0f, centerY), Offset(fieldWidth, centerY), line)
        drawLine(white, Offset(0f, goalLineDistance), Offset(fieldWidth, goalLineDistance), line)
        drawLine(white, Offset(0f, fieldHeight - goalLineDistance), Offset(fieldWidth, fieldHeight - goalLineDistance), line)

        fun drawCircleAndMarks(backlineY: Float, inward: Float) {
            drawArc(
                color = white,
                startAngle = if (inward > 0f) 0f else 180f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(centerX - circleRadius, backlineY - circleRadius),
                size = Size(circleRadius * 2f, circleRadius * 2f),
                style = Stroke(line)
            )
            drawArc(
                color = white,
                startAngle = if (inward > 0f) 0f else 180f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(centerX - dottedRadius, backlineY - dottedRadius),
                size = Size(dottedRadius * 2f, dottedRadius * 2f),
                style = dashed
            )
            drawCircle(white, 0.32f * fieldScale, Offset(centerX, backlineY + inward * penaltySpot))
            listOf(5f, 10f).forEach { distance ->
                val x = distance * fieldScale
                drawLine(white, Offset(centerX - x, backlineY), Offset(centerX - x, backlineY + inward * 0.5f * fieldScale), line)
                drawLine(white, Offset(centerX + x, backlineY), Offset(centerX + x, backlineY + inward * 0.5f * fieldScale), line)
            }
        }

        drawCircleAndMarks(0f, 1f)
        drawCircleAndMarks(fieldHeight, -1f)

        drawLine(white, Offset(0f, 5f * fieldScale), Offset(0.5f * fieldScale, 5f * fieldScale), line)
        drawLine(white, Offset(fieldWidth, 5f * fieldScale), Offset(fieldWidth - 0.5f * fieldScale, 5f * fieldScale), line)
        drawLine(white, Offset(0f, fieldHeight - 5f * fieldScale), Offset(0.5f * fieldScale, fieldHeight - 5f * fieldScale), line)
        drawLine(white, Offset(fieldWidth, fieldHeight - 5f * fieldScale), Offset(fieldWidth - 0.5f * fieldScale, fieldHeight - 5f * fieldScale), line)

        drawRect(
            color = white,
            topLeft = Offset(centerX - goalWidth / 2f, -goalDepth),
            size = Size(goalWidth, goalDepth),
            style = Stroke(line)
        )
        drawRect(
            color = white,
            topLeft = Offset(centerX - goalWidth / 2f, fieldHeight),
            size = Size(goalWidth, goalDepth),
            style = Stroke(line)
        )
    }
}

data class PathData(val path: Path)
