package top.guangyiliushan.rebecca.core.demo

import kotlin.time.Instant
import top.guangyiliushan.rebecca.core.model.AccountId
import top.guangyiliushan.rebecca.core.model.MasteryEvent
import top.guangyiliushan.rebecca.core.model.Sense
import top.guangyiliushan.rebecca.core.model.SenseId
import top.guangyiliushan.rebecca.core.model.SenseMastery
import top.guangyiliushan.rebecca.core.model.WordList
import top.guangyiliushan.rebecca.core.model.WordListEntry
import top.guangyiliushan.rebecca.core.model.WordListSource
import top.guangyiliushan.rebecca.core.repository.DictionaryRepository
import top.guangyiliushan.rebecca.core.repository.MasteryRepository
import top.guangyiliushan.rebecca.core.repository.StudyRepository
import top.guangyiliushan.rebecca.core.repository.WordListRepository

private val DEMO_ACCOUNT = AccountId("demo-account")
private val NOW = Instant.fromEpochMilliseconds(1_700_000_000_000L)

private val SENSES = listOf(
    Sense(SenseId("sense-run-1"), "lemma-run", "move at a speed faster than a walk", "verb"),
    Sense(SenseId("sense-run-2"), "lemma-run", "operate or function", "verb"),
    Sense(SenseId("sense-supply-1"), "lemma-supply", "make something needed available", "verb"),
)

private val DEMO_LIST = WordList(DEMO_ACCOUNT, "list-basic-verbs", "Basic Verbs", WordListSource.PACK, NOW)

private val DEMO_ENTRIES = SENSES.mapIndexed { i, sense ->
    WordListEntry(DEMO_ACCOUNT, DEMO_LIST.id, sense.id, i, NOW)
}

internal object DemoDictionaryRepository : DictionaryRepository {
    override fun sense(id: SenseId): Sense? = SENSES.firstOrNull { it.id == id }

    override fun sensesForLemma(lemmaId: String): List<Sense> =
        SENSES.filter { it.lemmaId == lemmaId }

    override fun search(query: String): List<Sense> {
        val q = query.trim()
        if (q.isEmpty()) return SENSES
        return SENSES.filter { it.definition.contains(q, ignoreCase = true) }
    }
}

internal object DemoWordListRepository : WordListRepository {
    private val lists = mutableListOf(DEMO_LIST)
    private val entries = DEMO_ENTRIES.toMutableList()

    override fun lists(accountId: AccountId): List<WordList> =
        lists.filter { it.accountId == accountId && it.deletedAt == null }

    override fun entries(accountId: AccountId, listId: String): List<WordListEntry> =
        entries
            .filter { it.accountId == accountId && it.listId == listId && it.deletedAt == null }
            .sortedBy { it.ord }

    override fun upsertList(list: WordList) {
        lists.removeAll { it.accountId == list.accountId && it.id == list.id }
        lists += list
    }

    override fun upsertEntry(entry: WordListEntry) {
        entries.removeAll {
            it.accountId == entry.accountId && it.listId == entry.listId && it.senseId == entry.senseId
        }
        entries += entry
    }
}

internal object DemoMasteryRepository : MasteryRepository {
    private val mastery = mutableMapOf<Pair<AccountId, SenseId>, SenseMastery>()
    private val events = mutableListOf<MasteryEvent>()

    init {
        upsert(SenseMastery(DEMO_ACCOUNT, SenseId("sense-run-1"), 0.8, NOW, lapses = 0, updatedAt = NOW))
        upsert(SenseMastery(DEMO_ACCOUNT, SenseId("sense-run-2"), 0.4, NOW, lapses = 2, updatedAt = NOW))
        upsert(SenseMastery(DEMO_ACCOUNT, SenseId("sense-supply-1"), 0.6, NOW, lapses = 1, updatedAt = NOW))
    }

    override fun mastery(accountId: AccountId, senseId: SenseId): SenseMastery? =
        mastery[accountId to senseId]

    override fun allFor(accountId: AccountId): List<SenseMastery> =
        mastery.values.filter { it.accountId == accountId && it.deletedAt == null }

    override fun upsert(mastery: SenseMastery) {
        this.mastery[mastery.accountId to mastery.senseId] = mastery
    }

    override fun record(event: MasteryEvent) {
        events += event
    }
}

internal object DemoStudyRepository : StudyRepository {
    override fun dueSenses(accountId: AccountId, limit: Int): List<Sense> {
        // ponytail: demo 调度不比较时钟；Slice 1 接练习会话时再加 dueAt <= now 截止判断。
        val ids = DemoMasteryRepository.allFor(accountId)
            .sortedBy { it.dueAt }
            .map { it.senseId }
            .take(limit)
        return ids.mapNotNull { DemoDictionaryRepository.sense(it) }
    }
}
