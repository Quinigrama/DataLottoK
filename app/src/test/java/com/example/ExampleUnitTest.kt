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
    val combination = LotteryGenerator.generateSimple(bonoloto)

    assertEquals(6, combination.size)
    assertEquals(6, combination.toSet().size)
    assertTrue(combination.all { it in 1..49 })
    assertEquals(combination.sorted(), combination)
  }

  @Test
  fun testLaPrimitivaGenerator() {
    val primitiva = GameConfig.LaPrimitiva
    val combination = LotteryGenerator.generateSimple(primitiva)

    assertEquals(6, combination.size)
    assertEquals(6, combination.toSet().size)
    assertTrue(combination.all { it in 1..49 })
    assertEquals(combination.sorted(), combination)
    assertEquals("primitiva", primitiva.id)
    assertEquals("La Primitiva", primitiva.name)
    assertEquals("🇪🇸", primitiva.flagEmoji)
  }

  @Test
  fun testGameConfigLookup() {
    assertEquals(2, GameConfig.AvailableGames.size)
    assertEquals(GameConfig.Bonoloto, GameConfig.findById("bonoloto"))
    assertEquals(GameConfig.LaPrimitiva, GameConfig.findById("primitiva"))
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
}

