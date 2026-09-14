package top.guangyiliushan.rebecca.core.logic

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.hours
import kotlin.time.Instant
import top.guangyiliushan.rebecca.core.model.AccountId
import top.guangyiliushan.rebecca.core.model.AnswerChannel
import top.guangyiliushan.rebecca.core.model.SenseId
import top.guangyiliushan.rebecca.core.model.SenseMastery

private val ACCT = AccountId("a")
private val SENSE = SenseId("s1")
private val NOW = Instant.fromEpochMilliseconds(1_700_000_000_000L)

private fun prev(stability: Double, lapses: Int = 0, deletedAt: Instant? = null) = SenseMastery(
    accountId = ACCT, senseId = SENSE, stability = stability,
    dueAt = NOW, lapses = lapses, updatedAt = NOW, deletedAt = deletedAt,
)

class CardSessionTest {
    @Test
    fun firstKnown_startsAtHalf() {
        val r = applyCardDecision(previous = null, accountId = ACCT, senseId = SENSE, known = true, now = NOW)
        assertEquals(0.5, r.stability)
        assertEquals(0, r.lapses)
        assertEquals(NOW + 12.hours, r.dueAt)
        assertEquals(ACCT, r.accountId)
        assertEquals(SENSE, r.senseId)
    }

    @Test
    fun consecutiveKnown_addsHalfEachTime() {
        val first = applyCardDecision(null, ACCT, SENSE, known = true, now = NOW)
        val second = applyCardDecision(first, ACCT, SENSE, known = true, now = NOW)
        assertEquals(1.0, second.stability)
        val third = applyCardDecision(second, ACCT, SENSE, known = true, now = NOW)
        assertEquals(1.5, third.stability)
    }

    @Test
    fun unknown_subtractsThirdAndIncrementsLapses() {
        val r = applyCardDecision(prev(1.0, lapses = 2), ACCT, SENSE, known = false, now = NOW)
        assertEquals(0.7, r.stability)
        assertEquals(3, r.lapses)
        assertEquals(NOW + 1.hours, r.dueAt)
    }

    @Test
    fun unknown_neverGoesBelowFloor() {
        val r = applyCardDecision(prev(0.2), ACCT, SENSE, known = false, now = NOW)
        assertEquals(0.1, r.stability)
    }

    @Test
    fun tombstonedPrevious_treatedAsNew() {
        val r = applyCardDecision(prev(3.0, deletedAt = NOW), ACCT, SENSE, known = true, now = NOW)
        assertEquals(0.5, r.stability) // 从 0 起算，不继承 tombstone 的 stability
        assertEquals(0, r.lapses)      // tombstone 的 lapses 也不继承
    }

    @Test
    fun known_preservesLapses() {
        val r = applyCardDecision(prev(1.0, lapses = 4), ACCT, SENSE, known = true, now = NOW)
        assertEquals(4, r.lapses)
    }

    @Test
    fun eventFields_areCorrect() {
        val e = cardEvent(ACCT, SENSE, known = true, occurredAt = NOW, durationMs = 1200, sequence = 0)
        assertEquals("card", e.kind)
        assertEquals(AnswerChannel.SWIPE, e.channel)
        assertEquals(true, e.correct)
        assertEquals(1200, e.durationMs)
        assertEquals(NOW, e.occurredAt)
        assertEquals(ACCT, e.accountId)
        assertEquals(SENSE, e.senseId)
        assertEquals("card-s1-${NOW.toEpochMilliseconds()}-0", e.id)
    }

    @Test
    fun eventId_differsBySequence_sameInstantAndSense() {
        // 同 sense、同毫秒判两次（重练/demo 固定时钟）不得撞主键 (accountId, id)
        val first = cardEvent(ACCT, SENSE, known = true, occurredAt = NOW, sequence = 0)
        val second = cardEvent(ACCT, SENSE, known = false, occurredAt = NOW, sequence = 1)
        assertTrue(first.id != second.id)
    }

    @Test
    fun summary_countsKnownUnknownAndAccuracy() {
        val s = cardSessionSummary(listOf(true, true, false))
        assertEquals(2, s.known)
        assertEquals(1, s.unknown)
        assertEquals(3, s.total)
        assertEquals(66, s.accuracyPercent) // 截断口径（与 QuizResult/StudyHub 一致）：2*100/3 = 66
    }

    @Test
    fun summary_emptySession_accuracyZero() {
        val s = cardSessionSummary(emptyList())
        assertEquals(0, s.total)
        assertEquals(0, s.accuracyPercent)
    }
}
