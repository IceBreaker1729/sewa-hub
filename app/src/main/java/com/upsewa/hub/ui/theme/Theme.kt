package com.upsewa.hub.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Brand        = Color(0xFF0B3D2E)
private val BrandLight   = Color(0xFF3A8A6D)
private val Gold         = Color(0xFFC79A3A)
private val GoldDeep     = Color(0xFFB13A2A)
private val Surface      = Color(0xFFFFFFFF)
private val Surface2     = Color(0xFFF0EEE7)
private val Background   = Color(0xFFF6F5F1)
private val Ink          = Color(0xFF1A2421)
private val DSurface     = Color(0xFF161D19)
private val DSurface2    = Color(0xFF1C2520)
private val DBackground  = Color(0xFF0E1411)
private val DInk         = Color(0xFFECEDE9)

private val LightColors = lightColorScheme(
    primary = Brand, onPrimary = Color.White,
    primaryContainer = Surface2, onPrimaryContainer = Brand,
    secondary = Gold, onSecondary = Color(0xFF1A1306),
    tertiary = GoldDeep, onTertiary = Color.White,
    background = Background, onBackground = Ink,
    surface = Surface, onSurface = Ink,
    surfaceVariant = Surface2, onSurfaceVariant = Ink,
)

private val DarkColors = darkColorScheme(
    primary = BrandLight, onPrimary = Color.White,
    primaryContainer = DSurface2, onPrimaryContainer = Gold,
    secondary = Gold, onSecondary = Color(0xFF1A1306),
    tertiary = Color(0xFFD65A48), onTertiary = Color.White,
    background = DBackground, onBackground = DInk,
    surface = DSurface, onSurface = DInk,
    surfaceVariant = DSurface2, onSurfaceVariant = DInk,
)

@Composable
fun UPSewaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = MaterialTheme.typography,
        content = content
    )
}
