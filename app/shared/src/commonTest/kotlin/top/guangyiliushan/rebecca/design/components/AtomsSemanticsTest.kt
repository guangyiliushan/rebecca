package top.guangyiliushan.rebecca.design.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import top.guangyiliushan.rebecca.design.theme.AppTheme
import top.guangyiliushan.rebecca.design.theme.ThemeSettings
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * atoms 语义断言（F15/F8；DoD 3/4）。规格页 03-atom-mapping/10 §4。
 * 触控热区断言：语义树 touchBoundsInRoot 宽高 ≥48dp（minimumInteractiveComponentSize）。
 */
@OptIn(ExperimentalTestApi::class)
class AtomsSemanticsTest {
    private fun hasRole(role: Role) = SemanticsMatcher("role=$role") {
        it.config.getOrElseNullable(SemanticsProperties.Role) { null } == role
    }

    private fun hasStateDescription(text: String) = SemanticsMatcher("stateDescription=$text") {
        it.config.getOrElseNullable(SemanticsProperties.StateDescription) { null } == text
    }

    @Test
    fun appButton_hasButtonRole_andClick() = runComposeUiTest {
        var clicked = 0
        setContent {
            AppTheme(ThemeSettings()) {
                AppButton(text = "Save", onClick = { clicked++ })
            }
        }
        onNodeWithText("Save").assert(hasRole(Role.Button))
        onNodeWithText("Save").performClick()
        assertEquals(1, clicked)
    }

    @Test
    fun appButton_disabledHasDisabledSemantics() = runComposeUiTest {
        setContent {
            AppTheme(ThemeSettings()) {
                AppButton(text = "Save", onClick = {}, enabled = false)
            }
        }
        onNodeWithText("Save").assertIsNotEnabled()
    }

    @Test
    fun appButton_loadingDisablesAndSpinnerPresent() = runComposeUiTest {
        setContent {
            AppTheme(ThemeSettings()) {
                AppButton(text = "Save", onClick = {}, loading = true)
            }
        }
        onNodeWithContentDescription("Loading").assertExists()
        onNodeWithText("Save").assertIsNotEnabled()
    }

    @Test
    fun appButton_touchTargetAtLeast48dp() = runComposeUiTest {
        setContent {
            AppTheme(ThemeSettings()) {
                AppButton(text = "Save", onClick = {})
            }
        }
        // F8：touchBoundsInRoot（官方触控热区 bounds，含 minimumInteractiveComponentSize 扩出的区域）
        val node = onNodeWithText("Save").fetchSemanticsNode()
        val touch = node.touchBoundsInRoot
        val minPx = 48.dp.value * node.layoutInfo.density.density
        assertTrue(touch.width >= minPx, "触控宽度 ${touch.width} < 48dp")
        assertTrue(touch.height >= minPx, "触控高度 ${touch.height} < 48dp")
    }

    @Test
    fun appIconButton_contentDescriptionApplied() = runComposeUiTest {
        setContent {
            AppTheme(ThemeSettings()) {
                AppIconButton(onClick = {}, icon = { Text("✕") }, contentDescription = "Close dialog")
            }
        }
        onNodeWithContentDescription("Close dialog").assertExists()
    }

    @Test
    fun appCard_clickableHasButtonRole() = runComposeUiTest {
        setContent {
            AppTheme(ThemeSettings()) {
                AppCard(onClick = {}) { AppCardContent { Text("Card content") } }
            }
        }
        onNodeWithText("Card content").assert(hasRole(Role.Button))
    }

    @Test
    fun appListRow_clickableHasButtonRole_selectedOnlyWhenSelected() = runComposeUiTest {
        setContent {
            AppTheme(ThemeSettings()) {
                AppListRow(
                    title = { Text("Selected row") },
                    selected = true,
                    onClick = {},
                )
                AppListRow(
                    title = { Text("Plain row") },
                    onClick = {},
                )
            }
        }
        onNodeWithText("Selected row").assert(hasRole(Role.Button))
            .assert(hasStateDescription("Selected"))
        onNodeWithText("Plain row").assert(hasRole(Role.Button))
        // 未选中行不播报 "Not selected"
        onNodeWithText("Plain row").fetchSemanticsNode().also { node ->
            assertEquals(
                null,
                node.config.getOrElseNullable(SemanticsProperties.StateDescription) { null },
                "未选中行不应有 stateDescription",
            )
        }
    }

    @Test
    fun appChip_stateDescriptionFromCatalog() = runComposeUiTest {
        setContent {
            AppTheme(ThemeSettings()) {
                AppChip(label = { Text("Verb") }, selected = true, onClick = {})
            }
        }
        onNodeWithText("Verb").assert(hasStateDescription("Selected"))
    }

    @Test
    fun appFieldError_dedupAndLiveRegionOnContainer() = runComposeUiTest {
        setContent {
            AppTheme(ThemeSettings()) {
                AppField(errors = listOf("Required", "Required")) {
                    AppInput(value = "", onValueChange = {})
                }
            }
        }
        // 去重后恰好一条错误
        onAllNodesWithText("Required").assertCountEquals(1)
        // liveRegion=Assertive 在 AppFieldError 容器节点（含 "Required" 文本为后代）
        val liveNodes = onAllNodes(
            SemanticsMatcher("liveRegion=Assertive") {
                it.config.getOrElseNullable(SemanticsProperties.LiveRegion) { null } == LiveRegionMode.Assertive
            },
        ).fetchSemanticsNodes()
        assertTrue(liveNodes.isNotEmpty(), "AppFieldError 容器应有 liveRegion=Assertive")
    }

    @Test
    fun appFieldHorizontal_keepsErrorsAndDescription() = runComposeUiTest {
        setContent {
            AppTheme(ThemeSettings()) {
                AppField(
                    label = { Text("Label") },
                    description = { Text("Hint text") },
                    errors = listOf("Too short"),
                    orientation = AppFieldOrientation.Horizontal,
                ) {
                    AppInput(value = "", onValueChange = {})
                }
            }
        }
        onNodeWithText("Hint text").assertExists()
        onNodeWithText("Too short").assertExists()
    }

    @Test
    fun appInput_invalidErrorFromFieldState() = runComposeUiTest {
        setContent {
            AppTheme(ThemeSettings()) {
                AppField(invalid = true, errors = listOf("Name is required")) {
                    AppInput(value = "x", onValueChange = {})
                }
            }
        }
        val node = onNodeWithText("x", substring = true).fetchSemanticsNode()
        assertEquals(
            "Name is required",
            node.config.getOrElseNullable(SemanticsProperties.Error) { null },
            "invalid 时 error() 文案应来自 AppFieldState.errors 首条",
        )
    }

    @Test
    fun appSpinner_contentDescriptionPresent() = runComposeUiTest {
        setContent {
            AppTheme(ThemeSettings()) {
                AppSpinner(contentDescription = "Loading words")
            }
        }
        onNodeWithContentDescription("Loading words").assertExists()
    }

    @Test
    fun appProgress_determinateHasRangeInfoAndLabel() = runComposeUiTest {
        setContent {
            AppTheme(ThemeSettings()) {
                AppProgress(progress = 0.5f, label = "50%")
            }
        }
        val nodes = onAllNodes(hasProgressBarRangeInfo(androidx.compose.ui.semantics.ProgressBarRangeInfo(0.5f, 0f..1f)))
            .fetchSemanticsNodes()
        assertTrue(nodes.isNotEmpty(), "确定态应有 progressBarRangeInfo")
        val labeled = onAllNodes(hasStateDescription("50%")).fetchSemanticsNodes()
        assertTrue(labeled.isNotEmpty(), "label 应成为 stateDescription")
    }
}
