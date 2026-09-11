package top.guangyiliushan.rebecca.core.repository

import top.guangyiliushan.rebecca.core.model.AccountId
import top.guangyiliushan.rebecca.core.model.WordList
import top.guangyiliushan.rebecca.core.model.WordListEntry

/** 用户平面·词单（聚合根 = WordList；Entry 为子实体）。 */
interface WordListRepository {
    fun lists(accountId: AccountId): List<WordList>
    fun entries(accountId: AccountId, listId: String): List<WordListEntry>
    fun upsertList(list: WordList)
    fun upsertEntry(entry: WordListEntry)
}
