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
                            androidx.compose.ui.res.painterResource(id = com.example.hockey_app.R.drawable.ic_google_logo) // Placeholder for AsyncImage check
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
                    .background(Color(0xFF2E7D32)) // Field Green
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                currentPath = PathData(Path().apply { moveTo(offset.x, offset.y) })
                            },
                            onDrag = { change, dragAmount ->
                                currentPath?.path?.lineTo(change.position.x, change.position.y)
                                // Trigger recomposition
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
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawHockeyField()

                    // Watermark Escudo
                    if (clubEscudo != null) {
                        // Drawing an image on Canvas is complex, better to use an AsyncImage behind or on top
                    }

                    // Draw saved paths
                    paths.forEach { pathData ->
                        drawPath(
                            path = pathData.path,
                            color = Color.Yellow,
                            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }

                    // Draw current path
                    currentPath?.let { pathData ->
                        drawPath(
                            path = pathData.path,
                            color = Color.Yellow,
                            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                }

                // Watermark Escudo (Overlay)
                if (clubEscudo != null) {
                    coil.compose.AsyncImage(
                        model = clubEscudo,
                        contentDescription = null,
                        modifier = Modifier
                            .size(150.dp)
                            .align(Alignment.Center)
                            .alpha(0.15f)
                    )
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
    val paintColor = Color.White.copy(alpha = 0.5f)
    val strokeWidth = 1.5.dp.toPx()
    val dottedStroke = Stroke(width = strokeWidth, pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f))
    val normalStroke = Stroke(width = strokeWidth)

    // Boundaries (91.4m x 55m)
    drawRect(color = paintColor, size = size, style = normalStroke)

    val meterToPx = size.height / 91.4f
    val meterToPxW = size.width / 55f
    val centerX = size.width / 2f

    // Center Line
    drawLine(color = paintColor, start = Offset(0f, size.height / 2f), end = Offset(size.width, size.height / 2f), strokeWidth = strokeWidth)

    // 22.9m Lines (25-yard lines)
    val line23mTop = 22.9f * meterToPx
    val line23mBottom = (91.4f - 22.9f) * meterToPx
    drawLine(color = paintColor, start = Offset(0f, line23mTop), end = Offset(size.width, line23mTop), strokeWidth = strokeWidth)
    drawLine(color = paintColor, start = Offset(0f, line23mBottom), end = Offset(size.width, line23mBottom), strokeWidth = strokeWidth)

    // Shooting Circles (D) - Radius 14.63m
    val radiusD = 14.63f * meterToPxW
    val radiusDotted = (14.63f + 5f) * meterToPxW

    // Goal straight line (3.66m)
    val goalWidthHalf = 1.83f * meterToPxW

    fun drawArea(isTop: Boolean) {
        val yBase = if (isTop) 0f else size.height
        val angleStart = if (isTop) 0f else 180f
        
        // D Circle arcs
        drawArc(
            color = paintColor,
            startAngle = angleStart,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(centerX - radiusD, yBase - radiusD),
            size = Size(radiusD * 2f, radiusD * 2f),
            style = normalStroke
        )
        
        // Dotted Circle arcs
        drawArc(
            color = paintColor,
            startAngle = angleStart,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(centerX - radiusDotted, yBase - radiusDotted),
            size = Size(radiusDotted * 2f, radiusDotted * 2f),
            style = dottedStroke
        )

        // Penalty Spot (6.4m)
        val penaltyY = if (isTop) 6.4f * meterToPx else size.height - (6.4f * meterToPx)
        drawCircle(color = paintColor, radius = 2.dp.toPx(), center = Offset(centerX, penaltyY))

        // Long corner marks (side lines, 5m from backline)
        val cornerY = if (isTop) 5f * meterToPx else size.height - (5f * meterToPx)
        drawLine(color = paintColor, start = Offset(0f, cornerY), end = Offset(5.dp.toPx(), cornerY), strokeWidth = strokeWidth)
        drawLine(color = paintColor, start = Offset(size.width - 5.dp.toPx(), cornerY), end = Offset(size.width, cornerY), strokeWidth = strokeWidth)
    }

    drawArea(true)
    drawArea(false)

    // Goals (3.66m wide)
    val goalW = 3.66f * meterToPxW
    drawRect(color = paintColor, topLeft = Offset(centerX - goalW / 2f, -8.dp.toPx()), size = Size(goalW, 8.dp.toPx()), style = normalStroke)
    drawRect(color = paintColor, topLeft = Offset(centerX - goalW / 2f, size.height), size = Size(goalW, 8.dp.toPx()), style = normalStroke)
}

data class PathData(val path: Path)
