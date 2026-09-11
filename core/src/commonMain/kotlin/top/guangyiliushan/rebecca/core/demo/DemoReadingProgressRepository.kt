package top.guangyiliushan.rebecca.core.demo

import top.guangyiliushan.rebecca.core.model.AccountId
import top.guangyiliushan.rebecca.core.model.ReadingProgress
import top.guangyiliushan.rebecca.core.repository.ReadingProgressRepository

internal object DemoReadingProgressRepository : ReadingProgressRepository {
    private val progress = mutableMapOf<Pair<AccountId, String>, ReadingProgress>()

    init {
        upsertProgress(
            ReadingProgress(DEMO_ACCOUNT, "book-1", "p2", DEMO_NOW),
        )
    }

    override fun progress(accountId: AccountId, docId: String): ReadingProgress? =
        progress[accountId to docId]

    override fun upsertProgress(progress: ReadingProgress) {
        this.progress[progress.accountId to progress.docId] = progress
    }
}
