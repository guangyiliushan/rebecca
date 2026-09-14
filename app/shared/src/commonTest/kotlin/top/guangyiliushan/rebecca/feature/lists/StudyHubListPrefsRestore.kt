package top.guangyiliushan.rebecca.feature.lists

import top.guangyiliushan.rebecca.core.RebeccaData
import top.guangyiliushan.rebecca.core.model.Preference

/** 测试辅助：把共享 demo 单例的词单/偏好恢复到种子态（@AfterTest 用）。 */
internal object StudyHubListPrefsRestore {
    private val seedListIds = setOf("list-basic-verbs", "list-daily-life", "list-root-port", "list-completed-basics")

    fun restore() {
        // activeList 偏好还原 adaptive（空串 = adaptive）
        RebeccaData.preferences.upsert(
            Preference(
                accountId = RebeccaData.demoAccount,
                key = "study.activeList",
                valueJson = "",
                updatedAt = RebeccaData.demoNow,
            ),
        )
        // 测试期创建的词单软删
        RebeccaData.wordLists.lists(RebeccaData.demoAccount)
            .filter { it.id !in seedListIds }
            .forEach { RebeccaData.wordLists.deleteList(RebeccaData.demoAccount, it.id, RebeccaData.demoNow) }
    }
}
