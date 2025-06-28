package com.example.beerpal.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val BeerColorScheme = darkColorScheme(
    primary = Color(0xFFFFC107),     // LagerGold
    secondary = Color(0xFFFF9800),   // Amber
    background = Color(0xFF120A08),  // Deepest beer cave brown
    surface = Color(0xFFFFFFFF),     // Foam white
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color(0xFFFFF8E1), // Pale foam
    onSurface = Color.Black,
    error = Color(0xFFD32F2F),
    onError = Color.White
)



@Composable
fun BeerPalTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = BeerColorScheme,
        typography = Typography(),
        shapes = Shapes(),
        content = content
    )
}
