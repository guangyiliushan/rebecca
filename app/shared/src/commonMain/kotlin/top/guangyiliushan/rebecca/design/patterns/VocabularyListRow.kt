package top.guangyiliushan.rebecca.design.patterns

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import top.guangyiliushan.rebecca.design.components.AppBadge
import top.guangyiliushan.rebecca.design.components.AppBadgeVariant
import top.guangyiliushan.rebecca.design.components.AppListRow
import top.guangyiliushan.rebecca.design.theme.AppTheme
import top.guangyiliushan.rebecca.design.theme.ThemeSettings

/**
 * 词单行（09 spec §List row）：title + source 徽章 + 词数/掌握计数/due + More。
 * 整行是可点区（打开详情），合并朗读一条（AppListRow mergeDescendants）；More 为独立 48dp 动作。
 * 计数文案由调用方拼好（R6；复数在 feature 层经 catalog 处理）。
 */
@Composable
fun VocabularyListRow(
    title: String,
    sourceLabel: String,
    wordCountLabel: String,
    progressLabel: String,
    dueLabel: String?,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    more: (@Composable () -> Unit)? = null,
) {
    val spacing = AppTheme.spacing
    val colors = AppTheme.colors
    AppListRow(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = colors.onSurface,
            )
        },
        description = {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.xxs)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(spacing.xs),
                ) {
                    AppBadge(variant = AppBadgeVariant.Outline) {
                        Text(text = sourceLabel, style = MaterialTheme.typography.labelMedium)
                    }
                    Text(
                        text = wordCountLabel,
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.onMuted,
                    )
                }
                Text(
                    text = progressLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.onMuted,
                )
                if (dueLabel != null) {
                    Text(
                        text = dueLabel,
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.warning,
                    )
                }
            }
        },
        trailing = more,
    )
}

@PreviewLightDark
@PreviewFontScale
@Composable
private fun VocabularyListRowPreview() {
    AppTheme(ThemeSettings()) {
        VocabularyListRow(
            title = "JLPT N5 core",
            sourceLabel = "Curated",
            wordCountLabel = "240 words",
            progressLabel = "42 learned · 18 mastered",
            dueLabel = "Due: 12",
            onClick = {},
        )
    }
}
