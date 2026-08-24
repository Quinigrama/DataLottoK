package com.example.logic

import com.example.data.model.GameConfig
import java.security.SecureRandom

/**
 * Resultado de una combinación de lotería generada.
 */
data class CombinationResult(
    val primaryNumbers: List<Int>,
    val secondaryNumbers: List<Int> = emptyList()
)

/**
 * Lógica de generación de combinaciones de lotería.
 * En esta fase implementa la estrategia "Simple" (aleatoria sin repetición).
 */
object LotteryGenerator {
    private val random = SecureRandom()

    /**
     * Genera una combinación aleatoria simple para el juego especificado.
     * Retorna una lista ordenada de números únicos principales y secundarios (si aplica).
     */
    fun generateSimple(gameConfig: GameConfig): CombinationResult {
        val totalNumbers = (gameConfig.minNumber..gameConfig.maxNumber).toList()
        val picked = totalNumbers.shuffled(random).take(gameConfig.pickCount).sorted()

        val secondaryPicked = if (gameConfig.hasSecondaryMatrix &&
            gameConfig.secondaryMinNumber != null &&
            gameConfig.secondaryMaxNumber != null &&
            gameConfig.secondaryPickCount > 0
        ) {
            val secondaryTotal = (gameConfig.secondaryMinNumber..gameConfig.secondaryMaxNumber).toList()
            secondaryTotal.shuffled(random).take(gameConfig.secondaryPickCount).sorted()
        } else {
            emptyList()
        }

        return CombinationResult(
            primaryNumbers = picked,
            secondaryNumbers = secondaryPicked
        )
    }
}

