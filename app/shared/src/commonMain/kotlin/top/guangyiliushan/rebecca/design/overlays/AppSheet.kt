package top.guangyiliushan.rebecca.design.overlays

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import top.guangyiliushan.rebecca.design.components.AppButton
import top.guangyiliushan.rebecca.design.theme.AppTheme
import top.guangyiliushan.rebecca.design.tokens.AppOverlayWidth
import top.guangyiliushan.rebecca.design.tokens.AppScrim

/**
 * 底部动作面板（frontend-design-system v0.3 §6.5；规格页 06-appsheet，官方 sheet/drawer）。
 * 底座 ModalBottomSheet。**v1 只做 bottom**（side 变体 CMP 无原语，0.1.2 由侧栏承担）。
 * ⚠ 更正调研 06 页 SH-2 的误判：skiko actual 关的是 Dialog 层 scrim，但 commonMain
 * ModalBottomSheet **内部自带 Scrim 组件**（ModalBottomSheet.kt:152，颜色走 scrimColor 参数，
 * 默认黑 60%）——四端遮罩一致，**无需自盖**。showScrim=false 时传 Color.Transparent。
 * F17：showDragHandle=false 时必须提供显式关闭按钮（由调用方在 content 内放）。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSheet(
    open: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    showDragHandle: Boolean = true,
    dismissOnClickOutside: Boolean = true,
    dismissOnBackPress: Boolean = true,
    showScrim: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
) {
    if (!open) return
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier,   // modifier 透传（R4）
        sheetState = sheetState,
        sheetMaxWidth = AppOverlayWidth.Sm,
        dragHandle = if (showDragHandle) {
            { BottomSheetDefaults.DragHandle() }
        } else null,
        scrimColor = if (showScrim) AppScrim.color else Color.Transparent,
        containerColor = AppTheme.colors.surface,
        contentColor = AppTheme.colors.onSurface,
        properties = androidx.compose.material3.ModalBottomSheetProperties(
            shouldDismissOnBackPress = dismissOnBackPress,
            shouldDismissOnClickOutside = dismissOnClickOutside,
        ),
        content = content,
    )
}

/** Sheet 头：标题 + 可选描述（官方 SheetHeader）。 */
@Composable
fun AppSheetHeader(
    title: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier,
    description: (@Composable () -> Unit)? = null,
) {
    val spacing = AppTheme.spacing
    Column(
        modifier = modifier.fillMaxWidth().padding(horizontal = spacing.md, vertical = spacing.xs),
        verticalArrangement = Arrangement.spacedBy(spacing.xs),
    ) {
        title?.invoke()
        description?.invoke()
    }
}

/** Sheet 脚（官方 SheetFooter：mt-auto 动作区）。 */
@Composable
fun AppSheetFooter(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(horizontal = AppTheme.spacing.md, vertical = AppTheme.spacing.md),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
    ) {
        content()
    }
}

@PreviewLightDark
@PreviewFontScale
@Composable
private fun AppSheetPreview() {
    AppTheme {
        AppSheet(open = true, onDismiss = {}) {
            AppSheetHeader(title = { androidx.compose.material3.Text("Actions") })
            AppSheetFooter {
                AppButton(text = "Done", onClick = {})
            }
        }
    }
}
