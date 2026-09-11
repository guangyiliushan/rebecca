package top.guangyiliushan.rebecca.design.overlays

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import top.guangyiliushan.rebecca.design.components.AppButton
import top.guangyiliushan.rebecca.design.components.AppButtonVariant
import top.guangyiliushan.rebecca.design.theme.AppTheme
import top.guangyiliushan.rebecca.design.tokens.AppOverlayWidth

/** AppAlertDialog 尺寸（官方 alert-dialog size: default/sm）。 */
enum class AppAlertDialogSize(val width: Dp) {
    Xs(AppOverlayWidth.Xs),
    Md(AppOverlayWidth.Md),
}

/**
 * 确认对话框（frontend-design-system v0.3 §6.5；规格页 05，决策 D2）。
 * 语义差异（vs AppDialog）：**不可点外关闭**（dismissOnClickOutside=false 固定）、
 * 必有 Action/Cancel、遵循 alertdialog pattern。
 * 底座 M3 AlertDialog（内部转 BasicAlertDialog，自带 paneTitle 语义）。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppAlertDialog(
    open: Boolean,
    onDismiss: () -> Unit,
    title: @Composable () -> Unit,
    confirmLabel: String,
    onConfirm: () -> Unit,
    dismissLabel: String,
    modifier: Modifier = Modifier,
    text: (@Composable () -> Unit)? = null,
    media: (@Composable () -> Unit)? = null,
    size: AppAlertDialogSize = AppAlertDialogSize.Md,
) {
    if (!open) return

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier.sizeIn(maxWidth = size.width),
        confirmButton = {
            AppButton(
                text = confirmLabel,
                onClick = onConfirm,
                variant = AppButtonVariant.Primary,
            )
        },
        dismissButton = {
            AppButton(
                text = dismissLabel,
                onClick = onDismiss,
                variant = AppButtonVariant.Ghost,
            )
        },
        icon = media,
        title = title,
        text = text,
        shape = RoundedCornerShape(AppTheme.radius.large),
        containerColor = AppTheme.colors.surface,
        titleContentColor = AppTheme.colors.onSurface,
        textContentColor = AppTheme.colors.onMuted,
        properties = androidx.compose.ui.window.DialogProperties(
            dismissOnClickOutside = false,  // 不可点外关闭（alertdialog pattern）
            dismissOnBackPress = true,
        ),
    )
}

@PreviewLightDark
@PreviewFontScale
@Composable
private fun AppAlertDialogPreview() {
    AppTheme {
        AppAlertDialog(
            open = true,
            onDismiss = {},
            title = { androidx.compose.material3.Text("Delete list?") },
            text = { androidx.compose.material3.Text("This cannot be undone.") },
            confirmLabel = "Delete",
            onConfirm = {},
            dismissLabel = "Cancel",
        )
    }
}
