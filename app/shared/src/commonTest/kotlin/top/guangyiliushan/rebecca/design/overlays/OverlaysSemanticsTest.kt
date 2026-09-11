package top.guangyiliushan.rebecca.design.overlays

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.input.key.Key
import top.guangyiliushan.rebecca.design.theme.AppTheme
import top.guangyiliushan.rebecca.design.theme.ThemeSettings
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * overlays 语义/行为断言（F15/F17；规格页 05/06）。
 * Esc 关闭走 onPreviewKeyEvent 桥接（08-poc-results §0.6 实证：key-inject 流不触发 back handler）。
 */
@OptIn(ExperimentalTestApi::class)
class OverlaysSemanticsTest {
    @Test
    fun appDialog_escDismisses() = runComposeUiTest {
        var dismissed = false
        setContent {
            AppTheme(ThemeSettings()) {
                AppDialog(open = true, onDismiss = { dismissed = true }) {
                    AppDialogHeader(title = { androidx.compose.material3.Text("Title") })
                    AppDialogBody { androidx.compose.material3.Text("Body") }
                }
            }
        }
        onNodeWithText("Title").assertExists()
        onNodeWithText("Body").performKeyInput { pressKey(Key.Escape) }
        waitForIdle()
        assertTrue(dismissed, "Esc 应触发 onDismiss（onPreviewKeyEvent 桥接）")
    }

    @Test
    fun appAlertDialog_buttonsWorkAndTextPresent() = runComposeUiTest {
        var confirmed = false
        var dismissed = false
        setContent {
            AppTheme(ThemeSettings()) {
                AppAlertDialog(
                    open = true,
                    onDismiss = { dismissed = true },
                    title = { androidx.compose.material3.Text("Delete?") },
                    confirmLabel = "Delete",
                    onConfirm = { confirmed = true },
                    dismissLabel = "Cancel",
                )
            }
        }
        onNodeWithText("Delete?").assertExists()
        onNodeWithText("Delete").performClick()
        assertEquals(true, confirmed)
        assertEquals(false, dismissed)
    }

    @Test
    fun appSheet_contentInteractable() = runComposeUiTest {
        var clicked = 0
        setContent {
            AppTheme(ThemeSettings()) {
                AppSheet(open = true, onDismiss = {}) {
                    AppSheetHeader(title = { androidx.compose.material3.Text("Sheet") })
                    AppSheetFooter {
                        top.guangyiliushan.rebecca.design.components.AppButton(
                            text = "Action",
                            onClick = { clicked++ },
                        )
                    }
                }
            }
        }
        onNodeWithText("Sheet").assertExists()
        onNodeWithText("Action").performClick()
        waitForIdle()
        assertEquals(1, clicked)
    }
}
