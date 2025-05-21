package com.marcpicone.file_template_app.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val DarkPrimary = Color(0xFF242424)
private val DarkSecondary = Color(0xFF414141)
private val DarkBackground = Color(0xFF121212)
private val DarkSurface = Color(0xFF1E1E1E)
private val DarkOnPrimary = Color(0xFFFFFFFF)
private val DarkOnSecondary = Color(0xFFFFFFFF)
private val DarkOnBackground = Color(0xFFFFFFFF)
private val DarkOnSurface = Color(0xFFFFFFFF)

private val DarkColors = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface
)

private val LightPrimary = Color(0xFFF5F5F5)
private val LightSecondary = Color(0xFFE0E0E0)
private val LightBackground = Color(0xFFFFFFFF)
private val LightSurface = Color(0xFFFAFAFA)
private val LightOnPrimary = Color(0xFF000000)
private val LightOnSecondary = Color(0xFF000000)
private val LightOnBackground = Color(0xFF000000)
private val LightOnSurface = Color(0xFF000000)

private val LightColors = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    secondary = LightSecondary,
    onSecondary = LightOnSecondary,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface
)

@Composable
fun FileTemplateTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (useDarkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colors,
        shapes = Shapes(
            small = RoundedCornerShape(4.dp),
            medium = RoundedCornerShape(8.dp),
            large = RoundedCornerShape(12.dp)
        ),
        content = content
    )
}
