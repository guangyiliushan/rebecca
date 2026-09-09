package top.guangyiliushan.rebecca.core.logic

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Instant
import top.guangyiliushan.rebecca.core.model.AccountId
import top.guangyiliushan.rebecca.core.model.SenseId
import top.guangyiliushan.rebecca.core.model.SenseMastery

class MasteryTest {
    private val account = AccountId("a")
    private val t0 = Instant.fromEpochMilliseconds(1000)
    private val t1 = Instant.fromEpochMilliseconds(2000)

    private fun mastery(
        sense: String,
        stability: Double,
        lapses: Int = 0,
        dueAt: Instant = t0,
        updatedAt: Instant = t0,
        deletedAt: Instant? = null,
    ) = SenseMastery(account, SenseId(sense), stability, dueAt, lapses, updatedAt, deletedAt)

    @Test
    fun lemmaMastery_averagesActiveSenses() {
        val result = lemmaMastery(
            listOf(
                mastery("s1", 0.8),
                mastery("s2", 0.4),
                mastery("s3", 0.6, deletedAt = t1),
            ),
        )
        assertEquals(0.6, result, 1e-9)
    }

    @Test
    fun lemmaMastery_emptyIsZero() {
        assertEquals(0.0, lemmaMastery(emptyList()))
    }

    @Test
    fun merge_takesConservativePerField() {
        val a = mastery("s1", 0.8, lapses = 2, dueAt = t0, updatedAt = t0)
        val b = mastery("s1", 0.4, lapses = 5, dueAt = t1, updatedAt = t1)
        val m = merge(a, b)
        assertEquals(0.8, m.stability, 1e-9)
        assertEquals(5, m.lapses)
        assertEquals(t0, m.dueAt)
        assertEquals(t1, m.updatedAt)
        assertEquals<Instant?>(null, m.deletedAt)
    }

    @Test
    fun merge_tombstoneWins() {
        val live = mastery("s1", 0.8)
        val tomb = mastery("s1", 0.4, updatedAt = t1, deletedAt = t1)
        assertEquals(t1, merge(live, tomb).deletedAt)
    }
}
