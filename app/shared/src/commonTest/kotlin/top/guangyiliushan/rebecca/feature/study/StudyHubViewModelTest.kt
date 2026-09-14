package top.guangyiliushan.rebecca.feature.study

import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class StudyHubViewModelTest {
    @AfterTest
    fun restoreDemoPreferences() {
        // 异常安全还原（review 🟡4）：共享单例 DemoPreferenceRepository，失败也还原
        StudyHubViewModel().onGoalSave(20)
        StudyHubViewModel().onListSelect(null)
    }

    @Test
    fun loadsDemoSnapshot_withExpectedCounts() {
        val vm = StudyHubViewModel()
        val s = vm.uiState.value
        assertEquals(7, s.dueCount)   // i%3==0 且 i<20 → 7 个 due 今天
        assertEquals(8, s.newCount)   // 后 8 个 sense 无 mastery = 新词池
        assertEquals(4, s.streak)     // 事件种子连续 4 天
    }

    @Test
    fun loadsDemoSnapshot_withEnrichedState() {
        val vm = StudyHubViewModel()
        val s = vm.uiState.value
        assertEquals(LearningFocus.Vocabulary, s.focus)
        assertEquals(20, s.dailyGoal)                       // demo 偏好 study.goal 默认
        assertEquals(2, s.completedToday)                   // 今日 2 事件、2 个不同 sense
        assertEquals(50, s.accuracyPercent)                 // 10 事件 5 对，真实计算（Q5）
        assertEquals(listOf(false, false, false, true, true, true, true), s.activity)
        assertEquals(4, s.practicedDays)
        assertNull(s.activeListId)                          // adaptive 默认
        assertEquals(listOf("Basic Verbs", "Daily Life", "port = carry（词根族）", "Starter words"), s.quickLists.map { it.title })
        assertEquals(listOf(11, 8, 4, 4), s.quickLists.map { it.wordCount })
    }

    @Test
    fun goalSave_roundTripsThroughPreference() {
        val vm = StudyHubViewModel()
        vm.onGoalSave(30)
        assertEquals(30, vm.uiState.value.dailyGoal)
        vm.onGoalSave(0)   // 非法忽略（1..99）
        assertEquals(30, vm.uiState.value.dailyGoal)
        vm.onGoalSave(100) // 非法忽略
        assertEquals(30, vm.uiState.value.dailyGoal)
        vm.onGoalSave(20)  // 还原，避免污染其他测试
        assertEquals(20, vm.uiState.value.dailyGoal)
    }

    @Test
    fun listSelect_roundTripsAndRestoresAdaptive() {
        val vm = StudyHubViewModel()
        vm.onListSelect("list-daily-life")
        assertEquals("list-daily-life", vm.uiState.value.activeListId)
        vm.onListSelect(null) // adaptive 恢复（08 spec：默认可恢复）
        assertNull(vm.uiState.value.activeListId)
    }

    @Test
    fun focusSelect_isSessionLocal() {
        val vm = StudyHubViewModel()
        vm.onFocusSelect(LearningFocus.Grammar)
        assertEquals(LearningFocus.Grammar, vm.uiState.value.focus)
        vm.onFocusSelect(LearningFocus.Vocabulary)
        assertEquals(LearningFocus.Vocabulary, vm.uiState.value.focus)
    }
}
