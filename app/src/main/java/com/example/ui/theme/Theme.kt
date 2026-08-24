package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
val LocalAppLocale = staticCompositionLocalOf { "es" }

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
    primary = DarkIndigo,
    onPrimary = DarkBackground,
    primaryContainer = Color(0xFF312E81),
    onPrimaryContainer = Color(0xFFE0E7FF),
    secondary = DarkViolet,
    onSecondary = Color(0xFF2E1065),
    secondaryContainer = Color(0xFF4C1D95),
    onSecondaryContainer = Color(0xFFEDE9FE),
    tertiary = Color(0xFF60A5FA),
    onTertiary = Color(0xFF1E3A8A),
    error = BrandDanger,
    onError = Color.White,
    background = DarkBackground,
    onBackground = Color(0xFFF8FAFC),
    surface = DarkSurface,
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = DarkOutline
)

@Composable
fun DataLottoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Set false to maintain consistent DataLotto brand colors
    locale: String = "es",
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

    val extraColors = if (darkTheme) {
        ExtraColors(
            success = BrandSuccess,
            warning = BrandWarning,
            gradientStart = DarkGradientStart,
            gradientEnd = DarkGradientEnd
        )
    } else {
        ExtraColors(
            success = BrandSuccess,
            warning = BrandWarning,
            gradientStart = BrandGradientStart,
            gradientEnd = BrandGradientEnd
        )
    }

    CompositionLocalProvider(LocalExtraColors provides extraColors) {
        CompositionLocalProvider(LocalAppLocale provides locale) {
            MaterialTheme(
                colorScheme = colorScheme,
                typography = Typography,
                content = content
            )
        }
    }
}


