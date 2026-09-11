package top.guangyiliushan.rebecca.core.repository

import top.guangyiliushan.rebecca.core.model.AccountId
import top.guangyiliushan.rebecca.core.model.Sense

/** 学习调度入口；demo 调度不比较时钟，真 SRS 0.2.2 才接。 */
interface StudyRepository {
    fun dueSenses(accountId: AccountId, limit: Int): List<Sense>
}
