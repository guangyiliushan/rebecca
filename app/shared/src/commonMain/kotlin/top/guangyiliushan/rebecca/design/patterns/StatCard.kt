package top.guangyiliushan.rebecca.design.patterns

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import top.guangyiliushan.rebecca.design.components.AppCard
import top.guangyiliushan.rebecca.design.theme.AppTheme
import top.guangyiliushan.rebecca.design.theme.ThemeSettings

/**
 * 统计卡（frontend-design-system v0.3 §6.6）：AppCard 组装——数字 + 标签。
 * 文案由调用方经 catalog 给（R6）；trend 位 0.2.3 统计版接趋势，0.1.1 传 null。
 * patterns 目录在 showcase 门禁（components/overlays/scaffold）之外，与 AppPlaceholderScreen 同列。
 */
@Composable
fun StatCard(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    trend: String? = null,
) {
    val spacing = AppTheme.spacing
    AppCard(modifier = modifier) {
        Column(
            modifier = Modifier.padding(spacing.sm),
            verticalArrangement = Arrangement.spacedBy(spacing.xxs),
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = AppTheme.colors.onSurface,
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = AppTheme.colors.onMuted,
            )
            if (trend != null) {
                Text(
                    text = trend,
                    style = MaterialTheme.typography.labelMedium,
                    color = AppTheme.colors.primary,
                )
            }
        }
    }
}

@PreviewLightDark
@PreviewFontScale
@Composable
private fun StatCardPreview() {
    AppTheme(ThemeSettings()) {
        StatCard(value = "7", label = "Due today", trend = "+2")
    }
}
