package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

@Immutable
data class ExtraColors(
    val success: Color = BrandSuccess,
    val warning: Color = BrandWarning,
    val gradientStart: Color = BrandGradientStart,
    val gradientEnd: Color = BrandGradientEnd
)

val LocalExtraColors = staticCompositionLocalOf { ExtraColors() }

private val LightColorScheme = lightColorScheme(
    primary = BrandIndigo,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEEF2FF),
    onPrimaryContainer = BrandIndigoDark,
    secondary = BrandViolet,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF3E8FF),
    onSecondaryContainer = Color(0xFF581C87),
    tertiary = Color(0xFF2563EB),
    onTertiary = Color.White,
    error = BrandDanger,
    onError = Color.White,
    background = SlateBackground,
    onBackground = BrandDark,
    surface = SlateSurface,
    onSurface = BrandDark,
    surfaceVariant = SlateSurfaceVariant,
    onSurfaceVariant = Color(0xFF475569),
    outline = SlateOutline
)

private val DarkColorScheme = darkColorScheme(
    primary = EmeraldDarkTheme,
    onPrimary = Color(0xFF022C22),
    primaryContainer = EmeraldDark,
    onPrimaryContainer = EmeraldLight,
    secondary = Color(0xFFFBBF24),
    onSecondary = Color(0xFF451A03),
    secondaryContainer = Color(0xFF78350F),
    onSecondaryContainer = GoldLight,
    tertiary = Color(0xFF60A5FA),
    onTertiary = Color(0xFF1E3A8A),
    error = BrandDanger,
    onError = Color.White,
    background = SlateDarkBackground,
    onBackground = Color(0xFFF1F5F9),
    surface = SlateDarkSurface,
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = SlateDarkSurfaceVariant,
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = Color(0xFF475569)
)

@Composable
fun DataLottoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Set false to maintain consistent DataLotto brand colors
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

