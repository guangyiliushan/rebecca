package top.guangyiliushan.rebecca.core.model

import kotlin.time.Instant

enum class AnswerChannel { TAP, TYPE, HANDWRITE, VOICE, SWIPE }

/** Sense 级掌握度，用户数据平面，主键 (accountId, senseId)。 */
data class SenseMastery(
    val accountId: AccountId,
    val senseId: SenseId,
    val stability: Double,
    val dueAt: Instant,
    val lapses: Int = 0,
    val updatedAt: Instant,
    val deletedAt: Instant? = null,
)

data class MasteryEvent(
    val accountId: AccountId,
    val id: String,
    val senseId: SenseId,
    val kind: String,
    val channel: AnswerChannel,
    val correct: Boolean,
    val durationMs: Int? = null,
    val occurredAt: Instant,
)
