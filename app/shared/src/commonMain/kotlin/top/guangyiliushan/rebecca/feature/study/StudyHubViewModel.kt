package top.guangyiliushan.rebecca.feature.study

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import top.guangyiliushan.rebecca.core.RebeccaData
import top.guangyiliushan.rebecca.core.logic.activityDays
import top.guangyiliushan.rebecca.core.logic.currentStreak
import top.guangyiliushan.rebecca.core.logic.distinctSensesToday
import top.guangyiliushan.rebecca.core.model.MasteryEvent
import top.guangyiliushan.rebecca.core.model.Preference

/** 学习焦点（feature 层展示枚举；core 不感知 UI focus，0.2.x 若 core 需要再迁移——计划 R2）。 */
enum class LearningFocus { Vocabulary, ListeningReading, Grammar }

/** 近期词单展示值（词数由 WordListRepository.entries 真实统计）。 */
data class QuickList(
    val id: String,
    val title: String,
    val wordCount: Int,
)

/** 偏好键（载荷为纯 String：0.0.4 契约，0.4.1 接 kotlinx.serialization 后类型化）。 */
private const val GOAL_KEY = "study.goal"
private const val ACTIVE_LIST_KEY = "study.activeList"

/**
 * Study 主页状态（0.1.1 补：Study 富化）。
 * demo 静态快照 + 全真实计算（Q5 裁决：不动事件 seed）；0.2.3 统计版接事件流后改驱动式。
 */
data class StudyHubUiState(
    val dueCount: Int = 0,
    val newCount: Int = 0,
    val focus: LearningFocus = LearningFocus.Vocabulary,
    val accuracyPercent: Int? = null,
    val dailyGoal: Int = DEFAULT_GOAL,
    val completedToday: Int = 0,
    val activeListId: String? = null, // null = adaptive（默认可恢复，08 spec）
    val activity: List<Boolean> = emptyList(),
    val practicedDays: Int = 0,
    val streak: Int = 0,
    val quickLists: List<QuickList> = emptyList(),
) {
    companion object {
        const val DEFAULT_GOAL = 20
    }
}

/**
 * 学习中枢装配（R1：只编排）。
 * 会话池仍 due pool（0.1.1 契约）；词单选择只影响展示与偏好持久化——
 * list-scoped session 是 0.1.2 词库迁移的正式活（Q6 裁决 TODO）。
 */
class StudyHubViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(load())
    val uiState: StateFlow<StudyHubUiState> = _uiState

    fun onFocusSelect(focus: LearningFocus) {
        _uiState.value = _uiState.value.copy(focus = focus)
    }

    fun onGoalSave(words: Int) {
        if (words < 1 || words > 99) return
        upsertPreference(GOAL_KEY, words.toString())
        _uiState.value = load()
    }

    fun onListSelect(id: String?) {
        upsertPreference(ACTIVE_LIST_KEY, id.orEmpty())
        _uiState.value = load()
    }

    private fun upsertPreference(key: String, value: String) {
        RebeccaData.preferences.upsert(
            Preference(
                accountId = RebeccaData.demoAccount,
                key = key,
                valueJson = value,
                updatedAt = RebeccaData.demoNow,
            ),
        )
    }

    private fun load(): StudyHubUiState {
        val account = RebeccaData.demoAccount
        val now = RebeccaData.demoNow
        val events = RebeccaData.mastery.events(account)
        val due = RebeccaData.study.dueSenses(account, limit = 1000).size
        val total = RebeccaData.contentDictionary.allSenses().size
        val mastered = RebeccaData.mastery.allFor(account).size
        val activity = activityDays(events, now)
        val lists = RebeccaData.wordLists.lists(account).mapNotNull { list ->
            if (list.deletedAt != null) return@mapNotNull null
            val count = RebeccaData.wordLists.entries(account, list.id).count { it.deletedAt == null }
            QuickList(id = list.id, title = list.title, wordCount = count)
        }
        // 校验偏好里存的活动词单 id 仍在词单集合中（review 🟡9：陈旧 id 静默回退自适应）
        val activeListId = activeListFromPreferences()?.takeIf { id -> lists.any { it.id == id } }
        return StudyHubUiState(
            dueCount = due,
            newCount = total - mastered,
            accuracyPercent = accuracyPercent(events),
            dailyGoal = goalFromPreferences() ?: StudyHubUiState.DEFAULT_GOAL,
            completedToday = distinctSensesToday(events, now),
            activeListId = activeListId,
            activity = activity,
            practicedDays = activity.count { it },
            streak = currentStreak(events, now),
            quickLists = lists,
        )
    }

    private fun accuracyPercent(events: List<MasteryEvent>): Int? {
        if (events.isEmpty()) return null // "No reviews yet"，不显示误导性 0%（05 spec）
        val correct = events.count { it.correct }
        return correct * 100 / events.size
    }

    private fun goalFromPreferences(): Int? =
        RebeccaData.preferences.get(RebeccaData.demoAccount, GOAL_KEY)
            ?.valueJson?.toIntOrNull()

    private fun activeListFromPreferences(): String? =
        RebeccaData.preferences.get(RebeccaData.demoAccount, ACTIVE_LIST_KEY)
            ?.valueJson?.takeIf { it.isNotEmpty() }
}
