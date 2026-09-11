package top.guangyiliushan.rebecca.core.model

import kotlin.time.Instant

/** 阅读进度；position 是语言包定义的定位串，不解释。 */
data class ReadingProgress(
    val accountId: AccountId,
    val docId: String,
    val position: String,
    val updatedAt: Instant,
    val deletedAt: Instant? = null,
)

/** 摘录句（点句 → 词库引用）。docId+offset 打开阅读器，导入书正文永不进模型正文。 */
data class Excerpt(
    val accountId: AccountId,
    val id: String,
    val senseId: SenseId,
    val text: String,
    val docId: String,
    val startOff: Int,
    val endOff: Int,
    val updatedAt: Instant,
    val deletedAt: Instant? = null,
)
