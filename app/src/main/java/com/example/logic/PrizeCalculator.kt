package com.example.logic

import com.example.data.model.GameConfig

data class PrizeTier(
    val category: Int,
    val name: String,
    val hits: Int,
    val starHits: Int = 0,
    val basePrize: Double = 0.0
)

data class PrizeResult(
    val isWinner: Boolean,
    val hits: Int,
    val starHits: Int = 0,
    val tier: PrizeTier? = null,
    val prizeLabel: String
)

object PrizeCalculator {

    /**
     * Tabla oficial completa de las 13 categorías de Euromillones
     */
    val EUROMILLONES_TIERS = listOf(
        PrizeTier(category = 1, name = "1ª Categoría (5 + 2⭐)", hits = 5, starHits = 2, basePrize = 17000000.0),
        PrizeTier(category = 2, name = "2ª Categoría (5 + 1⭐)", hits = 5, starHits = 1, basePrize = 200000.0),
        PrizeTier(category = 3, name = "3ª Categoría (5 + 0⭐)", hits = 5, starHits = 0, basePrize = 20000.0),
        PrizeTier(category = 4, name = "4ª Categoría (4 + 2⭐)", hits = 4, starHits = 2, basePrize = 1500.0),
        PrizeTier(category = 5, name = "5ª Categoría (4 + 1⭐)", hits = 4, starHits = 1, basePrize = 120.0),
        PrizeTier(category = 6, name = "6ª Categoría (3 + 2⭐)", hits = 3, starHits = 2, basePrize = 60.0),
        PrizeTier(category = 7, name = "7ª Categoría (4 + 0⭐)", hits = 4, starHits = 0, basePrize = 40.0),
        PrizeTier(category = 8, name = "8ª Categoría (2 + 2⭐)", hits = 2, starHits = 2, basePrize = 15.0),
        PrizeTier(category = 9, name = "9ª Categoría (3 + 1⭐)", hits = 3, starHits = 1, basePrize = 12.0),
        PrizeTier(category = 10, name = "10ª Categoría (3 + 0⭐)", hits = 3, starHits = 0, basePrize = 10.0),
        PrizeTier(category = 11, name = "11ª Categoría (1 + 2⭐)", hits = 1, starHits = 2, basePrize = 9.0),
        PrizeTier(category = 12, name = "12ª Categoría (2 + 1⭐)", hits = 2, starHits = 1, basePrize = 7.0),
        PrizeTier(category = 13, name = "13ª Categoría (2 + 0⭐)", hits = 2, starHits = 0, basePrize = 4.0)
    )

    /**
     * Tabla oficial completa de las 6 categorías de EuroDreams
     */
    val EURODREAMS_TIERS = listOf(
        PrizeTier(category = 1, name = "1ª Categoría (6 + 1🌙)", hits = 6, starHits = 1, basePrize = 20000.0), // 20.000€/mes durante 30 años
        PrizeTier(category = 2, name = "2ª Categoría (6 + 0🌙)", hits = 6, starHits = 0, basePrize = 2000.0), // 2.000€/mes durante 5 años
        PrizeTier(category = 3, name = "3ª Categoría (5 aciertos)", hits = 5, starHits = 0, basePrize = 120.0),
        PrizeTier(category = 4, name = "4ª Categoría (4 aciertos)", hits = 4, starHits = 0, basePrize = 40.0),
        PrizeTier(category = 5, name = "5ª Categoría (3 aciertos)", hits = 3, starHits = 0, basePrize = 5.0),
        PrizeTier(category = 6, name = "6ª Categoría (2 aciertos)", hits = 2, starHits = 0, basePrize = 2.50)
    )

    /**
     * Tabla oficial completa de las 8 categorías + reintegro de El Gordo
     */
    val GORDO_TIERS = listOf(
        PrizeTier(category = 1, name = "1ª Categoría (5 + 1🔑)", hits = 5, starHits = 1, basePrize = 5000000.0),
        PrizeTier(category = 2, name = "2ª Categoría (5 + 0🔑)", hits = 5, starHits = 0, basePrize = 150000.0),
        PrizeTier(category = 3, name = "3ª Categoría (4 + 1🔑)", hits = 4, starHits = 1, basePrize = 8000.0),
        PrizeTier(category = 4, name = "4ª Categoría (4 + 0🔑)", hits = 4, starHits = 0, basePrize = 200.0),
        PrizeTier(category = 5, name = "5ª Categoría (3 + 1🔑)", hits = 3, starHits = 1, basePrize = 50.0),
        PrizeTier(category = 6, name = "6ª Categoría (3 + 0🔑)", hits = 3, starHits = 0, basePrize = 15.0),
        PrizeTier(category = 7, name = "7ª Categoría (2 + 1🔑)", hits = 2, starHits = 1, basePrize = 6.0),
        PrizeTier(category = 8, name = "8ª Categoría (2 + 0🔑)", hits = 2, starHits = 0, basePrize = 3.0),
        PrizeTier(category = 9, name = "Reintegro (0-1 + 1🔑)", hits = 0, starHits = 1, basePrize = 1.50)
    )

    /**
     * Calcula la categoría de premio correspondiente a los aciertos obtenidos
     */
    fun calculatePrize(game: GameConfig, hits: Int, starHits: Int = 0): PrizeResult {
        return when (game.id) {
            "euromillones" -> {
                val tier = EUROMILLONES_TIERS.firstOrNull { it.hits == hits && it.starHits == starHits }
                if (tier != null) {
                    PrizeResult(
                        isWinner = true,
                        hits = hits,
                        starHits = starHits,
                        tier = tier,
                        prizeLabel = tier.name
                    )
                } else {
                    PrizeResult(
                        isWinner = false,
                        hits = hits,
                        starHits = starHits,
                        tier = null,
                        prizeLabel = "Sin premio ($hits + $starHits⭐)"
                    )
                }
            }
            "eurodreams" -> {
                val tier = when {
                    hits == 6 && starHits == 1 -> EURODREAMS_TIERS[0]
                    hits == 6 && starHits == 0 -> EURODREAMS_TIERS[1]
                    hits == 5 -> EURODREAMS_TIERS[2]
                    hits == 4 -> EURODREAMS_TIERS[3]
                    hits == 3 -> EURODREAMS_TIERS[4]
                    hits == 2 -> EURODREAMS_TIERS[5]
                    else -> null
                }
                if (tier != null) {
                    PrizeResult(
                        isWinner = true,
                        hits = hits,
                        starHits = starHits,
                        tier = tier,
                        prizeLabel = tier.name
                    )
                } else {
                    PrizeResult(
                        isWinner = false,
                        hits = hits,
                        starHits = starHits,
                        tier = null,
                        prizeLabel = "Sin premio ($hits + $starHits🌙)"
                    )
                }
            }
            "gordo" -> {
                val tier = when {
                    hits == 5 && starHits == 1 -> GORDO_TIERS[0]
                    hits == 5 && starHits == 0 -> GORDO_TIERS[1]
                    hits == 4 && starHits == 1 -> GORDO_TIERS[2]
                    hits == 4 && starHits == 0 -> GORDO_TIERS[3]
                    hits == 3 && starHits == 1 -> GORDO_TIERS[4]
                    hits == 3 && starHits == 0 -> GORDO_TIERS[5]
                    hits == 2 && starHits == 1 -> GORDO_TIERS[6]
                    hits == 2 && starHits == 0 -> GORDO_TIERS[7]
                    (hits == 0 || hits == 1) && starHits == 1 -> GORDO_TIERS[8]
                    else -> null
                }
                if (tier != null) {
                    PrizeResult(
                        isWinner = true,
                        hits = hits,
                        starHits = starHits,
                        tier = tier,
                        prizeLabel = tier.name
                    )
                } else {
                    PrizeResult(
                        isWinner = false,
                        hits = hits,
                        starHits = starHits,
                        tier = null,
                        prizeLabel = "Sin premio ($hits + $starHits🔑)"
                    )
                }
            }
            else -> {
                // Bonoloto / La Primitiva: premio a partir de 3 aciertos (3, 4, 5, 6)
                val isWinner = hits >= 3
                if (isWinner) {
                    val tier = PrizeTier(
                        category = (6 - hits) + 1,
                        name = "$hits aciertos",
                        hits = hits,
                        starHits = 0
                    )
                    PrizeResult(
                        isWinner = true,
                        hits = hits,
                        starHits = 0,
                        tier = tier,
                        prizeLabel = "$hits aciertos"
                    )
                } else {
                    PrizeResult(
                        isWinner = false,
                        hits = hits,
                        starHits = 0,
                        tier = null,
                        prizeLabel = "Sin premio ($hits aciertos)"
                    )
                }
            }
        }
    }
}
