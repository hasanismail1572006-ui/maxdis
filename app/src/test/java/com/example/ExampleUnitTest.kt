package com.example

import com.example.model.AisleItem
import org.junit.Assert.assertEquals
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testPluToSecondsFormula() {
        // Standar: 12 PLU = 1 menit (60 detik), 1 PLU = 5 detik
        val plu1 = 12
        val duration1 = plu1 * 5L
        assertEquals(60L, duration1)
        assertEquals("01:00", AisleItem.formatSecondsToMMSS(duration1))

        val plu2 = 5
        val duration2 = plu2 * 5L
        assertEquals(25L, duration2)
        assertEquals("00:25", AisleItem.formatSecondsToMMSS(duration2))
    }

    @Test
    fun testSortingAscendingFormula() {
        // Input: "5+10+6" -> parsed and sorted: [5, 6, 10]
        val formula = "5+10+6"
        val sorted = formula.split("+")
            .mapNotNull { it.trim().toIntOrNull() }
            .filter { it > 0 }
            .sorted()

        assertEquals(listOf(5, 6, 10), sorted)
    }

    @Test
    fun testEmptyAndInvalidInputFiltered() {
        val formula = "5++0+10+abc+6+"
        val sorted = formula.split("+")
            .mapNotNull { it.trim().toIntOrNull() }
            .filter { it > 0 }
            .sorted()

        assertEquals(listOf(5, 6, 10), sorted)
    }
}
