package top.guangyiliushan.rebecca.core.model

import kotlin.time.Instant

enum class WordListSource { USER, PACK, AI_EXTRACT, IMPORT }

data class WordList(
    val accountId: AccountId,
    val id: String,
    val title: String,
    val sourceType: WordListSource,
    val updatedAt: Instant,
    val description: String? = null, // 0.1.2 追加（backend-scope §6.2 同批更新）；置于 updatedAt 后保住既有位置实参
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
