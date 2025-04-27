import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.math.sin

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Gradient Clock") {
        MaterialTheme {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                // Background gradient
                animatedGradientBackground()

                // Time display
                clockCenterText()
            }
        }
    }
}

@Composable
fun clockCenterText() {
    var currentTime by remember { mutableStateOf(LocalTime.now()) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = LocalTime.now()
            kotlinx.coroutines.delay(1000L)
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = currentTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")),
            fontSize = 48.sp,
            color = Color.White
        )
    }
}

@Composable
fun animatedGradientBackground() {
    val infiniteTransition = rememberInfiniteTransition()

    val animatedValue by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 30000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val color1 = remember(animatedValue) { animatedDarkColor(animatedValue, shift = 0f) }
    val color2 = remember(animatedValue) { animatedDarkColor(animatedValue, shift = 1f) }

    Canvas(modifier = Modifier.fillMaxSize()) {
        drawRect(
            brush = Brush.linearGradient(
                colors = listOf(color1, color2),
                start = Offset.Zero,
                end = Offset(size.width, size.height)
            )
        )
    }
}

fun animatedDarkColor(time: Float, shift: Float): Color {
    // Oscillate hues slowly, staying around dark blues and violets
    val hue = (230f + 30f * sin((time + shift) * 2 * Math.PI)).toFloat() % 360
    val saturation = 0.5f + 0.2f * sin((time + shift) * 2 * Math.PI)
    val lightness = 0.2f + 0.05f * sin((time + shift) * 2 * Math.PI)

    return hslToColor(hue, saturation.toFloat(), lightness.toFloat())
}

fun hslToColor(h: Float, s: Float, l: Float): Color {
    val c = (1f - kotlin.math.abs(2 * l - 1f)) * s
    val x = c * (1f - kotlin.math.abs((h / 60f) % 2 - 1f))
    val m = l - c / 2f

    val (r1, g1, b1) = when {
        h < 60f -> Triple(c, x, 0f)
        h < 120f -> Triple(x, c, 0f)
        h < 180f -> Triple(0f, c, x)
        h < 240f -> Triple(0f, x, c)
        h < 300f -> Triple(x, 0f, c)
        else -> Triple(c, 0f, x)
    }

    return Color(
        red = (r1 + m).coerceIn(0f, 1f),
        green = (g1 + m).coerceIn(0f, 1f),
        blue = (b1 + m).coerceIn(0f, 1f),
        alpha = 1f
    )
}

