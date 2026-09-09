package top.guangyiliushan.rebecca.core.model

import kotlin.jvm.JvmInline
import kotlin.time.Instant

@JvmInline
value class AccountId(val value: String)

@JvmInline
value class SenseId(val value: String)

enum class WordListSource { USER, PACK, AI_EXTRACT, IMPORT }

enum class AnswerChannel { TAP, TYPE }

/** 词形入口（内容）；掌握度不挂在 Lemma 上。 */
data class Lemma(
    val id: String,
    val form: String,
)

/** 一条义项；Sense 级掌握度以此为聚合根。 */
data class Sense(
    val id: SenseId,
    val lemmaId: String,
    val definition: String,
    val partOfSpeech: String? = null,
)

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

data class WordList(
    val accountId: AccountId,
    val id: String,
    val title: String,
    val sourceType: WordListSource,
    val updatedAt: Instant,
    val deletedAt: Instant? = null,
)

data class WordListEntry(
    val accountId: AccountId,
    val listId: String,
    val senseId: SenseId,
    val ord: Int,
    val updatedAt: Instant,
    val deletedAt: Instant? = null,
)
