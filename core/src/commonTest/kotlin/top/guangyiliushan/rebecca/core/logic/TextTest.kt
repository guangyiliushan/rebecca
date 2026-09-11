package top.guangyiliushan.rebecca.core.logic

import kotlin.test.Test
import kotlin.test.assertEquals

class TextTest {
    @Test
    fun splitSentences_threeBoundaries() {
        assertEquals(
            listOf("Hello world.", "How are you?", "I am fine!"),
            splitSentences("Hello world. How are you? I am fine!"),
        )
    }

    @Test
    fun splitSentences_trimsAndSkipsEmpty() {
        assertEquals(listOf("One."), splitSentences("  One.  "))
    }

    @Test
    fun extractWords_uniqueLowercase() {
        assertEquals(listOf("the", "cat"), extractWords("The cat, the CAT."))
    }
}
