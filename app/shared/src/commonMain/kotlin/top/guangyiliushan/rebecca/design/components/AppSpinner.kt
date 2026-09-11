package top.guangyiliushan.rebecca.design.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import top.guangyiliushan.rebecca.design.theme.AppTheme
import top.guangyiliushan.rebecca.design.tokens.AppIconSize

/** AppSpinner 尺寸（= AppIconSize 档）。 */
enum class AppSpinnerSize(val diameter: Dp) {
    Xs(AppIconSize.Xs),
    Sm(AppIconSize.Sm),
    Md(AppIconSize.Md),
    Lg(AppIconSize.Lg),
}

/**
 * 转圈指示（frontend-design-system v0.3 §6.5；对应官方 spinner.tsx）。
 * role=status + contentDescription 来自 catalog（F6/F15）；liveRegion=Polite（§10.6）。
 */
@Composable
fun AppSpinner(
    contentDescription: String,
    modifier: Modifier = Modifier,
    size: AppSpinnerSize = AppSpinnerSize.Sm,  // 官方 spinner 默认 size-4(16)
) {
    val colors = AppTheme.colors
    CircularProgressIndicator(
        modifier = modifier
            .then(
                Modifier.semantics {
                    this.contentDescription = contentDescription
                    liveRegion = LiveRegionMode.Polite
                },
            )
            .size(size.diameter),
        color = colors.primary,
        trackColor = colors.muted,
    )
}

@PreviewLightDark
@PreviewFontScale
@Composable
private fun AppSpinnerPreview() {
    AppTheme {
        AppSpinner(contentDescription = "Loading")
    }
}
