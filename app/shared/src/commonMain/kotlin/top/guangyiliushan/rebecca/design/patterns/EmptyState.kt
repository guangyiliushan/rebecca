package top.guangyiliushan.rebecca.design.patterns

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import top.guangyiliushan.rebecca.design.theme.AppTheme
import top.guangyiliushan.rebecca.design.theme.ThemeSettings
import top.guangyiliushan.rebecca.design.tokens.AppIconSize

/**
 * 空态（frontend-design-system v0.3 §6.6）：图标 + 标题 + 描述 + 可选动作。
 * 全部文案参数化（R6）；图标纯装饰（clearAndSetSemantics，§10.2）。
 * patterns 目录在 showcase 门禁之外，与 AppPlaceholderScreen 同列。
 */
@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null,
) {
    val spacing = AppTheme.spacing
    Column(
        modifier = modifier.padding(spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacing.xs),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier
                .size(AppIconSize.Xl)
                .clearAndSetSemantics { },   // 纯装饰，不参与朗读
            tint = AppTheme.colors.onMuted,
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = AppTheme.colors.onSurface,
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = AppTheme.colors.onMuted,
        )
        if (action != null) action()
    }
}

@PreviewLightDark
@PreviewFontScale
@Composable
private fun EmptyStatePreview() {
    AppTheme(ThemeSettings()) {
        EmptyState(
            icon = Icons.Filled.Star,
            title = "Learning trends",
            description = "Charts arrive in a later version.",
        )
    }
}
