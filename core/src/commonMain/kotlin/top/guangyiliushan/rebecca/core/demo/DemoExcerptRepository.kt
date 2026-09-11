package top.guangyiliushan.rebecca.core.demo

import top.guangyiliushan.rebecca.core.model.AccountId
import top.guangyiliushan.rebecca.core.model.Excerpt
import top.guangyiliushan.rebecca.core.model.SenseId
import top.guangyiliushan.rebecca.core.repository.ExcerptRepository

internal object DemoExcerptRepository : ExcerptRepository {
    private val excerpts = mutableListOf<Excerpt>()

    init {
        excerpts += Excerpt(
            accountId = DEMO_ACCOUNT,
            id = "excerpt-1",
            senseId = SenseId("sense-run-1"),
            text = "On the way home, he began to run.",
            docId = "book-1",
            startOff = 18,
            endOff = 51,
            updatedAt = DEMO_NOW,
        )
    }

    override fun excerpts(accountId: AccountId): List<Excerpt> =
        excerpts.filter { it.accountId == accountId && it.deletedAt == null }

    override fun upsertExcerpt(excerpt: Excerpt) {
        excerpts.removeAll { it.accountId == excerpt.accountId && it.id == excerpt.id }
        excerpts += excerpt
    }
}
