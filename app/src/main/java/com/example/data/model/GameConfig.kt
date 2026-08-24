package com.example.data.model

import androidx.compose.ui.graphics.Color

/**
 * Modelo de datos genérico para juegos de lotería.
 * Preparado para soportar matrices secundarias (estrellas, números adicionales, reintegros).
 */
data class GameConfig(
    val id: String,
    val name: String,
    val flagEmoji: String,
    val minNumber: Int,
    val maxNumber: Int,
    val pickCount: Int,
    val currency: String = "€",
    val primaryColor: Color = Color(0xFF059669),
    val darkColor: Color = Color(0xFF047857),
    val glowColor: Color = Color(0xFF34D399),
    val containerColor: Color = Color(0xFFD1FAE5),
    val onContainerColor: Color = Color(0xFF065F46),
    // Soporte para matriz secundaria futura
    val hasSecondaryMatrix: Boolean = false,
    val secondaryMinNumber: Int? = null,
    val secondaryMaxNumber: Int? = null,
    val secondaryPickCount: Int = 0,
    val secondaryName: String? = null
) {
    companion object {
        val Bonoloto = GameConfig(
            id = "bonoloto",
            name = "Bonoloto",
            flagEmoji = "🇪🇸",
            minNumber = 1,
            maxNumber = 49,
            pickCount = 6,
            currency = "€",
            primaryColor = Color(0xFF059669),     // Verde Esmeralda
            darkColor = Color(0xFF065F46),
            glowColor = Color(0xFF34D399),
            containerColor = Color(0xFFD1FAE5),
            onContainerColor = Color(0xFF065F46),
            hasSecondaryMatrix = false
        )

        val LaPrimitiva = GameConfig(
            id = "primitiva",
            name = "La Primitiva",
            flagEmoji = "🇪🇸",
            minNumber = 1,
            maxNumber = 49,
            pickCount = 6,
            currency = "€",
            primaryColor = Color(0xFFD97706),     // Ámbar / Dorado
            darkColor = Color(0xFF92400E),
            glowColor = Color(0xFFFBBF24),
            containerColor = Color(0xFFFEF3C7),
            onContainerColor = Color(0xFF78350F),
            hasSecondaryMatrix = false
        )

        val AvailableGames = listOf(Bonoloto, LaPrimitiva)

        fun findById(id: String): GameConfig {
            return AvailableGames.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: Bonoloto
        }
    }
}

