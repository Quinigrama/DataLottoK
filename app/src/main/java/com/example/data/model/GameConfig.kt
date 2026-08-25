package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.logic.GridLayout

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
    // Layouts de cuadrícula oficial
    val numbersLayout: GridLayout = GridLayout.Standard(columns = 7),
    val secondaryLayout: GridLayout? = null,
    // Soporte para matriz secundaria futura
    val hasSecondaryMatrix: Boolean = false,
    val secondaryMinNumber: Int? = null,
    val secondaryMaxNumber: Int? = null,
    val secondaryPickCount: Int = 0,
    val secondaryName: String? = null,
    val secondaryEmoji: String? = null,
    val secondaryPrimaryColor: Color = Color(0xFFEAB308),
    val secondaryDarkColor: Color = Color(0xFFCA8A04),
    val secondaryGlowColor: Color = Color(0xFFFDE047),
    val secondaryContainerColor: Color = Color(0xFFFEF08A),
    val secondaryOnContainerColor: Color = Color(0xFF854D0E),
    // Días de sorteo (0 = Domingo, 1 = Lunes, 2 = Martes, 3 = Miércoles, 4 = Jueves, 5 = Viernes, 6 = Sábado)
    val drawDays: List<Int> = listOf(0, 1, 2, 3, 4, 5, 6),
    val costPerBet: Double = 1.0
) {
    fun formatDrawDays(): String {
        val dayNames = mapOf(
            0 to "Domingo",
            1 to "Lunes",
            2 to "Martes",
            3 to "Miércoles",
            4 to "Jueves",
            5 to "Viernes",
            6 to "Sábado"
        )
        return drawDays.mapNotNull { dayNames[it] }.joinToString(", ")
    }

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
            numbersLayout = GridLayout.DecadeColumn,
            hasSecondaryMatrix = false,
            drawDays = listOf(1, 2, 3, 4, 5, 6, 0),
            costPerBet = 0.50
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
            numbersLayout = GridLayout.DecadeColumn,
            hasSecondaryMatrix = false,
            drawDays = listOf(1, 4, 6),
            costPerBet = 1.00
        )

        val Euromillones = GameConfig(
            id = "euromillones",
            name = "Euromillones",
            flagEmoji = "🇪🇺",
            minNumber = 1,
            maxNumber = 50,
            pickCount = 5,
            currency = "€",
            primaryColor = Color(0xFF2563EB),     // Azul Real Europeo
            darkColor = Color(0xFF1D4ED8),
            glowColor = Color(0xFF60A5FA),
            containerColor = Color(0xFFDBEAFE),
            onContainerColor = Color(0xFF1E40AF),
            numbersLayout = GridLayout.ColumnChunk(chunkSize = 9),
            secondaryLayout = GridLayout.ColumnChunk(chunkSize = 4),
            hasSecondaryMatrix = true,
            secondaryMinNumber = 1,
            secondaryMaxNumber = 12,
            secondaryPickCount = 2,
            secondaryName = "Estrellas",
            secondaryEmoji = "⭐",
            secondaryPrimaryColor = Color(0xFFEAB308),
            secondaryDarkColor = Color(0xFFCA8A04),
            secondaryGlowColor = Color(0xFFFDE047),
            secondaryContainerColor = Color(0xFFFEF08A),
            secondaryOnContainerColor = Color(0xFF854D0E),
            drawDays = listOf(2, 5),
            costPerBet = 2.50
        )

        val EuroDreams = GameConfig(
            id = "eurodreams",
            name = "EuroDreams",
            flagEmoji = "🇪🇺",
            minNumber = 1,
            maxNumber = 40,
            pickCount = 6,
            currency = "€",
            primaryColor = Color(0xFF6B21A8),     // Púrpura Europeo
            darkColor = Color(0xFF581C87),
            glowColor = Color(0xFFA855F7),
            containerColor = Color(0xFFF3E8FF),
            onContainerColor = Color(0xFF581C87),
            numbersLayout = GridLayout.Standard(columns = 8),
            secondaryLayout = GridLayout.Standard(columns = 5),
            hasSecondaryMatrix = true,
            secondaryMinNumber = 1,
            secondaryMaxNumber = 5,
            secondaryPickCount = 1,
            secondaryName = "Sueño",
            secondaryEmoji = "🌙",
            secondaryPrimaryColor = Color(0xFFEC4899),
            secondaryDarkColor = Color(0xFFBE185D),
            secondaryGlowColor = Color(0xFFF472B6),
            secondaryContainerColor = Color(0xFFFCE7F3),
            secondaryOnContainerColor = Color(0xFF9D174D),
            drawDays = listOf(1, 4),
            costPerBet = 2.50
        )

        val AvailableGames = listOf(Bonoloto, LaPrimitiva, Euromillones, EuroDreams)

        fun findById(id: String): GameConfig {
            return AvailableGames.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: Bonoloto
        }
    }
}

