package top.guangyiliushan.rebecca.design.patterns

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * 快捷条带行划分（review 🟠2 修复：200% 字体下 4 连排必截断 → Compact=2×2 / Regular=1×4）。
 * 纯函数锁行为：不读窗口尺寸（F-L4），密度由 AppScaffold 经 LocalLayoutDensity 下发。
 */
class HomeQuickActionBarRowsTest {

    private fun actions(n: Int) = List(n) { QuickAction(label = "A$it", onClick = {}) }

    @Test
    fun regular_singleRow() {
        assertEquals(listOf(4), quickActionRows(actions(4), LayoutDensity.Regular).map { it.size })
    }

    @Test
    fun compact_twoByTwo() {
        assertEquals(listOf(2, 2), quickActionRows(actions(4), LayoutDensity.Compact).map { it.size })
    }

    @Test
    fun compact_oddCount_lastRowShorter() {
        assertEquals(listOf(2, 1), quickActionRows(actions(3), LayoutDensity.Compact).map { it.size })
    }

    @Test
    fun preservesOrder() {
        val rows = quickActionRows(actions(4), LayoutDensity.Compact)
        assertEquals(listOf("A0", "A1"), rows[0].map { it.label })
        assertEquals(listOf("A2", "A3"), rows[1].map { it.label })
    }

    @Test
    fun empty_returnsNoRows() {
        assertEquals(emptyList(), quickActionRows(emptyList(), LayoutDensity.Compact))
    }
}
