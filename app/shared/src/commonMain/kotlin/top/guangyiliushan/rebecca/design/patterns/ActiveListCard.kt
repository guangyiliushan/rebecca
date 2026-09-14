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
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import top.guangyiliushan.rebecca.design.components.AppButton
import top.guangyiliushan.rebecca.design.components.AppButtonSize
import top.guangyiliushan.rebecca.design.components.AppButtonVariant
import top.guangyiliushan.rebecca.design.components.AppCard
import top.guangyiliushan.rebecca.design.theme.AppTheme
import top.guangyiliushan.rebecca.design.theme.ThemeSettings

/**
 * 活跃词单摘要卡（09 spec §Active list summary）：与 Study 焦点卡的词单选择器同源偏好。
 * Practice 是唯一主行动（Lg 视觉——09 spec「不要大 hero」，故非 Study 主 CTA 的 Xl）；Change 为次级 Ghost。
 * 文案由调用方经 catalog 给（R6）。
 */
@Composable
fun ActiveListCard(
    title: String,
    subtitle: String,
    countsLine: String,
    practiceLabel: String,
    changeLabel: String,
    onPractice: () -> Unit,
    onChangeList: () -> Unit,
    modifier: Modifier = Modifier,
    sectionTitle: String? = null,
) {
    val spacing = AppTheme.spacing
    val colors = AppTheme.colors
    AppCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(spacing.md),
            verticalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            if (sectionTitle != null) {
                Text(
                    text = sectionTitle,
                    style = MaterialTheme.typography.labelMedium,
                    color = colors.onMuted,
                    modifier = Modifier.semantics { heading() },
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = colors.onSurface,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = colors.onMuted,
            )
            Text(
                text = countsLine,
                style = MaterialTheme.typography.bodySmall,
                color = colors.onMuted,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
            ) {
                AppButton(
                    text = practiceLabel,
                    onClick = onPractice,
                    modifier = Modifier.weight(1f),
                    size = AppButtonSize.Lg,
                )
                AppButton(
                    text = changeLabel,
                    onClick = onChangeList,
                    variant = AppButtonVariant.Ghost,
                )
            }
        }
    }
}

@PreviewLightDark
@PreviewFontScale
@Composable
private fun ActiveListCardPreview() {
    AppTheme(ThemeSettings()) {
        ActiveListCard(
            sectionTitle = "Active word list",
            title = "Adaptive list",
            subtitle = "Adapts to your level, reviews, and new words.",
            countsLine = "240 words · 12 due · 8 new",
            practiceLabel = "Practice",
            changeLabel = "Change",
            onPractice = {},
            onChangeList = {},
        )
    }
}
