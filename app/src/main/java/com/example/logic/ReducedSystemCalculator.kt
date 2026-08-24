package com.example.logic

import com.example.data.model.GameConfig

data class ReducedSystem(
    val id: String,
    val baseNumbersCount: Int,
    val guarantee: Int,
    val combinationsCount: Int,
    val description: String
)

object ReducedSystemCalculator {

    private val BONOLOTO_PRIMITIVA_SYSTEMS = listOf(
        ReducedSystem(
            id = "reduced-8-5-5",
            baseNumbersCount = 8,
            guarantee = 5,
            combinationsCount = 12,
            description = "Garantiza al menos un premio de 5 aciertos si entre tus 8 números elegidos están los 6 ganadores."
        ),
        ReducedSystem(
            id = "reduced-10-5-5",
            baseNumbersCount = 10,
            guarantee = 5,
            combinationsCount = 56,
            description = "Garantiza al menos un premio de 5 aciertos si entre tus 10 números elegidos están los 6 ganadores."
        ),
        ReducedSystem(
            id = "reduced-12-5-5",
            baseNumbersCount = 12,
            guarantee = 5,
            combinationsCount = 172,
            description = "Garantiza al menos un premio de 5 aciertos si entre tus 12 números elegidos están los 6 ganadores."
        ),
        ReducedSystem(
            id = "reduced-10-4-4",
            baseNumbersCount = 10,
            guarantee = 4,
            combinationsCount = 23,
            description = "Garantiza al menos un premio de 4 aciertos si entre tus 10 números elegidos están los 6 ganadores."
        ),
        ReducedSystem(
            id = "reduced-12-4-4",
            baseNumbersCount = 12,
            guarantee = 4,
            combinationsCount = 53,
            description = "Garantiza al menos un premio de 4 aciertos si entre tus 12 números elegidos están los 6 ganadores."
        ),
        ReducedSystem(
            id = "reduced-14-4-4",
            baseNumbersCount = 14,
            guarantee = 4,
            combinationsCount = 107,
            description = "Garantiza al menos un premio de 4 aciertos si entre tus 14 números elegidos están los 6 ganadores."
        )
    )

    private val EUROMILLONES_SYSTEMS = listOf(
        ReducedSystem(
            id = "reduced-8-4-4",
            baseNumbersCount = 8,
            guarantee = 4,
            combinationsCount = 23,
            description = "Garantiza al menos un premio de 4 aciertos si entre tus 8 números elegidos están los 5 ganadores. (2 estrellas fijas)."
        ),
        ReducedSystem(
            id = "reduced-10-4-4",
            baseNumbersCount = 10,
            guarantee = 4,
            combinationsCount = 53,
            description = "Garantiza al menos un premio de 4 aciertos si entre tus 10 números elegidos están los 5 ganadores. (2 estrellas fijas)."
        ),
        ReducedSystem(
            id = "reduced-12-4-4",
            baseNumbersCount = 12,
            guarantee = 4,
            combinationsCount = 132,
            description = "Garantiza al menos un premio de 4 aciertos si entre tus 12 números elegidos están los 5 ganadores. (2 estrellas fijas)."
        ),
        ReducedSystem(
            id = "reduced-10-3-3",
            baseNumbersCount = 10,
            guarantee = 3,
            combinationsCount = 19,
            description = "Garantiza al menos un premio de 3 aciertos si entre tus 10 números elegidos están los 5 ganadores. (2 estrellas fijas)."
        ),
        ReducedSystem(
            id = "reduced-12-3-3",
            baseNumbersCount = 12,
            guarantee = 3,
            combinationsCount = 33,
            description = "Garantiza al menos un premio de 3 aciertos si entre tus 12 números elegidos están los 5 ganadores. (2 estrellas fijas)."
        ),
        ReducedSystem(
            id = "reduced-15-3-3",
            baseNumbersCount = 15,
            guarantee = 3,
            combinationsCount = 62,
            description = "Garantiza al menos un premio de 3 aciertos si entre tus 15 números elegidos están los 5 ganadores. (2 estrellas fijas)."
        )
    )

    fun getSystems(gameId: String): List<ReducedSystem> {
        return when (gameId.lowercase()) {
            "bonoloto", "primitiva" -> BONOLOTO_PRIMITIVA_SYSTEMS
            "euromillones" -> EUROMILLONES_SYSTEMS
            else -> emptyList()
        }
    }

    fun findSystem(gameId: String, systemId: String?): ReducedSystem? {
        if (systemId == null) return null
        return getSystems(gameId).find { it.id == systemId }
    }

    fun totalCost(game: GameConfig, system: ReducedSystem): Double {
        return system.combinationsCount * game.costPerBet
    }

    fun combinations(n: Int, k: Int): List<List<Int>> {
        val result = mutableListOf<List<Int>>()
        fun backtrack(start: Int, current: MutableList<Int>) {
            if (current.size == k) {
                result.add(ArrayList(current))
                return
            }
            for (i in start until n) {
                current.add(i)
                backtrack(i + 1, current)
                current.removeAt(current.size - 1)
            }
        }
        backtrack(0, mutableListOf())
        return result
    }

    fun getGreedyCovering(n: Int, k: Int, t: Int, c: Int): List<List<Int>> {
        val targets = combinations(n, t)
        val candidates = combinations(n, k)

        val targetMasks = targets.map { subset ->
            subset.fold(0) { acc, num -> acc or (1 shl num) }
        }
        val candidateMasks = candidates.map { subset ->
            subset.fold(0) { acc, num -> acc or (1 shl num) }
        }

        val uncoveredTargets = (0 until targets.size).toMutableSet()
        val selectedCandidates = mutableListOf<List<Int>>()
        val chosenCandidateIndices = BooleanArray(candidates.size)

        for (step in 0 until c) {
            if (selectedCandidates.size >= candidates.size) break

            var bestIndex = -1
            var bestCoverCount = -1

            for (i in candidates.indices) {
                if (chosenCandidateIndices[i]) continue

                val candMask = candidateMasks[i]
                var coverCount = 0
                for (targetIdx in uncoveredTargets) {
                    val targetMask = targetMasks[targetIdx]
                    if ((candMask and targetMask) == targetMask) {
                        coverCount++
                    }
                }

                if (coverCount > bestCoverCount) {
                    bestCoverCount = coverCount
                    bestIndex = i
                }
            }

            // Fallback if no candidate covers anything new
            if (bestIndex == -1 || bestCoverCount <= 0) {
                bestIndex = candidates.indices.firstOrNull { !chosenCandidateIndices[it] } ?: -1
            }

            if (bestIndex == -1) break

            chosenCandidateIndices[bestIndex] = true
            selectedCandidates.add(candidates[bestIndex])

            // Remove covered targets
            val chosenMask = candidateMasks[bestIndex]
            val iterator = uncoveredTargets.iterator()
            while (iterator.hasNext()) {
                val targetIdx = iterator.next()
                val targetMask = targetMasks[targetIdx]
                if ((chosenMask and targetMask) == targetMask) {
                    iterator.remove()
                }
            }
        }

        return selectedCandidates
    }

    fun generate(game: GameConfig, system: ReducedSystem, baseNumbers: List<Int>): List<List<Int>> {
        val baseSorted = baseNumbers.sorted()
        require(baseSorted.size == system.baseNumbersCount) {
            "Se requieren exactamente ${system.baseNumbersCount} números base para el sistema ${system.id}"
        }

        val indexCombinations = getGreedyCovering(
            n = system.baseNumbersCount,
            k = game.pickCount,
            t = system.guarantee,
            c = system.combinationsCount
        )

        return indexCombinations.map { combination ->
            combination.map { baseSorted[it] }.sorted()
        }
    }
}
