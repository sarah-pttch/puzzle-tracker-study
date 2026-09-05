package com.study.puzzletracker.ui.statistics

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CompletionTimeChart(completionSeconds: List<Long>, modifier: Modifier = Modifier) {
    if (completionSeconds.size < 2) {
        Text("Need at least 2 finished attempts to show a trend", modifier = modifier.padding(16.dp))
        return
    }

    val maxTime = completionSeconds.max()
    val axisMin = 0L
    val lineColor = MaterialTheme.colorScheme.primary
    val axisColor = MaterialTheme.colorScheme.onSurface
    val gridColor = MaterialTheme.colorScheme.outlineVariant
    val labelColor = MaterialTheme.colorScheme.onSurface.toArgb()
    val leftPadding = 48.dp
    val bottomPadding = 32.dp
    val topPadding = 16.dp

    Canvas(modifier = modifier.fillMaxWidth().height(320.dp)) {
        val axisStrokeWidth = 2.dp.toPx()
        val gridStrokeWidth = 1.dp.toPx()
        val chartWidth = size.width - leftPadding.toPx()
        val chartTop = topPadding.toPx()
        val chartBottom = size.height - bottomPadding.toPx()
        val chartHeight = chartBottom - chartTop
        val stepX = chartWidth / (completionSeconds.size - 1)
        val range = (maxTime - axisMin).coerceAtLeast(1).toFloat()

        fun yFor(value: Long): Float =
            chartBottom - ((value - axisMin).toFloat() / range) * chartHeight

        val points = completionSeconds.mapIndexed { index, value ->
            Offset(x = leftPadding.toPx() + index * stepX, y = yFor(value))
        }

        // y
        drawLine(
            color = axisColor,
            start = Offset(leftPadding.toPx(), chartTop),
            end = Offset(leftPadding.toPx(), chartBottom),
            strokeWidth = axisStrokeWidth
        )
        // x
        drawLine(
            color = axisColor,
            start = Offset(leftPadding.toPx(), chartBottom),
            end = Offset(size.width, chartBottom),
            strokeWidth = axisStrokeWidth
        )

        val textPaint = Paint().apply {
            color = labelColor
            textSize = 11.sp.toPx()
            textAlign = Paint.Align.RIGHT
        }
        val yTicks = listOf(axisMin, maxTime / 4, maxTime / 2, (maxTime * 3) / 4, maxTime)
        yTicks.forEach { tickValue ->
            val y = yFor(tickValue)
            drawLine(
                color = gridColor,
                start = Offset(leftPadding.toPx(), y),
                end = Offset(size.width, y),
                strokeWidth = gridStrokeWidth
            )
            drawContext.canvas.nativeCanvas.drawText(
                formatDuration(tickValue),
                leftPadding.toPx() - 8f,
                y + 4f,
                textPaint
            )
            drawLine(
                color = axisColor.copy(alpha = 0.3f),
                start = Offset(leftPadding.toPx(), y),
                end = Offset(size.width, y),
                strokeWidth = 1f
            )
        }

        val xTextPaint = Paint().apply {
            color = labelColor
            textSize = 11.sp.toPx()
            textAlign = Paint.Align.CENTER
        }
        points.forEachIndexed { index, point ->
            drawContext.canvas.nativeCanvas.drawText(
                "${index + 1}",
                point.x,
                chartBottom + 24f,
                xTextPaint
            )
        }

        for (i in 0 until points.size - 1) {
            drawLine(
                color = lineColor,
                start = points[i],
                end = points[i + 1],
                strokeWidth = 4.dp.toPx()
            )
        }
        points.forEach { point ->
            drawCircle(color = lineColor, radius = 5.dp.toPx(), center = point)
        }
    }
}

private fun formatDuration(totalSeconds: Long): String {
    val m = totalSeconds / 60
    val s = totalSeconds % 60
    return "%d:%02d".format(m, s)
}