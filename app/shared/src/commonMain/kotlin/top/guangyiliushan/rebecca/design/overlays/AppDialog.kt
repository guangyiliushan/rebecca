package top.guangyiliushan.rebecca.design.overlays

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import top.guangyiliushan.rebecca.design.components.AppButton
import top.guangyiliushan.rebecca.design.components.AppButtonVariant
import top.guangyiliushan.rebecca.design.theme.AppTheme
import top.guangyiliushan.rebecca.design.tokens.AppOverlayWidth

/** AppDialog 尺寸（= AppOverlayWidth 档）。 */
enum class AppDialogSize(val width: Dp) {
    Xs(AppOverlayWidth.Xs),
    Md(AppOverlayWidth.Md),
}

/**
 * 对话框（frontend-design-system v0.3 §6.5；规格页 05-appdialog，官方 dialog.tsx）。
 * 底座 BasicAlertDialog（自带 paneTitle 语义 + 最小宽）；skiko 自带 scrim 黑 60%（不另盖）。
 * ⚠ Esc 实证（08-poc-results §0.6）：`dismissOnBackPress` 的 back-handler 在 key-inject 流不自动触发，
 * 故自接 `onPreviewKeyEvent`（Escape → onDismiss）+ 内容初始 focusable（焦点圈禁入口）。
 * 已知差异：无法拦截 outside-click 的确认交互（E3，CMP 无 onPointerDownOutside）。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDialog(
    open: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    dismissOnClickOutside: Boolean = true,
    dismissOnBackPress: Boolean = true,
    size: AppDialogSize = AppDialogSize.Md,
    content: @Composable ColumnScope.() -> Unit,
) {
    if (!open) return
    val focusRequester = remember { FocusRequester() }

    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier
            .sizeIn(maxWidth = size.width)
            .focusRequester(focusRequester)
            .focusable()
            .onPreviewKeyEvent { event ->
                if (dismissOnBackPress && event.type == KeyEventType.KeyUp && event.key == Key.Escape) {
                    onDismiss()
                    true
                } else false
            },
        properties = androidx.compose.ui.window.DialogProperties(
            dismissOnClickOutside = dismissOnClickOutside,
            dismissOnBackPress = dismissOnBackPress,
        ),
    ) {
        LaunchedEffect(Unit) { focusRequester.requestFocus() }
        Surface(
            shape = RoundedCornerShape(AppTheme.radius.large),
            color = AppTheme.colors.surface,
            contentColor = AppTheme.colors.onSurface,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(content = content)
        }
    }
}

/** 对话框头：标题 + 可选描述（官方 DialogHeader）。 */
@Composable
fun AppDialogHeader(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    description: (@Composable () -> Unit)? = null,
) {
    val spacing = AppTheme.spacing
    Column(
        modifier = modifier.padding(horizontal = spacing.lg, vertical = spacing.md),
        verticalArrangement = Arrangement.spacedBy(spacing.xs),
    ) {
        title()
        description?.invoke()
    }
}

/** 对话框正文（官方 DialogContent 区域）。 */
@Composable
fun AppDialogBody(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(modifier.padding(horizontal = AppTheme.spacing.lg), content = { content() })
}

/** 对话框脚（官方 DialogFooter：操作按钮区）。 */
@Composable
fun AppDialogFooter(
    modifier: Modifier = Modifier,
    confirm: (@Composable () -> Unit)? = null,
    dismiss: (@Composable () -> Unit)? = null,
) {
    val spacing = AppTheme.spacing
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = spacing.lg, vertical = spacing.md),
        horizontalArrangement = Arrangement.spacedBy(spacing.sm, androidx.compose.ui.Alignment.End),
    ) {
        dismiss?.invoke()
        confirm?.invoke()
    }
}

@PreviewLightDark
@PreviewFontScale
@Composable
private fun AppDialogPreview() {
    AppTheme {
        AppDialog(open = true, onDismiss = {}) {
            AppDialogHeader(title = { androidx.compose.material3.Text("Title") })
            AppDialogBody { androidx.compose.material3.Text("Body content") }
            AppDialogFooter(
                confirm = { AppButton(text = "Confirm", onClick = {}) },
                dismiss = { AppButton(text = "Cancel", variant = AppButtonVariant.Ghost, onClick = {}) },
            )
        }
    }
}
