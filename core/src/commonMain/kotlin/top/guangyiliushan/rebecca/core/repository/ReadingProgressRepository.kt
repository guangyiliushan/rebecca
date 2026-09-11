package top.guangyiliushan.rebecca.core.repository

import top.guangyiliushan.rebecca.core.model.AccountId
import top.guangyiliushan.rebecca.core.model.ReadingProgress

/** 用户平面·阅读进度（每聚合一仓储，对齐 backend-scope §7.3 两表）。 */
interface ReadingProgressRepository {
    fun progress(accountId: AccountId, docId: String): ReadingProgress?
    fun upsertProgress(progress: ReadingProgress)
}
