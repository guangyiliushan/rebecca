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
