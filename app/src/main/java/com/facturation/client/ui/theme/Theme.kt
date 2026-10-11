package com.facturation.client.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val NavyBg = Color(0xFF080F22)
val NavyCard = Color(0xFF0F1A33)
val NavyField = Color(0xFF14233F)
val NavyBar = Color(0xFF0B1630)
val BlueHeader = Color(0xFF1D63F0)
val TextMuted = Color(0xFF94A3B8)
val GreenPay = Color(0xFF34D399)
val RedDebt = Color(0xFFF87171)

private val AppColorScheme = darkColorScheme(
    primary = BlueHeader,
    onPrimary = Color.White,
    primaryContainer = BlueHeader,
    onPrimaryContainer = Color.White,
    secondary = Color(0xFF818CF8),
    onSecondary = Color.White,
    secondaryContainer = BlueHeader,
    onSecondaryContainer = Color.White,
    background = NavyBg,
    onBackground = Color.White,
    surface = NavyBg,
    onSurface = Color.White,
    surfaceVariant = NavyCard,
    onSurfaceVariant = TextMuted,
    outline = Color(0xFF2A3A5C)
)

@Composable
fun FacturationTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        typography = Typography,
        content = content
    )
}
