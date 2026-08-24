package com.example.logic

import com.example.data.model.GameConfig
import java.security.SecureRandom

/**
 * Lógica de generación de combinaciones de lotería.
 * En esta fase implementa la estrategia "Simple" (aleatoria sin repetición).
 */
object LotteryGenerator {
    private val random = SecureRandom()

    /**
     * Genera una combinación aleatoria simple para el juego especificado.
     * Retorna una lista ordenada de números únicos.
     */
    fun generateSimple(gameConfig: GameConfig): List<Int> {
        val totalNumbers = (gameConfig.minNumber..gameConfig.maxNumber).toList()
        val picked = totalNumbers.shuffled(random).take(gameConfig.pickCount)
        return picked.sorted()
    }
}
