package top.guangyiliushan.rebecca.core.logic

import kotlin.time.Instant
import top.guangyiliushan.rebecca.core.model.SenseMastery

/** Lemma 综合掌握度 = 活跃 Sense 掌握度均值，纯函数，禁止落库。 */
fun lemmaMastery(senses: List<SenseMastery>): Double {
    val active = senses.filter { it.deletedAt == null }
    if (active.isEmpty()) return 0.0
    return active.map { it.stability }.average()
}

/** 同步冲突合并，端云必须使用同一份实现。 */
fun merge(a: SenseMastery, b: SenseMastery): SenseMastery {
    require(a.accountId == b.accountId && a.senseId == b.senseId) {
        "merge requires the same (accountId, senseId)"
    }
    return SenseMastery(
        accountId = a.accountId,
        senseId = a.senseId,
        stability = maxOf(a.stability, b.stability),
        dueAt = minOf(a.dueAt, b.dueAt),
        lapses = maxOf(a.lapses, b.lapses),
        updatedAt = maxOf(a.updatedAt, b.updatedAt),
        deletedAt = latestTombstone(a.deletedAt, b.deletedAt),
    )
}

private fun latestTombstone(a: Instant?, b: Instant?): Instant? = when {
    a != null && b != null -> maxOf(a, b)
    a != null -> a
    else -> b
}
