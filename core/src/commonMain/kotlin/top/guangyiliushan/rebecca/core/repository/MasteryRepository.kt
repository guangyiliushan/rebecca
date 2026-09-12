package top.guangyiliushan.rebecca.core.repository

import top.guangyiliushan.rebecca.core.model.AccountId
import top.guangyiliushan.rebecca.core.model.MasteryEvent
import top.guangyiliushan.rebecca.core.model.SenseId
import top.guangyiliushan.rebecca.core.model.SenseMastery

/** 用户平面·掌握度（聚合根 = SenseMastery；MasteryEvent 为其事实流）。 */
interface MasteryRepository {
    fun mastery(accountId: AccountId, senseId: SenseId): SenseMastery?
    fun allFor(accountId: AccountId): List<SenseMastery>
    fun upsert(mastery: SenseMastery)
    fun record(event: MasteryEvent)
    fun events(accountId: AccountId): List<MasteryEvent>
}
