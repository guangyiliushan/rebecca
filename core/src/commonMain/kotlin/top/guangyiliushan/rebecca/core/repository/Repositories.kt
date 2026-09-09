package top.guangyiliushan.rebecca.core.repository

import top.guangyiliushan.rebecca.core.model.AccountId
import top.guangyiliushan.rebecca.core.model.MasteryEvent
import top.guangyiliushan.rebecca.core.model.Sense
import top.guangyiliushan.rebecca.core.model.SenseId
import top.guangyiliushan.rebecca.core.model.SenseMastery
import top.guangyiliushan.rebecca.core.model.WordList
import top.guangyiliushan.rebecca.core.model.WordListEntry

interface DictionaryRepository {
    fun sense(id: SenseId): Sense?
    fun sensesForLemma(lemmaId: String): List<Sense>
    fun search(query: String): List<Sense>
}

interface WordListRepository {
    fun lists(accountId: AccountId): List<WordList>
    fun entries(accountId: AccountId, listId: String): List<WordListEntry>
    fun upsertList(list: WordList)
    fun upsertEntry(entry: WordListEntry)
}

interface MasteryRepository {
    fun mastery(accountId: AccountId, senseId: SenseId): SenseMastery?
    fun allFor(accountId: AccountId): List<SenseMastery>
    fun upsert(mastery: SenseMastery)
    fun record(event: MasteryEvent)
}

interface StudyRepository {
    fun dueSenses(accountId: AccountId, limit: Int): List<Sense>
}
