package top.guangyiliushan.rebecca.core.logic

import kotlin.time.Instant
import top.guangyiliushan.rebecca.core.model.MasteryEvent

/**
 * 连续学习天数（Q8 裁定 a：中枢核心数字）。
 * v0 以 UTC 日切分（epoch 86400s）；时区敏感的正确日历日归 0.2.3 统计版（事件流聚合）。
 * 语义：今天有事件从今天数起，否则从昨天数起（今天没学不算断）；遇断即停。
 */
fun currentStreak(events: List<MasteryEvent>, now: Instant): Int {
    fun dayOf(t: Instant): Long = t.epochSeconds / 86_400
    val days = events.map { dayOf(it.occurredAt) }.toSet()
    var d = dayOf(now)
    if (d !in days) d -= 1
    var streak = 0
    while (d in days) { streak++; d-- }
    return streak
}

/**
 * 最近 [days] 天每日是否有学习事件（索引 0 = 最旧，末位 = 今天）。
 * 日切分口径与 [currentStreak] 相同（UTC epoch 日）；同日多条事件去重。
 * 消费方：StreakCard 的 7 格 pipe 与 "Practiced on X of the last N days" 文本替代。
 */
fun activityDays(events: List<MasteryEvent>, now: Instant, days: Int = 7): List<Boolean> {
    require(days > 0) { "days must be positive" }
    fun dayOf(t: Instant): Long = t.epochSeconds / 86_400
    val active = events.map { dayOf(it.occurredAt) }.toSet()
    val today = dayOf(now)
    return (days - 1 downTo 0).map { offset -> (today - offset) in active }
}

/**
 * 今天（UTC 日，口径同 [currentStreak]）练习过的不同词条数。
 * LearningFocusCard 的 "X of Y words" 进度文本之 X；同日同 sense 去重。
 */
fun distinctSensesToday(events: List<MasteryEvent>, now: Instant): Int {
    val today = now.epochSeconds / 86_400
    return events
        .filter { it.occurredAt.epochSeconds / 86_400 == today }
        .map { it.senseId }
        .toSet()
        .size
}
