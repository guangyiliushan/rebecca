package top.guangyiliushan.rebecca.core.logic

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Instant
import kotlin.time.Duration.Companion.days
import top.guangyiliushan.rebecca.core.model.AccountId
import top.guangyiliushan.rebecca.core.model.SenseId
import top.guangyiliushan.rebecca.core.model.SenseMastery

private val ACCT = AccountId("a")
private val NOW = Instant.fromEpochMilliseconds(1_700_000_000_000L)

private fun mastery(stability: Double, dueAt: Instant = NOW + 1.days, deletedAt: Instant? = null) = SenseMastery(
    accountId = ACCT,
    senseId = SenseId("s"),
    stability = stability,
    dueAt = dueAt,
    lapses = 0,
    updatedAt = NOW,
    deletedAt = deletedAt,
)

class MasteryGradeTest {
    @Test
    fun noMastery_isNew() = assertEquals(MasteryGrade.New, masteryGrade(null))

    @Test
    fun tombstoned_isNew() = assertEquals(MasteryGrade.New, masteryGrade(mastery(3.0, deletedAt = NOW)))

    @Test
    fun lowStability_isLearning() = assertEquals(MasteryGrade.Learning, masteryGrade(mastery(0.8)))

    @Test
    fun justBelowThreshold_isLearning() = assertEquals(MasteryGrade.Learning, masteryGrade(mastery(1.99)))

    @Test
    fun atThreshold_isMastered() = assertEquals(MasteryGrade.Mastered, masteryGrade(mastery(2.0)))

    @Test
    fun aboveThreshold_isMastered() = assertEquals(MasteryGrade.Mastered, masteryGrade(mastery(5.0)))

    @Test
    fun dueWhenDueAtInPast() {
        assertTrue(isDue(mastery(1.0, dueAt = NOW), NOW))
        assertTrue(isDue(mastery(1.0, dueAt = NOW - 1.days), NOW))
    }

    @Test
    fun notDueWhenDueAtInFuture() = assertFalse(isDue(mastery(1.0, dueAt = NOW + 1.days), NOW))

    @Test
    fun tombstonedOrNull_isNeverDue() {
        assertFalse(isDue(null, NOW))
        assertFalse(isDue(mastery(1.0, dueAt = NOW, deletedAt = NOW), NOW))
    }
}
