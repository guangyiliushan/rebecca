package top.guangyiliushan.rebecca.core.language

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/** 词汇等级匹配（Adaptive 行底部信息的"当前匹配等级"来源）。 */
class VocabularyLevelTest {
    private val levels = listOf(
        VocabularyLevel("A1", 500),
        VocabularyLevel("A2", 1500),
        VocabularyLevel("B1", 3000),
    )

    @Test
    fun matchesFirstLevelWhoseTargetCoversMastered() {
        assertEquals("A1", matchVocabularyLevel(0, levels)?.code)
        assertEquals("A1", matchVocabularyLevel(500, levels)?.code)
        assertEquals("A2", matchVocabularyLevel(501, levels)?.code)
        assertEquals("B1", matchVocabularyLevel(1501, levels)?.code)
    }

    @Test
    fun masteredBeyondTopLevel_clampsToTopLevel() {
        assertEquals("B1", matchVocabularyLevel(99_999, levels)?.code)
    }

    @Test
    fun emptyLevels_returnsNull_notFabricatedLevel() {
        assertNull(matchVocabularyLevel(10, emptyList()))
    }

    @Test
    fun unsortedInput_isOrderedByTargetSize() {
        assertEquals("A1", matchVocabularyLevel(10, levels.reversed())?.code)
        assertEquals("B1", matchVocabularyLevel(50_000, levels.reversed())?.code)
    }
}
