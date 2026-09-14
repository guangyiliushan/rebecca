package top.guangyiliushan.rebecca.core.repository

import kotlin.time.Instant
import top.guangyiliushan.rebecca.core.model.AccountId
import top.guangyiliushan.rebecca.core.model.SenseId
import top.guangyiliushan.rebecca.core.model.WordList
import top.guangyiliushan.rebecca.core.model.WordListEntry

/** 用户平面·词单（聚合根 = WordList；Entry 为子实体）。 */
interface WordListRepository {
    fun lists(accountId: AccountId): List<WordList>
    fun entries(accountId: AccountId, listId: String): List<WordListEntry>
    fun upsertList(list: WordList)
    fun upsertEntry(entry: WordListEntry)

    // 0.1.2 补齐（词库 CRUD + 卡片浏览反查）
    fun deleteList(accountId: AccountId, listId: String, deletedAt: Instant)
    fun removeEntry(accountId: AccountId, listId: String, senseId: SenseId, deletedAt: Instant)

    /**
     * 全量重排：`orderedSenseIds` 必须是该单**当前全部有效 entry** 的有序 id 列表（按索引赋 ord）。
     * 未列入的 entry 保持原 ord（子集传入可能产生重复 ord，消费方靠 ord 稳定排序兜底）。
     */
    fun reorder(accountId: AccountId, listId: String, orderedSenseIds: List<SenseId>, updatedAt: Instant)
    fun listsContaining(accountId: AccountId, senseId: SenseId): List<WordList>
}
