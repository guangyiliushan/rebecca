package top.guangyiliushan.rebecca.core.logic

import kotlin.test.Test
import kotlin.test.assertEquals

class SpellingTest {
    @Test
    fun editDistance_identical() {
        assertEquals(0, editDistance("supply", "supply"))
    }

    @Test
    fun editDistance_oneLetterOff() {
        assertEquals(1, editDistance("skunk", "skun"))
    }
}
