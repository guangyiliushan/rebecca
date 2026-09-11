package top.guangyiliushan.rebecca.core.logic

import kotlin.test.Test
import kotlin.test.assertEquals

class SentenceAnalysisTest {
    @Test
    fun disabledAnalyzer_returnsUnavailable() {
        assertEquals(SentenceAnalysis.Unavailable, DisabledSentenceAnalyzer.analyze("any"))
    }
}
