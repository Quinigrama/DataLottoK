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
        PrizeTier(category = 1, name = "1ª Categoría (5 + 2⭐)", hits = 5, starHits = 2, basePrize = 17_000_000.0),
        PrizeTier(category = 2, name = "2ª Categoría (5 + 1⭐)", hits = 5, starHits = 1, basePrize = 200_738.0),
        PrizeTier(category = 3, name = "3ª Categoría (5 + 0⭐)", hits = 5, starHits = 0, basePrize = 20_851.0),
        PrizeTier(category = 4, name = "4ª Categoría (4 + 2⭐)", hits = 4, starHits = 2, basePrize = 1_299.0),
        PrizeTier(category = 5, name = "5ª Categoría (4 + 1⭐)", hits = 4, starHits = 1, basePrize = 120.0),
        PrizeTier(category = 6, name = "6ª Categoría (3 + 2⭐)", hits = 3, starHits = 2, basePrize = 57.0),
        PrizeTier(category = 7, name = "7ª Categoría (4 + 0⭐)", hits = 4, starHits = 0, basePrize = 39.0),
        PrizeTier(category = 8, name = "8ª Categoría (2 + 2⭐)", hits = 2, starHits = 2, basePrize = 14.0),
        PrizeTier(category = 9, name = "9ª Categoría (3 + 1⭐)", hits = 3, starHits = 1, basePrize = 11.0),
        PrizeTier(category = 10, name = "10ª Categoría (3 + 0⭐)", hits = 3, starHits = 0, basePrize = 9.0),
        PrizeTier(category = 11, name = "11ª Categoría (1 + 2⭐)", hits = 1, starHits = 2, basePrize = 7.0),
        PrizeTier(category = 12, name = "12ª Categoría (2 + 1⭐)", hits = 2, starHits = 1, basePrize = 6.0),
        PrizeTier(category = 13, name = "13ª Categoría (2 + 0⭐)", hits = 2, starHits = 0, basePrize = 4.0)
    )

    /**
     * Calcula la categoría de premio correspondiente a los aciertos obtenidos
     */
    fun calculatePrize(game: GameConfig, hits: Int, starHits: Int = 0): PrizeResult {
        return if (game.hasSecondaryMatrix) {
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
        } else {
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
