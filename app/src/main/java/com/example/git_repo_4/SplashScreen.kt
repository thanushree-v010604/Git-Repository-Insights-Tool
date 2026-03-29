package com.example.git_repo_4

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.git_repo_4.ui.theme.Git_repo_4Theme
import kotlinx.coroutines.delay

private val SplashBgTop = Color(0xFF0A0F14)
private val SplashBgMid = Color(0xFF0A0F14)
private val SplashBgBottom = Color(0xFF101820)
private val SplashAccent = Color(0xFF00E5FF)
private val SplashSurface = Color(0xFF1F2A33)
private val SplashTextPrimary = Color(0xFFE6F7FF)
private val SplashTextSecondary = Color(0xFF7AA6B2)

@Composable
fun SplashScreen(
    onTimeout: () -> Unit = {}
) {
    var progress by remember { mutableFloatStateOf(0f) }
    val infiniteTransition = rememberInfiniteTransition(label = "rotation")

    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    // Navigate to log in screen after 5 seconds
    LaunchedEffect(Unit) {
        // Animate progress from 0 to 1 over 5 seconds
        val duration = 5000L
        val steps = 50
        val delayTime = duration / steps

        repeat(steps) {
            delay(delayTime)
            progress = (it + 1).toFloat() / steps
        }

        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(SplashBgTop, SplashBgMid, SplashBgBottom)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Logo Circle with Icon
            Box(
                modifier = Modifier.size(200.dp),
                contentAlignment = Alignment.Center
            ) {
                // Outer circle border
                Canvas(modifier = Modifier.size(200.dp)) {
                    drawCircle(
                        color = SplashSurface,
                        radius = size.minDimension / 2,
                        style = Stroke(width = 2.dp.toPx())
                    )
                }

                // RepoPulse Icon (simplified version)
                Canvas(modifier = Modifier.size(100.dp)) {
                    val strokeWidth = 12.dp.toPx()
                    val centerX = size.width / 2
                    val centerY = size.height / 2

                    // Left vertical line with circles
                    drawCircle(
                        color = SplashAccent,
                        radius = 8.dp.toPx(),
                        center = Offset(centerX - 30.dp.toPx(), centerY - 40.dp.toPx())
                    )

                    drawLine(
                        color = SplashAccent,
                        start = Offset(centerX - 30.dp.toPx(), centerY - 32.dp.toPx()),
                        end = Offset(centerX - 30.dp.toPx(), centerY + 32.dp.toPx()),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )

                    drawCircle(
                        color = SplashAccent,
                        radius = 8.dp.toPx(),
                        center = Offset(centerX - 30.dp.toPx(), centerY + 40.dp.toPx())
                    )

                    // Curved connector
                    drawLine(
                        color = SplashAccent,
                        start = Offset(centerX - 30.dp.toPx(), centerY + 32.dp.toPx()),
                        end = Offset(centerX - 10.dp.toPx(), centerY + 32.dp.toPx()),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )

                    drawLine(
                        color = SplashAccent,
                        start = Offset(centerX - 10.dp.toPx(), centerY + 32.dp.toPx()),
                        end = Offset(centerX + 20.dp.toPx(), centerY + 10.dp.toPx()),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )

                    // Right vertical line with circle
                    drawLine(
                        color = SplashAccent,
                        start = Offset(centerX + 20.dp.toPx(), centerY + 10.dp.toPx()),
                        end = Offset(centerX + 20.dp.toPx(), centerY + 32.dp.toPx()),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )

                    drawCircle(
                        color = SplashAccent,
                        radius = 8.dp.toPx(),
                        center = Offset(centerX + 20.dp.toPx(), centerY + 40.dp.toPx())
                    )
                }
            }

            Spacer(modifier = Modifier.height(60.dp))

            // RepoPulse Text
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Repo",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = SplashTextPrimary,
                    letterSpacing = 0.sp
                )
                Text(
                    text = "Pulse",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = SplashAccent,
                    letterSpacing = 0.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Subtitle
            Text(
                text = "A tool that visualizes and analyzes Git repository \n activity and development patterns",
                fontSize = 14.sp,
                color = SplashTextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(80.dp))

            // Initializing workspace text
            Row(
                modifier = Modifier.fillMaxWidth(0.8f),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Initializing workspace",
                    fontSize = 14.sp,
                    color = SplashTextSecondary
                )
                Text(
                    text = "${(progress * 100).toInt()}%",
                    fontSize = 14.sp,
                    color = SplashAccent,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress Bar
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(6.dp),
                color = SplashAccent,
                trackColor = SplashSurface
            )

            Spacer(modifier = Modifier.height(60.dp))

            // System Syncing Button
            Box(
                modifier = Modifier
                    .width(200.dp)
                    .height(48.dp)
                    .background(
                        color = Color.Transparent,
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp)
                    )
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(SplashSurface, SplashBgBottom)
                        ),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Rotating sync icon (simplified)
                    Canvas(modifier = Modifier.size(16.dp)) {
                        drawArc(
                            color = SplashAccent,
                            startAngle = rotationAngle,
                            sweepAngle = 300f,
                            useCenter = false,
                            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "SYSTEM SYNCING",
                        fontSize = 12.sp,
                        color = SplashAccent,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SplashScreenPreview() {
    Git_repo_4Theme {
        SplashScreen()
    }
}
