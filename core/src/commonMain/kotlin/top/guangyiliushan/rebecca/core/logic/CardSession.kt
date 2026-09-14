package top.guangyiliushan.rebecca.core.logic

import kotlin.time.Duration.Companion.hours
import kotlin.time.Instant
import top.guangyiliushan.rebecca.core.model.AccountId
import top.guangyiliushan.rebecca.core.model.AnswerChannel
import top.guangyiliushan.rebecca.core.model.MasteryEvent
import top.guangyiliushan.rebecca.core.model.SenseId
import top.guangyiliushan.rebecca.core.model.SenseMastery

/**
 * 卡片判定 → 掌握度更新（0.1.2；SRS 演示级政策，真调度 0.2.2 校准）。
 * 认识：+0.5 稳定度（首次 0.5），12 小时后复习；不认识：−0.3（下限 0.1）、lapses+1、1 小时后重来。
 * 掌握度写入是 0.1.2 首次真实落点（0.1.1 的 quiz 刻意不写，当时 Q7 裁定）。
 * 边界政策：全新词首次判「不认识」得 stability 0.1（下限）而非 0（Review 🟡5）——留 0.2.2 SRS 校准时一并定。
 */

private const val KNOWN_DELTA = 0.5
private const val UNKNOWN_DELTA = 0.3
private const val MIN_STABILITY = 0.1

fun applyCardDecision(
    previous: SenseMastery?,
    accountId: AccountId,
    senseId: SenseId,
    known: Boolean,
    now: Instant,
): SenseMastery {
    // tombstone 视同 New：stability/lapses 均从零起
    val live = previous?.takeIf { it.deletedAt == null }
    val base = live?.stability ?: 0.0
    return if (known) {
        SenseMastery(
            accountId = accountId,
            senseId = senseId,
            stability = base + KNOWN_DELTA,
            dueAt = now + 12.hours,
            lapses = live?.lapses ?: 0,
            updatedAt = now,
        )
    } else {
        SenseMastery(
            accountId = accountId,
            senseId = senseId,
            stability = maxOf(MIN_STABILITY, base - UNKNOWN_DELTA),
            dueAt = now + 1.hours,
            lapses = (live?.lapses ?: 0) + 1,
            updatedAt = now,
        )
    }
}

/**
 * 卡片事件（kind="card"；channel 复用已有枚举 SWIPE）。
 * id = "card-{senseId}-{epochMillis}-{sequence}"：主键为 (accountId, id)，同 sense 同毫秒重复判定
 * 必须靠调用方传入的会话内单调 sequence 区分（否则 0.4.0 SQLDelight 主键冲突）。
 */
fun cardEvent(
    accountId: AccountId,
    senseId: SenseId,
    known: Boolean,
    occurredAt: Instant,
    durationMs: Int? = null,
    sequence: Int = 0,
): MasteryEvent = MasteryEvent(
    accountId = accountId,
    id = "card-${senseId.value}-${occurredAt.toEpochMilliseconds()}-$sequence",
    senseId = senseId,
    kind = "card",
    channel = AnswerChannel.SWIPE,
    correct = known,
    durationMs = durationMs,
    occurredAt = occurredAt,
)

/** 卡片会话统计（accuracy 口径与 QuizResult/StudyHub 一致：整数截断，非四舍五入）。 */
data class CardSessionSummary(
    val known: Int,
    val unknown: Int,
    val total: Int,
) {
    val accuracyPercent: Int
        get() = if (total == 0) 0 else known * 100 / total
}

fun cardSessionSummary(decisions: List<Boolean>): CardSessionSummary {
    val known = decisions.count { it }
    return CardSessionSummary(
        known = known,
        unknown = decisions.size - known,
        total = decisions.size,
    )
}
