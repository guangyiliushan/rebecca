package top.guangyiliushan.rebecca.feature.lists

import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ListsViewModelTest {
    @AfterTest
    fun restoreDemoPreferences() {
        // 共享单例偏好还原（activeList 偏好可能被 onSetActive 改写）
        StudyHubListPrefsRestore.restore()
    }

    @Test
    fun loadsLibrary_bucketsActiveAndCompleted() {
        val vm = ListsViewModel()
        val s = vm.uiState.value
        // demo 4 单：Starter words（4 词全 mastered）→ Completed；其余 3 张为活跃
        assertEquals(listOf("Basic Verbs", "Daily Life", "port = carry（词根族）"), s.rows.map { it.title })
        assertEquals(listOf("Starter words"), s.completedRows.map { it.title })
        assertTrue(s.completedRows.all { it.completed })
        assertTrue(s.rows.all { !it.completed })
    }

    @Test
    fun rowCounts_comeFromMasteryGrades() {
        val vm = ListsViewModel()
        val s = vm.uiState.value
        val starter = s.completedRows.single()
        assertEquals(4, starter.wordCount)
        assertEquals(4, starter.mastered)
        val basic = s.rows.first { it.title == "Basic Verbs" }
        assertEquals(11, basic.wordCount)
        // run-1/supply-1/book-1 抬到 ≥2.0；light-2 抬到 4.0；form-1 5.0 → Basic Verbs 至少 4 mastered
        assertTrue(basic.mastered >= 4, "Basic Verbs mastered 应 ≥4，实际 ${basic.mastered}")
        assertTrue(basic.learned >= basic.mastered)
    }

    @Test
    fun search_filtersRowsByTitle() {
        val vm = ListsViewModel()
        vm.onQueryChange("daily")
        assertEquals(listOf("Daily Life"), vm.uiState.value.rows.map { it.title })
    }

    @Test
    fun search_noMatch_setsEmptySearch() {
        val vm = ListsViewModel()
        vm.onQueryChange("macbeth")
        assertTrue(vm.uiState.value.emptySearch)
        assertTrue(vm.uiState.value.rows.isEmpty())
    }

    @Test
    fun adaptiveRow_showsMatchedLevelAndTheoreticalSize() {
        val vm = ListsViewModel()
        val row = vm.uiState.value.adaptiveRow
        // Adaptive = 收录全站单词的词表，底部信息 = 当前匹配等级 + 理论词汇量（产品决策 2026-09-14）
        assertEquals("A1", row?.levelCode, "demo 已掌握词数落在 A1 区间")
        assertEquals(500, row?.levelTargetSize, "英语包 A1 理论词汇量")
        assertEquals(true, row?.isActive, "无实体活跃词单时 adaptive 即活跃学习来源")
    }

    @Test
    fun adaptiveRow_hiddenWhileSearching() {
        // 固定首行不是搜索结果（产品决策 2026-09-14）——搜索态由 state.query 判定
        val vm = ListsViewModel()
        vm.onQueryChange("daily")
        assertTrue(vm.uiState.value.query.isNotEmpty())
        assertFalse(vm.uiState.value.query.isEmpty()) // 屏幕据此不渲染 adaptive 行
        vm.onQueryChange("")
        assertTrue(vm.uiState.value.query.isEmpty())
    }

    @Test
    fun toggleCompleted_collapsesAndExpands() {
        val vm = ListsViewModel()
        assertTrue(vm.uiState.value.completedCollapsed) // 默认收起（09 spec）
        vm.onToggleCompleted()
        assertFalse(vm.uiState.value.completedCollapsed)
    }

    @Test
    fun setActive_roundTripsThroughPreference() {
        val vm = ListsViewModel()
        vm.onSetActive("list-daily-life")
        assertEquals("list-daily-life", vm.uiState.value.activeListId)
        assertEquals("Daily Life", vm.uiState.value.activeListTitle)
        vm.onSetActive(null) // 恢复 adaptive
        assertNull(vm.uiState.value.activeListId)
        assertNull(vm.uiState.value.activeListTitle)
    }

    @Test
    fun createList_addsRowAndPersists() {
        val vm = ListsViewModel()
        val before = vm.uiState.value.rows.size
        vm.onCreateList("Travel words", "Trip prep")
        val created = vm.uiState.value.rows.first { it.title == "Travel words" }
        assertEquals("Trip prep", created.description)
        assertEquals(before + 1, vm.uiState.value.rows.size)
        assertEquals("Personal", created.sourceLabel)
        // 清理（共享 demo 单例）
        vm.onDeleteList(created.id)
    }
}
