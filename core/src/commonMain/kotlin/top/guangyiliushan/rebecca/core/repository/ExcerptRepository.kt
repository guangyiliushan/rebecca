package top.guangyiliushan.rebecca.core.repository

import top.guangyiliushan.rebecca.core.model.AccountId
import top.guangyiliushan.rebecca.core.model.Excerpt

/** 用户平面·摘录（每聚合一仓储）。 */
interface ExcerptRepository {
    fun excerpts(accountId: AccountId): List<Excerpt>
    fun upsertExcerpt(excerpt: Excerpt)
}
