package top.guangyiliushan.rebecca.core.demo

import top.guangyiliushan.rebecca.core.model.AccountId
import top.guangyiliushan.rebecca.core.model.AnswerChannel
import top.guangyiliushan.rebecca.core.model.MasteryEvent
import top.guangyiliushan.rebecca.core.model.Sense
import top.guangyiliushan.rebecca.core.model.SenseId
import top.guangyiliushan.rebecca.core.model.SenseMastery
import top.guangyiliushan.rebecca.core.model.WordList
import top.guangyiliushan.rebecca.core.model.WordListEntry
import top.guangyiliushan.rebecca.core.model.WordListSource
import top.guangyiliushan.rebecca.core.repository.MasteryRepository
import top.guangyiliushan.rebecca.core.repository.StudyRepository
import top.guangyiliushan.rebecca.core.repository.WordListRepository
import kotlin.time.Duration.Companion.days
import kotlin.time.Instant

private val TOMORROW = DEMO_NOW + 1.days

internal object DemoWordListRepository : WordListRepository {
    private val lists = mutableListOf(
        WordList(DEMO_ACCOUNT, "list-basic-verbs", "Basic Verbs", WordListSource.PACK, DEMO_NOW),
        WordList(DEMO_ACCOUNT, "list-daily-life", "Daily Life", WordListSource.USER, DEMO_NOW),
        WordList(DEMO_ACCOUNT, "list-root-port", "port = carry（词根族）", WordListSource.AI_EXTRACT, DEMO_NOW),
    )

    private val entries = mutableListOf(
        // list-basic-verbs：run 的 4 个 sense + supply + book(2) + light(2) + form(2) = 11 词
        WordListEntry(DEMO_ACCOUNT, "list-basic-verbs", SenseId("sense-run-1"), 0, DEMO_NOW),
        WordListEntry(DEMO_ACCOUNT, "list-basic-verbs", SenseId("sense-run-2"), 1, DEMO_NOW),
        WordListEntry(DEMO_ACCOUNT, "list-basic-verbs", SenseId("sense-run-3"), 2, DEMO_NOW),
        WordListEntry(DEMO_ACCOUNT, "list-basic-verbs", SenseId("sense-run-4"), 3, DEMO_NOW),
        WordListEntry(DEMO_ACCOUNT, "list-basic-verbs", SenseId("sense-supply-1"), 4, DEMO_NOW),
        WordListEntry(DEMO_ACCOUNT, "list-basic-verbs", SenseId("sense-book-1"), 5, DEMO_NOW),
        WordListEntry(DEMO_ACCOUNT, "list-basic-verbs", SenseId("sense-book-2"), 6, DEMO_NOW),
        WordListEntry(DEMO_ACCOUNT, "list-basic-verbs", SenseId("sense-light-2"), 7, DEMO_NOW),
        WordListEntry(DEMO_ACCOUNT, "list-basic-verbs", SenseId("sense-light-3"), 8, DEMO_NOW),
        WordListEntry(DEMO_ACCOUNT, "list-basic-verbs", SenseId("sense-form-1"), 9, DEMO_NOW),
        WordListEntry(DEMO_ACCOUNT, "list-basic-verbs", SenseId("sense-form-2"), 10, DEMO_NOW),
        // list-daily-life：port 2 + light 1 + form 1 + book 1 = 8 词
        WordListEntry(DEMO_ACCOUNT, "list-daily-life", SenseId("sense-port-1"), 0, DEMO_NOW),
        WordListEntry(DEMO_ACCOUNT, "list-daily-life", SenseId("sense-port-2"), 1, DEMO_NOW),
        WordListEntry(DEMO_ACCOUNT, "list-daily-life", SenseId("sense-light-1"), 2, DEMO_NOW),
        WordListEntry(DEMO_ACCOUNT, "list-daily-life", SenseId("sense-form-1"), 3, DEMO_NOW),
        WordListEntry(DEMO_ACCOUNT, "list-daily-life", SenseId("sense-book-1"), 4, DEMO_NOW),
        WordListEntry(DEMO_ACCOUNT, "list-daily-life", SenseId("sense-run-1"), 5, DEMO_NOW),
        WordListEntry(DEMO_ACCOUNT, "list-daily-life", SenseId("sense-run-4"), 6, DEMO_NOW),
        WordListEntry(DEMO_ACCOUNT, "list-daily-life", SenseId("sense-supply-1"), 7, DEMO_NOW),
        // list-root-port：port 词根族 4 词
        WordListEntry(DEMO_ACCOUNT, "list-root-port", SenseId("sense-port-1"), 0, DEMO_NOW),
        WordListEntry(DEMO_ACCOUNT, "list-root-port", SenseId("sense-port-2"), 1, DEMO_NOW),
        WordListEntry(DEMO_ACCOUNT, "list-root-port", SenseId("sense-supply-1"), 2, DEMO_NOW),
        WordListEntry(DEMO_ACCOUNT, "list-root-port", SenseId("sense-form-2"), 3, DEMO_NOW),
    )

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

    /** 前 20 个 sense 有 mastery，后 8 个无 = 学习模式新词池（0.1.1，grill Q6）。 */
    private const val MASTERED_SENSE_COUNT = 20

    init {
        // 掌握度覆盖前 20 个 senses；dueAt 部分设「今天」（DEMO_NOW）供调度演示
        DEMO_SENSES.forEachIndexed { i, sense ->
            if (i >= MASTERED_SENSE_COUNT) return@forEachIndexed
            upsert(
                SenseMastery(
                    accountId = DEMO_ACCOUNT,
                    senseId = sense.id,
                    stability = 0.2 + (i % 5) * 0.15,
                    dueAt = if (i % 3 == 0) DEMO_NOW else TOMORROW,
                    lapses = i % 3,
                    updatedAt = DEMO_NOW,
                ),
            )
        }
        // 事件种子：连续 4 天（含今天）→ 中枢 streak=4；事件只挂已有 mastery 的 sense
        val mastered = DEMO_SENSES.map { it.id }.take(MASTERED_SENSE_COUNT)
        val seedPlan = listOf(0L to 2, 1L to 3, 2L to 2, 3L to 3) // (daysAgo, count)
        var k = 0
        for ((daysAgo, count) in seedPlan) {
            repeat(count) {
                events += MasteryEvent(
                    accountId = DEMO_ACCOUNT,
                    id = "demo-event-$k",
                    senseId = mastered[k++ % mastered.size],
                    kind = "quiz",
                    channel = AnswerChannel.TAP,
                    correct = k % 2 == 0,
                    durationMs = 1500 + k * 100,
                    occurredAt = DEMO_NOW - daysAgo.days,
                )
            }
        }
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

    override fun events(accountId: AccountId): List<MasteryEvent> =
        events.filter { it.accountId == accountId }
}

internal object DemoStudyRepository : StudyRepository {
    override fun dueSenses(accountId: AccountId, limit: Int): List<Sense> {
        val ids = DemoMasteryRepository.allFor(accountId)
            .filter { it.dueAt <= DEMO_NOW && it.deletedAt == null } // 0.1.1：dueAt <= now 截止
            .sortedBy { it.dueAt }
            .map { it.senseId }
            .take(limit)
        return ids.mapNotNull { DemoContentDictionaryRepository.sense(it) }
    }
}
