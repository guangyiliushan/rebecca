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
        // 0.1.1 补：Study 富化（LearningFocusCard 每日目标；goal 编辑 sheet 回写此键）。
        // 载荷为纯 String（D3：与 StudyHubViewModel 读写端一致，不用 JSON）
        upsert(Preference(DEMO_ACCOUNT, "study.goal", "20", DEMO_NOW))
    }

    override fun get(accountId: AccountId, key: String): Preference? =
        preferences[accountId to key]

    override fun upsert(preference: Preference) {
        preferences[preference.accountId to preference.key] = preference
    }
}
