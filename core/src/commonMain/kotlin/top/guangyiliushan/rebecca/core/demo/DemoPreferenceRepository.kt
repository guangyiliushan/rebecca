package top.guangyiliushan.rebecca.core.demo

import top.guangyiliushan.rebecca.core.model.AccountId
import top.guangyiliushan.rebecca.core.model.Preference
import top.guangyiliushan.rebecca.core.repository.PreferenceRepository

internal object DemoPreferenceRepository : PreferenceRepository {
    private val preferences = mutableMapOf<Pair<AccountId, String>, Preference>()

    init {
        upsert(Preference(DEMO_ACCOUNT, "ui.theme", """{"mode":"system","family":"teal"}""", DEMO_NOW))
        upsert(Preference(DEMO_ACCOUNT, "ui.language", """{"mode":"follow_target"}""", DEMO_NOW))
        upsert(Preference(DEMO_ACCOUNT, "nativeAssist", """{"enabled":false}""", DEMO_NOW))
    }

    override fun get(accountId: AccountId, key: String): Preference? =
        preferences[accountId to key]

    override fun upsert(preference: Preference) {
        preferences[preference.accountId to preference.key] = preference
    }
}
