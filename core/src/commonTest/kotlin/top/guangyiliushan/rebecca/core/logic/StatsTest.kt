package top.guangyiliushan.rebecca.core.logic

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Instant
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import top.guangyiliushan.rebecca.core.model.AccountId
import top.guangyiliushan.rebecca.core.model.AnswerChannel
import top.guangyiliushan.rebecca.core.model.MasteryEvent
import top.guangyiliushan.rebecca.core.model.SenseId

private val ACCT = AccountId("a")
private val NOW = Instant.fromEpochMilliseconds(1_700_000_000_000L)

private fun evt(daysAgo: Long, hour: Long = 12) = MasteryEvent(
    accountId = ACCT, id = "e-$daysAgo", senseId = SenseId("s"),
    kind = "quiz", channel = AnswerChannel.TAP, correct = true,
    occurredAt = NOW - daysAgo.days + (hour - 12).hours,
)

class StatsTest {
    @Test
    fun emptyEvents_zeroStreak() = assertEquals(0, currentStreak(emptyList(), NOW))

    @Test
    fun onlyToday_one() = assertEquals(1, currentStreak(listOf(evt(0)), NOW))

    @Test
    fun fourConsecutiveDaysIncludingToday_four() {
        assertEquals(4, currentStreak(listOf(evt(0), evt(1), evt(2), evt(3)), NOW))
    }

    @Test
    fun gapBreaksStreak() {
        assertEquals(1, currentStreak(listOf(evt(0), evt(2)), NOW)) // 昨天断，只算今天
    }

    @Test
    fun todayEmpty_countsFromYesterday() {
        assertEquals(3, currentStreak(listOf(evt(1), evt(2), evt(3)), NOW))
    }

    @Test
    fun activityDays_emptyEvents_allFalse() {
        assertEquals(List(7) { false }, activityDays(emptyList(), NOW))
    }

    @Test
    fun activityDays_todayOnly_lastTrue() {
        assertEquals(listOf(false, false, false, false, false, false, true), activityDays(listOf(evt(0)), NOW))
    }

    @Test
    fun activityDays_fourConsecutive_lastFourTrue() {
        assertEquals(
            listOf(false, false, false, true, true, true, true),
            activityDays(listOf(evt(0), evt(1), evt(2), evt(3)), NOW),
        )
    }

    @Test
    fun activityDays_gapDay_falseInBetween() {
        assertEquals(
            listOf(false, false, false, false, true, false, true),
            activityDays(listOf(evt(0), evt(2)), NOW),
        )
    }

    @Test
    fun activityDays_sameDayDuplicates_deduped() {
        assertEquals(
            listOf(false, false, false, false, false, false, true),
            activityDays(listOf(evt(0, hour = 10), evt(0, hour = 14), evt(0, hour = 18)), NOW),
        )
    }

    @Test
    fun activityDays_customWindow() {
        assertEquals(listOf(true, false, true), activityDays(listOf(evt(0), evt(2)), NOW, days = 3))
    }

    @Test
    fun activityDays_allOlderThanWindow_allFalse() {
        assertEquals(List(7) { false }, activityDays(listOf(evt(7), evt(10)), NOW))
    }

    @Test
    fun distinctSensesToday_countsDistinctSenses() {
        val events = listOf(
            evt(0, hour = 10).copy(id = "a", senseId = SenseId("s1")),
            evt(0, hour = 11).copy(id = "b", senseId = SenseId("s2")),
            evt(0, hour = 10).copy(id = "c", senseId = SenseId("s1")),
        )
        assertEquals(2, distinctSensesToday(events, NOW))
    }

    @Test
    fun distinctSensesToday_excludesOtherDays() {
        val events = listOf(
            evt(0, hour = 10).copy(id = "a", senseId = SenseId("s1")),
            evt(1, hour = 10).copy(id = "b", senseId = SenseId("s2")),
            evt(2, hour = 10).copy(id = "c", senseId = SenseId("s3")),
        )
        assertEquals(1, distinctSensesToday(events, NOW))
    }

    @Test
    fun distinctSensesToday_emptyEvents_zero() {
        assertEquals(0, distinctSensesToday(emptyList(), NOW))
    }
}
