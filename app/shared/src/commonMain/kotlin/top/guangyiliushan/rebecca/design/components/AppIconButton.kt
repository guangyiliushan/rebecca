package top.guangyiliushan.rebecca.design.components

import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import org.jetbrains.compose.resources.stringResource
import rebecca.app.shared.generated.resources.Res
import rebecca.app.shared.generated.resources.state_loading
import top.guangyiliushan.rebecca.design.theme.AppTheme
import top.guangyiliushan.rebecca.design.tokens.AppControlHeight

/** AppIconButton 尺寸（视觉 = AppControlHeight 档）。 */
enum class AppIconButtonSize(val height: Dp) {
    Sm(AppControlHeight.Sm),
    Md(AppControlHeight.Md),
    Lg(AppControlHeight.Lg),
}

/**
 * 图标按钮（frontend-design-system v0.3 §6.5）。contentDescription 必填（F8：来自 catalog）→
 * 应用到语义树（读屏标签）。loading=true → 禁交互 + spinner（role=status）。
 */
@Composable
fun AppIconButton(
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    contentDescription: String,
    modifier: Modifier = Modifier,
    size: AppIconButtonSize = AppIconButtonSize.Md,
    enabled: Boolean = true,
    loading: Boolean = false,
) {
    val colors = AppTheme.colors
    IconButton(
        onClick = onClick,
        // 顺序：minimumInteractiveComponentSize 在外（min 48dp 热区）→ requiredHeight 在内（视觉）
        modifier = modifier
            .minimumInteractiveComponentSize()
            .requiredHeight(size.height)
            .semantics { this.contentDescription = contentDescription },
        enabled = enabled && !loading,
        colors = IconButtonDefaults.iconButtonColors(contentColor = colors.onSurface),
    ) {
        if (loading) {
            AppSpinner(size = AppSpinnerSize.Sm, contentDescription = stringResource(Res.string.state_loading))
        } else {
            icon()
        }
    }
}

@PreviewLightDark
@PreviewFontScale
@Composable
private fun AppIconButtonPreview() {
    AppTheme {
        AppIconButton(onClick = {}, icon = { androidx.compose.material3.Text("✕") }, contentDescription = "close")
    }
}
