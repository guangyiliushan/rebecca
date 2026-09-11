package top.guangyiliushan.rebecca.design.components

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import top.guangyiliushan.rebecca.design.theme.AppTheme

/** AppProgress 变体（R2；规格页 07：Linear/Circular）。 */
enum class AppProgressVariant { Linear, Circular }

/**
 * 进度条（frontend-design-system v0.3 §6.5；规格页 07-appprogress）。
 * progress=null = 不确定态；label 来自 catalog（F6），成为 stateDescription（F15，读屏专用，无视觉文本）。
 * M3 1.12 确定态默认画 track gap 与 stop indicator → 传 gapSize=0.dp + drawStopIndicator={} 还原 shadcn 视觉；
 * 不确定态重载无这两个参数（M3 API 限制）→ 接受默认视觉（已知差异）。
 * 第三态 loading（官方 data-state=loading）0.1.0 不实现（已知差异）。
 */
@Composable
fun AppProgress(
    modifier: Modifier = Modifier,
    variant: AppProgressVariant = AppProgressVariant.Linear,
    progress: Float? = null,
    max: Float = 1f,
    label: String? = null,
) {
    val colors = AppTheme.colors
    val semanticsModifier: Modifier = if (label != null) {
        Modifier.semantics { stateDescription = label }
    } else Modifier

    when (variant) {
        AppProgressVariant.Linear -> if (progress == null) {
            LinearProgressIndicator(
                modifier = modifier.then(semanticsModifier),
                color = colors.primary,
                trackColor = colors.muted,
            )
        } else {
            LinearProgressIndicator(
                progress = { (progress / max).coerceIn(0f, 1f) },
                modifier = modifier.then(semanticsModifier),
                color = colors.primary,
                trackColor = colors.muted,
                gapSize = 0.dp,
                drawStopIndicator = {},
            )
        }
        AppProgressVariant.Circular -> if (progress == null) {
            CircularProgressIndicator(
                modifier = modifier.then(semanticsModifier),
                color = colors.primary,
                trackColor = colors.muted,
            )
        } else {
            CircularProgressIndicator(
                progress = { (progress / max).coerceIn(0f, 1f) },
                modifier = modifier.then(semanticsModifier),
                color = colors.primary,
                trackColor = colors.muted,
            )
        }
    }
}

@PreviewLightDark
@PreviewFontScale
@Composable
private fun AppProgressPreview() {
    AppTheme {
        androidx.compose.foundation.layout.Column {
            AppProgress(progress = 0.5f, label = "50%")
            AppProgress(progress = null)
            AppProgress(variant = AppProgressVariant.Circular, progress = 0.5f)
        }
    }
}
