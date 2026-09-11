package top.guangyiliushan.rebecca.core.repository

import top.guangyiliushan.rebecca.core.model.AccountId
import top.guangyiliushan.rebecca.core.model.Preference

/** 用户平面·偏好键值。 */
interface PreferenceRepository {
    fun get(accountId: AccountId, key: String): Preference?
    fun upsert(preference: Preference)
}
