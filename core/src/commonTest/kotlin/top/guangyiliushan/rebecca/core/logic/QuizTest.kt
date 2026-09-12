package top.guangyiliushan.rebecca.core.logic

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import top.guangyiliushan.rebecca.core.model.QuizMode
import top.guangyiliushan.rebecca.core.model.Sense
import top.guangyiliushan.rebecca.core.model.SenseId

private fun sense(id: String, lemmaId: String, def: String) =
    Sense(SenseId(id), lemmaId, def)

private val POOL = listOf(
    sense("s1", "l-run", "move fast"),
    sense("s2", "l-run", "operate"),
    sense("s3", "l-port", "a harbor"),
    sense("s4", "l-port", "left side of a ship"),
    sense("s5", "l-form", "shape"),
    sense("s6", "l-light", "brightness"),
    sense("s7", "l-book", "printed pages"),
    sense("s8", "l-supply", "make available"),
)

class QuizTest {
    @Test
    fun buildQuestion_excludesSameLemmaDistractors_andShufflesWithSeed() {
        val q = buildQuestion(POOL[0], POOL, rng = Random(42))
        assertNotNull(q)
        assertEquals(4, q.optionLemmaIds.size)
        assertEquals(4, q.optionLemmaIds.toSet().size) // 四选项词形互不相同（含干扰项之间）
        assertEquals(q.optionLemmaIds[q.correctIndex], "l-run")
        assertEquals(1, q.optionLemmaIds.count { it == "l-run" }) // 无同 lemma 干扰项
        assertTrue("l-run" in q.optionLemmaIds)
    }

    @Test
    fun buildQuestion_returnsNullWhenPoolTooSmall() {
        val tiny = listOf(sense("a", "l1", "x"), sense("b", "l2", "y"))
        assertNull(buildQuestion(tiny[0], tiny))
    }

    @Test
    fun isCorrect_onlyTrueAtCorrectIndex() {
        val q = buildQuestion(POOL[0], POOL, rng = Random(7))!!
        (0..3).forEach { i -> assertEquals(i == q.correctIndex, isCorrect(q, i)) }
    }

    @Test
    fun selectCandidates_learnPicksNewOnly() {
        val new = listOf(POOL[6], POOL[7])
        val due = listOf(POOL[0], POOL[1])
        val r = selectCandidates(QuizMode.LEARN, new, due, limit = 10, rng = Random(1))
        assertEquals(new.toSet(), r.toSet())
    }

    @Test
    fun selectCandidates_reviewPicksDueOnly() {
        val new = listOf(POOL[6])
        val due = listOf(POOL[0], POOL[1])
        val r = selectCandidates(QuizMode.REVIEW, new, due, limit = 10, rng = Random(1))
        assertEquals(due.toSet(), r.toSet())
    }

    @Test
    fun selectCandidates_mixedInterleavesOneToOne() {
        val new = (0..5).map { sense("n$it", "l-n$it", "n") }
        val due = (0..5).map { sense("d$it", "l-d$it", "d") }
        val r = selectCandidates(QuizMode.MIXED, new, due, limit = 8, rng = Random(3))
        assertEquals(8, r.size)
        assertEquals(4, r.count { it.id.value.startsWith("n") })
        assertEquals(4, r.count { it.id.value.startsWith("d") })
        // 交错：相邻两项来源不同
        assertTrue(r.zipWithNext().none { (a, b) -> a.id.value.first() == b.id.value.first() })
    }

    @Test
    fun selectCandidates_respectsLimit() {
        val new = (0..9).map { sense("n$it", "l-n$it", "n") }
        assertEquals(3, selectCandidates(QuizMode.LEARN, new, emptyList(), limit = 3, rng = Random(1)).size)
    }

    @Test
    fun selectCandidates_mixedFallsBackToSinglePoolWhenOneIsEmpty() {
        // review P1-2 回归：任一池为空时回退单池语义，不出空会话
        val new = (0..3).map { sense("n$it", "l-n$it", "n") }
        assertEquals(
            3,
            selectCandidates(QuizMode.MIXED, new, emptyList(), limit = 3, rng = Random(1)).size,
        )
        assertEquals(
            emptyList(),
            selectCandidates(QuizMode.MIXED, emptyList(), emptyList(), limit = 3, rng = Random(1)),
        )
    }
}
