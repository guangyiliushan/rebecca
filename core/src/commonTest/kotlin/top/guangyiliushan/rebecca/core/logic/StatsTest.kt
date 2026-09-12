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
}
