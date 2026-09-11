package top.guangyiliushan.rebecca.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import top.guangyiliushan.rebecca.design.theme.AppTheme

/** AppBadge 变体（R2；对齐 shadcn badge.tsx 6 variant）。纯展示，不可点。 */
enum class AppBadgeVariant { Default, Secondary, Destructive, Outline, Ghost, Link }

/**
 * 徽标（frontend-design-system v0.3 §6.5；规格页 03-appchip §1.1）。
 * 纯展示：无 onClick、无交互语义；图标/文字由调用方提供（content 为 RowScope）。
 * contentColor 经 LocalContentColor 下发（变体前景色生效）。
 * 已知差异：Link 与 Ghost 视觉同源（无下划线，text-sm 内容样式归调用方）——登记 COMPONENTS.md。
 */
@Composable
fun AppBadge(
    modifier: Modifier = Modifier,
    variant: AppBadgeVariant = AppBadgeVariant.Default,
    content: @Composable RowScope.() -> Unit,
) {
    val colors = AppTheme.colors
    val spacing = AppTheme.spacing
    val fullShape = RoundedCornerShape(percent = AppTheme.radius.full)

    val container: Color = when (variant) {
        AppBadgeVariant.Default -> colors.primary
        AppBadgeVariant.Secondary -> colors.accent
        AppBadgeVariant.Destructive -> colors.destructive
        AppBadgeVariant.Outline, AppBadgeVariant.Ghost, AppBadgeVariant.Link -> Color.Transparent
    }
    val contentColor: Color = when (variant) {
        AppBadgeVariant.Default -> colors.onPrimary
        AppBadgeVariant.Secondary -> colors.onAccent
        AppBadgeVariant.Destructive -> colors.onDestructive
        AppBadgeVariant.Outline, AppBadgeVariant.Ghost, AppBadgeVariant.Link -> colors.primary
    }

    CompositionLocalProvider(LocalContentColor provides contentColor) {
        Row(
            modifier = modifier
                .background(container, fullShape)
                .then(
                    if (variant == AppBadgeVariant.Outline) {
                        Modifier.border(1.dp, contentColor, fullShape)
                    } else Modifier,
                )
                .padding(horizontal = spacing.sm, vertical = spacing.xxs),
            content = content,
        )
    }
}

@PreviewLightDark
@PreviewFontScale
@Composable
private fun AppBadgePreview() {
    AppTheme {
        Row {
            AppBadge { androidx.compose.material3.Text("New") }
            AppBadge(variant = AppBadgeVariant.Outline) { androidx.compose.material3.Text("Outline") }
        }
    }
}
