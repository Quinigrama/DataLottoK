package com.example.logic

import com.example.data.model.GameConfig
import java.security.SecureRandom

data class MultipleOptions(
    val numberChoices: List<Int>,
    val defaultNumber: Int,
    val secondaryChoices: List<Int>,
    val defaultSecondary: Int
)

data class MultipleTicketResult(
    val numbers: List<Int>,
    val secondaryNumbers: List<Int>,
    val totalBets: Int,
    val totalCost: Double
)

object MultipleTicketCalculator {

    private val secureRandom = SecureRandom()

    fun combinationsCount(n: Int, k: Int): Long {
        if (k < 0 || k > n) return 0L
        if (k == 0 || k == n) return 1L
        val minK = minOf(k, n - k)
        var result = 1L
        for (i in 1..minK) {
            result = result * (n - i + 1) / i
        }
        return result
    }

    fun getOptions(game: GameConfig): MultipleOptions {
        return if (game.hasSecondaryMatrix) {
            MultipleOptions(
                numberChoices = listOf(5, 6, 7, 8, 9, 10),
                defaultNumber = 6,
                secondaryChoices = listOf(2, 3, 4, 5),
                defaultSecondary = 2
            )
        } else {
            MultipleOptions(
                numberChoices = listOf(5, 7, 8, 9, 10, 11),
                defaultNumber = 7,
                secondaryChoices = emptyList(),
                defaultSecondary = 0
            )
        }
    }

    @Throws(IllegalStateException::class)
    fun calculateTotalBets(game: GameConfig, numberCount: Int, secondaryCount: Int = 0): Int {
        if (!game.hasSecondaryMatrix) {
            return if (numberCount == 5) {
                // Regla especial oficial SELAE para 5 números en Bonoloto / Primitiva:
                // Se fija la combinación de 5 y se combina con cada uno de los 44 números restantes.
                44
            } else {
                combinationsCount(numberCount, game.pickCount).toInt()
            }
        } else {
            val secCount = if (secondaryCount > 0) secondaryCount else game.secondaryPickCount
            val primaryComb = combinationsCount(numberCount, game.pickCount)
            val secondaryComb = combinationsCount(secCount, game.secondaryPickCount)
            val total = primaryComb * secondaryComb

            if (total > 756) {
                throw IllegalStateException("El número de apuestas ($total) supera el límite oficial máximo permitido para Euromillones (756 apuestas).")
            }
            return total.toInt()
        }
    }

    @Throws(IllegalStateException::class)
    fun calculateTotalCost(game: GameConfig, numberCount: Int, secondaryCount: Int = 0): Double {
        val totalBets = calculateTotalBets(game, numberCount, secondaryCount)
        return totalBets * game.costPerBet
    }

    @Throws(IllegalStateException::class)
    fun generate(game: GameConfig, numberCount: Int, secondaryCount: Int = 0): MultipleTicketResult {
        val bets = calculateTotalBets(game, numberCount, secondaryCount)
        val cost = calculateTotalCost(game, numberCount, secondaryCount)

        // Generar superconjunto de números principales
        val primaryAvailable = (game.minNumber..game.maxNumber).toMutableList()
        val pickedPrimary = mutableListOf<Int>()
        repeat(numberCount) {
            val index = secureRandom.nextInt(primaryAvailable.size)
            pickedPrimary.add(primaryAvailable.removeAt(index))
        }
        pickedPrimary.sort()

        // Generar superconjunto de matriz secundaria (si aplica)
        val pickedSecondary = mutableListOf<Int>()
        if (game.hasSecondaryMatrix && game.secondaryMinNumber != null && game.secondaryMaxNumber != null) {
            val actualSecCount = if (secondaryCount > 0) secondaryCount else game.secondaryPickCount
            val secondaryAvailable = (game.secondaryMinNumber..game.secondaryMaxNumber).toMutableList()
            repeat(actualSecCount) {
                val index = secureRandom.nextInt(secondaryAvailable.size)
                pickedSecondary.add(secondaryAvailable.removeAt(index))
            }
            pickedSecondary.sort()
        }

        return MultipleTicketResult(
            numbers = pickedPrimary,
            secondaryNumbers = pickedSecondary,
            totalBets = bets,
            totalCost = cost
        )
    }
}
