package com.example.hockey_app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp

/** Compact mark formed by two field-hockey sticks, suitable for the app logo. */
@Composable
fun HockeySticksLogo(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val shaft = size.minDimension * 0.065f
        val blade = size.minDimension * 0.11f
        val gold = Brush.linearGradient(listOf(Color(0xFFFFD166), Color(0xFFE89B2C)))
        val navy = Color(0xFF0B2D4D)

        fun drawStick(angle: Float, offset: Offset, color: Brush) {
            rotate(angle, pivot = Offset(size.width / 2f, size.height / 2f)) {
                val start = Offset(size.width * 0.50f + offset.x, size.height * 0.15f + offset.y)
                val shaftEnd = Offset(start.x, size.height * 0.72f)
                drawLine(color, start, shaftEnd, shaft, StrokeCap.Round)

                val hook = Path().apply {
                    moveTo(shaftEnd.x, shaftEnd.y - blade * 0.25f)
                    cubicTo(
                        shaftEnd.x, shaftEnd.y + blade * 0.75f,
                        shaftEnd.x + blade * 0.25f, shaftEnd.y + blade,
                        shaftEnd.x + blade * 1.35f, shaftEnd.y + blade
                    )
                    lineTo(shaftEnd.x + blade * 1.35f, shaftEnd.y + blade * 0.55f)
                    cubicTo(
                        shaftEnd.x + blade * 0.75f, shaftEnd.y + blade * 0.55f,
                        shaftEnd.x + blade * 0.45f, shaftEnd.y + blade * 0.35f,
                        shaftEnd.x + blade * 0.42f, shaftEnd.y - blade * 0.25f
                    )
                    close()
                }
                drawPath(hook, color)
                drawLine(navy, Offset(start.x, start.y + size.height * 0.06f), Offset(start.x, start.y + size.height * 0.16f), shaft * 0.32f)
            }
        }

        drawStick(-18f, Offset(-size.width * 0.10f, 0f), gold)
        drawStick(18f, Offset(size.width * 0.10f, size.height * 0.03f), Brush.linearGradient(listOf(Color(0xFF63C7D8), Color(0xFF1B7894))))
        drawArc(color = navy, startAngle = 200f, sweepAngle = 140f, useCenter = false, topLeft = Offset(size.width * 0.24f, size.height * 0.67f), size = Size(size.width * 0.52f, size.height * 0.31f), style = Stroke(width = size.minDimension * 0.025f, cap = StrokeCap.Round))
    }
}
