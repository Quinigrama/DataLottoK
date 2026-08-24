package com.example.logic

import com.example.data.model.GameConfig
import kotlin.math.abs
import kotlin.math.floor
import kotlin.random.Random

data class SimulatedDraw(
    val numbers: List<Int>,
    val secondaryNumbers: List<Int> = emptyList()
)

enum class NumberClassification {
    HOT,
    COLD,
    NEUTRAL
}

data class FrequencyEntry(
    val number: Int,
    val count: Int,
    val percentage: Double,
    val classification: NumberClassification,
    val isStar: Boolean = false
)

object StatisticsEngine {

    /**
     * Genera [count] sorteos simulados de manera aleatoria e independiente
     */
    fun generateSimulatedDraws(game: GameConfig, count: Int = 200): List<SimulatedDraw> {
        val random = Random.Default
        val draws = mutableListOf<SimulatedDraw>()

        for (i in 0 until count) {
            val mainNumbers = (game.minNumber..game.maxNumber)
                .shuffled(random)
                .take(game.pickCount)
                .sorted()

            val secondaryNumbers = if (game.hasSecondaryMatrix &&
                game.secondaryMinNumber != null &&
                game.secondaryMaxNumber != null
            ) {
                (game.secondaryMinNumber..game.secondaryMaxNumber)
                    .shuffled(random)
                    .take(game.secondaryPickCount)
                    .sorted()
            } else {
                emptyList()
            }

            draws.add(SimulatedDraw(numbers = mainNumbers, secondaryNumbers = secondaryNumbers))
        }

        return draws
    }

    /**
     * Calcula frecuencias y clasifica en Caliente (percentil 70+), Frío (percentil 30-) o Neutro
     */
    fun computeFrequencies(
        draws: List<SimulatedDraw>,
        game: GameConfig,
        isSecondary: Boolean = false
    ): List<FrequencyEntry> {
        if (draws.isEmpty()) return emptyList()

        val totalDraws = draws.size
        val range = if (isSecondary) {
            (game.secondaryMinNumber ?: 1)..(game.secondaryMaxNumber ?: 12)
        } else {
            game.minNumber..game.maxNumber
        }

        val appearances = mutableMapOf<Int, Int>()
        for (n in range) {
            appearances[n] = 0
        }

        for (draw in draws) {
            val list = if (isSecondary) draw.secondaryNumbers else draw.numbers
            for (num in list) {
                appearances[num] = (appearances[num] ?: 0) + 1
            }
        }

        // Ordena todas las frecuencias de menor a mayor
        val sortedCounts = appearances.values.sorted()
        val n = sortedCounts.size
        val hotThresholdIndex = floor(n * 0.7).toInt().coerceIn(0, n - 1)
        val coldThresholdIndex = floor(n * 0.3).toInt().coerceIn(0, n - 1)

        val hotThreshold = sortedCounts[hotThresholdIndex]
        val coldThreshold = sortedCounts[coldThresholdIndex]

        return range.map { number ->
            val count = appearances[number] ?: 0
            val percentage = (count.toDouble() / totalDraws.toDouble()) * 100.0
            val classification = when {
                count >= hotThreshold -> NumberClassification.HOT
                count <= coldThreshold -> NumberClassification.COLD
                else -> NumberClassification.NEUTRAL
            }
            FrequencyEntry(
                number = number,
                count = count,
                percentage = percentage,
                classification = classification,
                isStar = isSecondary
            )
        }.sortedWith(compareByDescending<FrequencyEntry> { it.count }.thenBy { it.number })
    }

    /**
     * Calcula la calidad de la combinación (0-100) según balance combinatorio legítimo
     */
    fun calculateQualityScore(
        game: GameConfig,
        selectedNumbers: Set<Int>,
        selectedSecondary: Set<Int> = emptySet()
    ): Pair<Int, String> {
        if (selectedNumbers.isEmpty()) {
            return Pair(50, "Selecciona números para evaluar el balance estadístico.")
        }

        var score = 50
        val maxNumbers = game.pickCount
        val numberRange = game.maxNumber

        // 1. Balance par/impar
        val idealEvens = maxNumbers / 2
        val evens = selectedNumbers.count { it % 2 == 0 }
        if (evens == idealEvens || evens == idealEvens + 1) {
            score += 15
        } else if (abs(evens - idealEvens) <= 1) {
            score += 10
        } else {
            score -= 10
        }

        // 2. Balance bajo/alto
        val midPoint = numberRange / 2
        val lows = selectedNumbers.count { it <= midPoint }
        val idealLows = maxNumbers / 2
        if (lows == idealLows || lows == idealLows + 1) {
            score += 15
        } else if (abs(lows - idealLows) <= 1) {
            score += 10
        } else {
            score -= 10
        }

        // 3. (Omitido intencionadamente — ver indicaciones)

        // 4. Suma total
        val avgNum = (1.0 + numberRange.toDouble()) / 2.0
        val idealSum = avgNum * maxNumbers.toDouble()
        val sumRange = idealSum * 0.2
        val selectedSum = selectedNumbers.sum().toDouble()

        if (selectedSum in (idealSum - sumRange)..(idealSum + sumRange)) {
            score += 10
        } else if (selectedSum < idealSum - (sumRange * 2.0) || selectedSum > idealSum + (sumRange * 2.0)) {
            score -= 15
        }

        // 5. Estrellas (solo si el juego tiene matriz secundaria y la selección de estrellas está completa)
        if (game.hasSecondaryMatrix && selectedSecondary.size == game.secondaryPickCount) {
            val maxStars = game.secondaryPickCount
            val starRange = (game.secondaryMaxNumber ?: 12)

            if (maxStars == 2) {
                val starEvens = selectedSecondary.count { it % 2 == 0 }
                if (starEvens == 1) {
                    score += 10
                } else {
                    score += 5
                }

                val starLows = selectedSecondary.count { it <= starRange / 2 }
                if (starLows == 1) {
                    score += 5
                }

                val avgStar = (1.0 + starRange.toDouble()) / 2.0
                val idealStarSum = avgStar * maxStars.toDouble()
                val starSum = selectedSecondary.sum().toDouble()

                if (abs(starSum - idealStarSum) <= (starRange.toDouble() * 0.5)) {
                    score += 5
                }
            }
        }

        val finalScore = score.coerceIn(0, 100)

        val valuation = when {
            finalScore >= 80 -> "Excelente combinación. Sigue patrones estadísticos muy probables."
            finalScore >= 60 -> "Buena combinación. Tiene un balance sólido de factores."
            finalScore >= 40 -> "Combinación aceptable, pero podrías mejorar el balance par/impar o la suma."
            else -> "Combinación poco probable estadísticamente. Considera revisar el balance de números."
        }

        return Pair(finalScore, valuation)
    }
}
