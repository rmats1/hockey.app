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
fun TacticalBoardScreen(onBack: () -> Unit) {
    var paths by remember { mutableStateOf(listOf<PathData>()) }
    var currentPath by remember { mutableStateOf<PathData?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PIZARRA TÁCTICA", fontWeight = FontWeight.Black, fontSize = 16.sp) },
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
        containerColor = Color(0xFF003300)
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                currentPath = PathData(Path().apply { moveTo(offset.x, offset.y) })
                            },
                            onDrag = { change, dragAmount ->
                                currentPath?.path?.lineTo(change.position.x, change.position.y)
                                // Trigger recomposition by creating a new list or using a state holder
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
                    // Draw Hockey Field
                    drawHockeyField()

                    // Draw saved paths
                    paths.forEach { pathData ->
                        drawPath(
                            path = pathData.path,
                            color = Color.White,
                            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }

                    // Draw current path
                    currentPath?.let { pathData ->
                        drawPath(
                            path = pathData.path,
                            color = Color.White,
                            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                }
            }
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.1f))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Usa tu dedo para dibujar jugadas sobre la cancha.",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 11.sp,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawHockeyField() {
    val paintColor = Color.White.copy(alpha = 0.4f)
    val strokeWidth = 2.dp.toPx()

    // Outer boundary
    drawRect(
        color = paintColor,
        size = size,
        style = Stroke(width = strokeWidth)
    )

    // Center line
    drawLine(
        color = paintColor,
        start = Offset(0f, size.height / 2),
        end = Offset(size.width, size.height / 2),
        strokeWidth = strokeWidth
    )

    // Center circle
    drawCircle(
        color = paintColor,
        radius = 50.dp.toPx(),
        center = center,
        style = Stroke(width = strokeWidth)
    )

    // Areas (Simplified)
    drawArc(
        color = paintColor,
        startAngle = 0f,
        sweepAngle = 180f,
        useCenter = false,
        topLeft = Offset(center.x - 100.dp.toPx(), -75.dp.toPx()),
        size = Size(200.dp.toPx(), 150.dp.toPx()),
        style = Stroke(width = strokeWidth)
    )

    drawArc(
        color = paintColor,
        startAngle = 180f,
        sweepAngle = 180f,
        useCenter = false,
        topLeft = Offset(center.x - 100.dp.toPx(), size.height - 75.dp.toPx()),
        size = Size(200.dp.toPx(), 150.dp.toPx()),
        style = Stroke(width = strokeWidth)
    )
}

data class PathData(val path: Path)
