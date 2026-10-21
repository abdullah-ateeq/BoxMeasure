package com.example.boxmeasure.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.math.BigDecimal

@Composable
fun BoxDiagram(
    lengthStr: String,
    widthStr: String,
    heightStr: String,
    lengthUnitSymbol: String,
    widthUnitSymbol: String,
    heightUnitSymbol: String,
    animated: Boolean = true,
    modifier: Modifier = Modifier
) {
    // Parse approximate ratios for diagram scaling (clamped between 0.35 and 1.0)
    val lVal = lengthStr.toFloatOrNull()?.takeIf { it > 0 } ?: 10f
    val wVal = widthStr.toFloatOrNull()?.takeIf { it > 0 } ?: 5f
    val hVal = heightStr.toFloatOrNull()?.takeIf { it > 0 } ?: 4f

    val maxVal = maxOf(lVal, wVal, hVal)
    val targetL = (lVal / maxVal).coerceIn(0.4f, 1.0f)
    val targetW = (wVal / maxVal).coerceIn(0.35f, 0.95f)
    val targetH = (hVal / maxVal).coerceIn(0.35f, 1.0f)

    val animL by animateFloatAsState(
        targetValue = targetL,
        animationSpec = if (animated) tween(400) else tween(0),
        label = "animL"
    )
    val animW by animateFloatAsState(
        targetValue = targetW,
        animationSpec = if (animated) tween(400) else tween(0),
        label = "animW"
    )
    val animH by animateFloatAsState(
        targetValue = targetH,
        animationSpec = if (animated) tween(400) else tween(0),
        label = "animH"
    )

    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val tertiaryColor = MaterialTheme.colorScheme.tertiary
    val outlineColor = MaterialTheme.colorScheme.outline
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val onSurface = MaterialTheme.colorScheme.onSurface
    val isDark = isSystemInDarkTheme()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
            .semantics {
                contentDescription = "Rectangular box showing length, width, and height."
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                // Base coordinate bounds
                val centerX = canvasWidth * 0.46f
                val centerY = canvasHeight * 0.58f

                val maxBoxWidth = canvasWidth * 0.48f
                val maxBoxHeight = canvasHeight * 0.46f
                val maxBoxDepth = canvasWidth * 0.28f

                val boxWidth = maxBoxWidth * animL
                val boxHeight = maxBoxHeight * animH
                val boxDepth = maxBoxDepth * animW

                // Isometric / oblique angle
                val depthAngle = Math.toRadians(35.0)
                val dx = (boxDepth * Math.cos(depthAngle)).toFloat()
                val dy = (boxDepth * Math.sin(depthAngle)).toFloat()

                // Front face coordinates:
                // p0 (bottom-left), p1 (bottom-right), p2 (top-right), p3 (top-left)
                val p0 = Offset(centerX - boxWidth / 2f, centerY + boxHeight / 2f)
                val p1 = Offset(centerX + boxWidth / 2f, centerY + boxHeight / 2f)
                val p2 = Offset(centerX + boxWidth / 2f, centerY - boxHeight / 2f)
                val p3 = Offset(centerX - boxWidth / 2f, centerY - boxHeight / 2f)

                // Back face coordinates (shifted by +dx, -dy)
                val p4 = Offset(p0.x + dx, p0.y - dy)
                val p5 = Offset(p1.x + dx, p1.y - dy)
                val p6 = Offset(p2.x + dx, p2.y - dy)
                val p7 = Offset(p3.x + dx, p3.y - dy)

                // Hidden dashed edges (inside/back)
                val dashedPath = Path().apply {
                    moveTo(p0.x, p0.y)
                    lineTo(p4.x, p4.y)
                    lineTo(p5.x, p5.y)
                    moveTo(p4.x, p4.y)
                    lineTo(p7.x, p7.y)
                }
                drawPath(
                    path = dashedPath,
                    color = outlineColor.copy(alpha = 0.35f),
                    style = Stroke(
                        width = 1.5.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f))
                    )
                )

                // Back/Bottom shading (transparent tint)
                val frontFill = primaryColor.copy(alpha = if (isDark) 0.16f else 0.12f)
                val topFill = tertiaryColor.copy(alpha = if (isDark) 0.22f else 0.18f)
                val rightFill = secondaryColor.copy(alpha = if (isDark) 0.28f else 0.22f)

                // Draw Top Face: p3 -> p7 -> p6 -> p2
                val topPath = Path().apply {
                    moveTo(p3.x, p3.y)
                    lineTo(p7.x, p7.y)
                    lineTo(p6.x, p6.y)
                    lineTo(p2.x, p2.y)
                    close()
                }
                drawPath(topPath, topFill)
                drawPath(topPath, outlineColor, style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))

                // Draw Right Face: p1 -> p2 -> p6 -> p5
                val rightPath = Path().apply {
                    moveTo(p1.x, p1.y)
                    lineTo(p2.x, p2.y)
                    lineTo(p6.x, p6.y)
                    lineTo(p5.x, p5.y)
                    close()
                }
                drawPath(rightPath, rightFill)
                drawPath(rightPath, outlineColor, style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))

                // Draw Front Face: p0 -> p1 -> p2 -> p3
                val frontPath = Path().apply {
                    moveTo(p0.x, p0.y)
                    lineTo(p1.x, p1.y)
                    lineTo(p2.x, p2.y)
                    lineTo(p3.x, p3.y)
                    close()
                }
                drawPath(frontPath, frontFill)
                drawPath(frontPath, primaryColor, style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))

                // Dimension guides and arrows
                val guideColor = primaryColor

                // Length guide (Bottom, front):
                val lGuideY = p0.y + 18.dp.toPx()
                drawLine(
                    color = guideColor,
                    start = Offset(p0.x, lGuideY),
                    end = Offset(p1.x, lGuideY),
                    strokeWidth = 2.dp.toPx()
                )
                // Length ticks
                drawLine(guideColor, Offset(p0.x, lGuideY - 5.dp.toPx()), Offset(p0.x, lGuideY + 5.dp.toPx()), 1.5.dp.toPx())
                drawLine(guideColor, Offset(p1.x, lGuideY - 5.dp.toPx()), Offset(p1.x, lGuideY + 5.dp.toPx()), 1.5.dp.toPx())

                // Height guide (Left, vertical):
                val hGuideX = p0.x - 18.dp.toPx()
                drawLine(
                    color = tertiaryColor,
                    start = Offset(hGuideX, p3.y),
                    end = Offset(hGuideX, p0.y),
                    strokeWidth = 2.dp.toPx()
                )
                // Height ticks
                drawLine(tertiaryColor, Offset(hGuideX - 5.dp.toPx(), p3.y), Offset(hGuideX + 5.dp.toPx(), p3.y), 1.5.dp.toPx())
                drawLine(tertiaryColor, Offset(hGuideX - 5.dp.toPx(), p0.y), Offset(hGuideX + 5.dp.toPx(), p0.y), 1.5.dp.toPx())

                // Width / Depth guide (Top-right along depth):
                val wStart = Offset(p6.x + 10.dp.toPx(), p6.y)
                val wEnd = Offset(p2.x + 10.dp.toPx(), p2.y)
                drawLine(
                    color = secondaryColor,
                    start = wStart,
                    end = wEnd,
                    strokeWidth = 2.dp.toPx()
                )
                drawLine(secondaryColor, Offset(wStart.x - 3.dp.toPx(), wStart.y - 3.dp.toPx()), Offset(wStart.x + 3.dp.toPx(), wStart.y + 3.dp.toPx()), 1.5.dp.toPx())
                drawLine(secondaryColor, Offset(wEnd.x - 3.dp.toPx(), wEnd.y - 3.dp.toPx()), Offset(wEnd.x + 3.dp.toPx(), wEnd.y + 3.dp.toPx()), 1.5.dp.toPx())

                // Text labels using native canvas
                val paint = android.graphics.Paint().apply {
                    color = onSurface.hashCode()
                    textSize = 12.sp.toPx()
                    isAntiAlias = true
                    typeface = android.graphics.Typeface.DEFAULT_BOLD
                    textAlign = android.graphics.Paint.Align.CENTER
                }

                val primaryPaint = android.graphics.Paint(paint).apply {
                    color = primaryColor.hashCode()
                }
                val secondaryPaint = android.graphics.Paint(paint).apply {
                    color = secondaryColor.hashCode()
                    textAlign = android.graphics.Paint.Align.LEFT
                }
                val tertiaryPaint = android.graphics.Paint(paint).apply {
                    color = tertiaryColor.hashCode()
                    textAlign = android.graphics.Paint.Align.RIGHT
                }

                // L label
                drawContext.canvas.nativeCanvas.drawText(
                    "L: $lengthStr $lengthUnitSymbol",
                    (p0.x + p1.x) / 2f,
                    lGuideY + 16.dp.toPx(),
                    primaryPaint
                )

                // H label
                drawContext.canvas.nativeCanvas.drawText(
                    "H: $heightStr $heightUnitSymbol",
                    hGuideX - 8.dp.toPx(),
                    (p0.y + p3.y) / 2f + 4.dp.toPx(),
                    tertiaryPaint
                )

                // W label
                drawContext.canvas.nativeCanvas.drawText(
                    "W: $widthStr $widthUnitSymbol",
                    (wStart.x + wEnd.x) / 2f + 8.dp.toPx(),
                    (wStart.y + wEnd.y) / 2f + 4.dp.toPx(),
                    secondaryPaint
                )
            }
        }

        Text(
            text = "Enter the three inside dimensions of the box.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
