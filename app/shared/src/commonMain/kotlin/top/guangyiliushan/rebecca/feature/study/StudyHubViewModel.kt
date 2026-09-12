package top.guangyiliushan.rebecca.feature.study

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import top.guangyiliushan.rebecca.core.RebeccaData
import top.guangyiliushan.rebecca.core.logic.currentStreak

data class StudyHubUiState(
    val dueCount: Int = 0,
    val newCount: Int = 0,
    val streak: Int = 0,
)

sealed interface StudyHubUiEvent

/** 学习中枢装配（R1：只编排）；demo 静态快照，0.2.3 统计版接事件流后改驱动式。 */
class StudyHubViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(load())
    val uiState: StateFlow<StudyHubUiState> = _uiState

    fun onEvent(event: StudyHubUiEvent) = Unit // 0.1.1 无事件；预留入口

    private fun load(): StudyHubUiState {
        val account = RebeccaData.demoAccount
        val now = RebeccaData.demoNow
        val due = RebeccaData.study.dueSenses(account, limit = 1000).size
        val total = RebeccaData.contentDictionary.search("").size
        val mastered = RebeccaData.mastery.allFor(account).size
        val streak = currentStreak(RebeccaData.mastery.events(account), now)
        return StudyHubUiState(dueCount = due, newCount = total - mastered, streak = streak)
    }
}
