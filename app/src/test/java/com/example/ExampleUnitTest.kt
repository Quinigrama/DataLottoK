package com.example

import com.example.data.local.Converters
import com.example.data.model.GameConfig
import com.example.logic.LotteryGenerator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun testBonolotoGenerator() {
    val bonoloto = GameConfig.Bonoloto
    val result = LotteryGenerator.generateSimple(bonoloto)

    assertEquals(6, result.primaryNumbers.size)
    assertEquals(6, result.primaryNumbers.toSet().size)
    assertTrue(result.primaryNumbers.all { it in 1..49 })
    assertEquals(result.primaryNumbers.sorted(), result.primaryNumbers)
    assertTrue(result.secondaryNumbers.isEmpty())
  }

  @Test
  fun testLaPrimitivaGenerator() {
    val primitiva = GameConfig.LaPrimitiva
    val result = LotteryGenerator.generateSimple(primitiva)

    assertEquals(6, result.primaryNumbers.size)
    assertEquals(6, result.primaryNumbers.toSet().size)
    assertTrue(result.primaryNumbers.all { it in 1..49 })
    assertEquals(result.primaryNumbers.sorted(), result.primaryNumbers)
    assertEquals("primitiva", primitiva.id)
    assertEquals("La Primitiva", primitiva.name)
    assertEquals("🇪🇸", primitiva.flagEmoji)
    assertTrue(result.secondaryNumbers.isEmpty())
  }

  @Test
  fun testEuromillonesGenerator() {
    val euromillones = GameConfig.Euromillones
    val result = LotteryGenerator.generateSimple(euromillones)

    assertEquals(5, result.primaryNumbers.size)
    assertEquals(5, result.primaryNumbers.toSet().size)
    assertTrue(result.primaryNumbers.all { it in 1..50 })
    assertEquals(result.primaryNumbers.sorted(), result.primaryNumbers)

    assertTrue(euromillones.hasSecondaryMatrix)
    assertEquals(2, result.secondaryNumbers.size)
    assertEquals(2, result.secondaryNumbers.toSet().size)
    assertTrue(result.secondaryNumbers.all { it in 1..12 })
    assertEquals(result.secondaryNumbers.sorted(), result.secondaryNumbers)
    assertEquals("euromillones", euromillones.id)
    assertEquals("Euromillones", euromillones.name)
    assertEquals("🇪🇺", euromillones.flagEmoji)
  }

  @Test
  fun testGameConfigLookup() {
    assertEquals(3, GameConfig.AvailableGames.size)
    assertEquals(GameConfig.Bonoloto, GameConfig.findById("bonoloto"))
    assertEquals(GameConfig.LaPrimitiva, GameConfig.findById("primitiva"))
    assertEquals(GameConfig.Euromillones, GameConfig.findById("euromillones"))
    assertEquals(GameConfig.Bonoloto, GameConfig.findById("unknown_game"))
  }

  @Test
  fun testConverters() {
    val converters = Converters()
    val list = listOf(3, 12, 25, 33, 41, 49)
    val serialized = converters.fromIntList(list)
    val deserialized = converters.toIntList(serialized)

    assertEquals(list, deserialized)
    assertEquals(emptyList<Int>(), converters.toIntList(""))
    assertEquals(emptyList<Int>(), converters.toIntList(null))
  }

  @Test
  fun testDrawDaysFormatting() {
    val bonoloto = GameConfig.Bonoloto
    val primitiva = GameConfig.LaPrimitiva
    val euromillones = GameConfig.Euromillones

    assertEquals("Lunes, Jueves, Sábado", primitiva.formatDrawDays())
    assertEquals("Martes, Viernes", euromillones.formatDrawDays())
    assertTrue(bonoloto.formatDrawDays().contains("Lunes"))
  }

  @Test
  fun testBonolotoPrizeCalculation() {
    val bonoloto = GameConfig.Bonoloto

    val prize6 = com.example.logic.PrizeCalculator.calculatePrize(bonoloto, 6, 0)
    assertTrue(prize6.isWinner)
    assertEquals("6 aciertos", prize6.prizeLabel)

    val prize3 = com.example.logic.PrizeCalculator.calculatePrize(bonoloto, 3, 0)
    assertTrue(prize3.isWinner)
    assertEquals("3 aciertos", prize3.prizeLabel)

    val prize2 = com.example.logic.PrizeCalculator.calculatePrize(bonoloto, 2, 0)
    org.junit.Assert.assertFalse(prize2.isWinner)
    assertTrue(prize2.prizeLabel.contains("Sin premio"))
  }

  @Test
  fun testEuromillonesPrizeCalculation() {
    val euromillones = GameConfig.Euromillones

    // 1ª categoría: 5 + 2⭐
    val tier1 = com.example.logic.PrizeCalculator.calculatePrize(euromillones, 5, 2)
    assertTrue(tier1.isWinner)
    assertEquals(1, tier1.tier?.category)

    // 13ª categoría: 2 + 0⭐
    val tier13 = com.example.logic.PrizeCalculator.calculatePrize(euromillones, 2, 0)
    assertTrue(tier13.isWinner)
    assertEquals(13, tier13.tier?.category)

    // Sin premio: 1 + 0⭐
    val noPrize = com.example.logic.PrizeCalculator.calculatePrize(euromillones, 1, 0)
    org.junit.Assert.assertFalse(noPrize.isWinner)
    assertTrue(noPrize.prizeLabel.contains("Sin premio"))
  }
}


