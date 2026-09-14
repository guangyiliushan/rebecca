package top.guangyiliushan.rebecca.design.patterns

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import top.guangyiliushan.rebecca.design.components.AppButton
import top.guangyiliushan.rebecca.design.components.AppButtonVariant
import top.guangyiliushan.rebecca.design.components.AppCard
import top.guangyiliushan.rebecca.design.theme.AppTheme
import top.guangyiliushan.rebecca.design.theme.ThemeSettings

/** 结算指标（value/label 均调用方 catalog 文案，R6）。 */
data class SummaryMetric(
    val value: String,
    val label: String,
)

/**
 * 会话结算卡（0.1.2 抽出，R-D6）：答题会话与卡片会话共用同一结算形态，不建第二套结算屏。
 * title + 指标行（等宽居中）+ 主/次行动；文案全由调用方给（R6）。
 */
@Composable
fun SessionSummaryCard(
    title: String,
    metrics: List<SummaryMetric>,
    primaryLabel: String,
    onPrimary: () -> Unit,
    modifier: Modifier = Modifier,
    secondaryLabel: String? = null,
    onSecondary: (() -> Unit)? = null,
) {
    val spacing = AppTheme.spacing
    val colors = AppTheme.colors
    AppCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(spacing.md),
            verticalArrangement = Arrangement.spacedBy(spacing.md),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = colors.onSurface,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
            ) {
                metrics.forEach { metric ->
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(spacing.xxs),
                    ) {
                        Text(
                            text = metric.value,
                            style = MaterialTheme.typography.titleLarge,
                            color = colors.onSurface,
                        )
                        Text(
                            text = metric.label,
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.onMuted,
                        )
                    }
                }
            }
            AppButton(
                text = primaryLabel,
                onClick = onPrimary,
                modifier = Modifier.fillMaxWidth(),
            )
            if (secondaryLabel != null && onSecondary != null) {
                AppButton(
                    text = secondaryLabel,
                    onClick = onSecondary,
                    modifier = Modifier.fillMaxWidth(),
                    variant = AppButtonVariant.Ghost,
                )
            }
        }
    }
}

@PreviewLightDark
@PreviewFontScale
@Composable
private fun SessionSummaryCardPreview() {
    AppTheme(ThemeSettings()) {
        SessionSummaryCard(
            title = "Session complete",
            metrics = listOf(
                SummaryMetric("75%", "Accuracy"),
                SummaryMetric("42 s", "Time"),
            ),
            primaryLabel = "Practice again",
            onPrimary = {},
            secondaryLabel = "Back to hub",
            onSecondary = {},
        )
    }
}
