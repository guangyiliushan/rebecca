package top.guangyiliushan.rebecca.design.patterns

import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.snapTo
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.test.runComposeUiTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import top.guangyiliushan.rebecca.design.components.AppChipGroupMode
import top.guangyiliushan.rebecca.design.theme.AppTheme
import top.guangyiliushan.rebecca.design.theme.ThemeSettings

/**
 * 0.1.2 Lists patterns 语义（F15/F8/§10.2）：
 * 搜索框清空动作 / 词单行合并朗读 + More 独立 / 活跃卡主行动唯一 /
 * 主从双栏密度驱动 / 筛选 sheet 分组渲染 / 卡片手势层内容挂载。
 */
@OptIn(ExperimentalTestApi::class)
class ListsPatternsSemanticsTest {

    @Test
    fun searchField_clearActionVisibleOnlyWithQuery() = runComposeUiTest {
        var cleared = false
        var text = ""
        setContent {
            AppTheme(ThemeSettings()) {
                WordListSearchField(
                    value = "macbeth",
                    onValueChange = { text = it },
                    onClear = { cleared = true },
                    placeholder = "Search word lists",
                    clearContentDescription = "Clear",
                )
            }
        }
        onNodeWithText("macbeth").performTextReplacement("macduff")
        assertEquals("macduff", text) // 输入回调接上
        onNodeWithContentDescription("Clear").performClick()
        assertTrue(cleared, "清空动作应触发回调")
        // F8：trailing 清空钮热区 ≥48dp（AppIconButton 自带 minimumInteractiveComponentSize；输入框 Xl 档不夹它）
        val bounds = onNodeWithContentDescription("Clear").fetchSemanticsNode().touchBoundsInRoot
        assertTrue(bounds.height >= 48f && bounds.width >= 48f, "清空钮触控热区应 ≥48dp，实际 ${bounds.width}x${bounds.height}")
    }

    @Test
    fun searchField_clearDisabledWhenEmpty() = runComposeUiTest {
        setContent {
            AppTheme(ThemeSettings()) {
                WordListSearchField(
                    value = "",
                    onValueChange = {},
                    onClear = {},
                    placeholder = "Search word lists",
                    clearContentDescription = "Clear",
                )
            }
        }
        onAllNodesWithText("Search word lists").assertCountEquals(1) // 占位可见
        // 常驻但禁用：热区稳定（无高度跳变）+ 诚实禁用态（review 🟠1/🟠4）
        onNodeWithContentDescription("Clear").assertIsNotEnabled()
    }

    @Test
    fun vocabularyListRow_mergesAndKeepsMoreReachable() = runComposeUiTest {
        var moreClicked = false
        setContent {
            AppTheme(ThemeSettings()) {
                VocabularyListRow(
                    title = "JLPT N5 core",
                    sourceLabel = "Curated",
                    wordCountLabel = "240 words",
                    progressLabel = "42 learned · 18 mastered",
                    dueLabel = "Due: 12",
                    onClick = {},
                    more = {
                        top.guangyiliushan.rebecca.design.components.AppIconButton(
                            onClick = { moreClicked = true },
                            icon = { Text("…") },
                            contentDescription = "More",
                        )
                    },
                )
            }
        }
        onNodeWithText("JLPT N5 core").assertExists()
        onNodeWithContentDescription("More").performClick() // More 与整行点击相互独立可达
        assertTrue(moreClicked)
        // 合并朗读：整行语义节点应同时包含 title/source/词数/进度/due 五段（未来给 Badge 加 clearAndSetSemantics 会破坏它）
        val rowNode = onNodeWithText("JLPT N5 core").fetchSemanticsNode()
        val mergedTexts = rowNode.config.getOrNull(SemanticsProperties.Text) ?: emptyList()
        val joined = mergedTexts.joinToString(" ") { it.text }
        assertTrue(
            listOf("Curated", "240 words", "42 learned", "Due: 12").all { joined.contains(it) },
            "整行应合并朗读全部字段，实际：$joined",
        )
    }

    @Test
    fun activeListCard_practiceIsPrimaryAction() = runComposeUiTest {
        var practiced = false
        var changed = false
        setContent {
            AppTheme(ThemeSettings()) {
                ActiveListCard(
                    title = "Adaptive list",
                    subtitle = "Adapts to your level.",
                    countsLine = "240 words · 12 due · 8 new",
                    practiceLabel = "Practice",
                    changeLabel = "Change",
                    onPractice = { practiced = true },
                    onChangeList = { changed = true },
                )
            }
        }
        onNodeWithText("Practice").performClick()
        assertTrue(practiced)
        onNodeWithText("Change").performClick()
        assertTrue(changed)
    }

    @Test
    fun masterDetailPane_compactRendersListOnly() = runComposeUiTest {
        setContent {
            AppTheme(ThemeSettings()) {
                CompositionLocalProvider(LocalLayoutDensity provides LayoutDensity.Compact) {
                    MasterDetailPane(
                        list = { Text("LIST") },
                        detail = { Text("DETAIL") },
                    )
                }
            }
        }
        onNodeWithText("LIST").assertExists()
        onAllNodesWithText("DETAIL").assertCountEquals(0) // Compact 单栏（详情走压栈）
    }

    @Test
    fun masterDetailPane_regularWithDetailRendersBoth() = runComposeUiTest {
        setContent {
            AppTheme(ThemeSettings()) {
                CompositionLocalProvider(LocalLayoutDensity provides LayoutDensity.Regular) {
                    MasterDetailPane(
                        list = { Text("LIST") },
                        detail = { Text("DETAIL") },
                    )
                }
            }
        }
        onNodeWithText("LIST").assertExists()
        onNodeWithText("DETAIL").assertExists() // Regular 双栏同屏
    }

    @Test
    fun masterDetailPane_regularWithoutDetailRendersListOnly() = runComposeUiTest {
        setContent {
            AppTheme(ThemeSettings()) {
                CompositionLocalProvider(LocalLayoutDensity provides LayoutDensity.Regular) {
                    MasterDetailPane(list = { Text("LIST") })
                }
            }
        }
        onNodeWithText("LIST").assertExists()
    }

    @Test
    fun filterSheet_rendersGroupsAndActions() = runComposeUiTest {
        var applied = false
        var reset = false
        setContent {
            AppTheme(ThemeSettings()) {
                ListFilterSheet(
                    open = true,
                    title = "Filters",
                    groups = listOf(
                        FilterGroupUi(
                            title = "Source",
                            mode = AppChipGroupMode.Multiple,
                            options = listOf(FilterOptionUi("Personal", true, {})),
                        ),
                    ),
                    applyLabel = "Apply",
                    resetLabel = "Reset",
                    onApply = { applied = true },
                    onReset = { reset = true },
                    onDismiss = {},
                )
            }
        }
        onNodeWithText("Filters").assertExists()
        onNodeWithText("Source").assertExists()
        onNodeWithText("Personal").assertExists()
        onNodeWithText("Apply").performClick()
        assertTrue(applied)
        onNodeWithText("Reset").performClick()
        assertTrue(reset)
    }

    @Test
    fun cardSwipeStack_settledAnchorTriggersDecision() = runComposeUiTest {
        // state 注入驱动 settledValue（不经真实拖拽注入即可锁定组件唯一契约：落点→判定→回中位）
        val state = AnchoredDraggableState(SwipeAnchor.Center)
        var lastDecision: Boolean? = null
        setContent {
            AppTheme(ThemeSettings()) {
                CardSwipeStack(
                    onDecision = { lastDecision = it },
                    state = state,
                ) {
                    Text("headword")
                }
            }
        }
        state.snapTo(SwipeAnchor.Right) // runComposeUiTest 的 body 是协程作用域
        waitUntil(conditionDescription = "right decision fires") { lastDecision != null }
        assertEquals(true, lastDecision, "右锚点落点应判定为认识")
        assertEquals(SwipeAnchor.Center, state.currentValue, "判定后应回中位")
        state.snapTo(SwipeAnchor.Left)
        waitUntil(conditionDescription = "left decision fires") { lastDecision == false }
        assertEquals(false, lastDecision, "左锚点落点应判定为不认识")
    }
}
