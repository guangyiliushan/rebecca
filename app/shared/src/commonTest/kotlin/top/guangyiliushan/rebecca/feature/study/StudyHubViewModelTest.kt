package top.guangyiliushan.rebecca.feature.study

import kotlin.test.Test
import kotlin.test.assertEquals

class StudyHubViewModelTest {
    @Test
    fun loadsDemoSnapshot_withExpectedCounts() {
        val vm = StudyHubViewModel()
        val s = vm.uiState.value
        assertEquals(7, s.dueCount)   // i%3==0 且 i<20 → 7 个 due 今天
        assertEquals(8, s.newCount)   // 后 8 个 sense 无 mastery = 新词池
        assertEquals(4, s.streak)     // 事件种子连续 4 天
    }
}
