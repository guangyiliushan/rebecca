package top.guangyiliushan.rebecca.design.patterns

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import top.guangyiliushan.rebecca.design.components.AppCard
import top.guangyiliushan.rebecca.design.theme.AppTheme
import top.guangyiliushan.rebecca.design.theme.ThemeSettings

/** 概览统计项（value/label 均为调用方 catalog 文案，R6）。 */
data class OverviewStat(
    val value: String,
    val label: String,
)

/**
 * 学习概览卡（ui-design-survey 08 §LearningOverviewCard 精简版；用户实测裁定：单张大卡，
 * 把连续学习 pipe + 今日进度（due/new）合并——此前分离排版不对称。周图表不在本组件）。
 * 布局：标题（连续天数由调用方拼好）+ 7 格 pipe（双编码：active=实心填充 / inactive=空心描边，WCAG 1.4.1）
 * + 汇总文本（pipe 的读屏文本替代）+ 统计行（等宽居中，左右填充中心对称）。
 * 数据纯传入（R6）：days 索引 0 = 最旧，末位 = 今天。
 * patterns 目录在 showcase 门禁之外（2026-09-12 裁定）；双 preview 注解仍为强制（§6.7）。
 */
@Composable
fun LearningOverviewCard(
    title: String,
    summary: String,
    days: List<Boolean>,
    stats: List<OverviewStat>,
    modifier: Modifier = Modifier,
) {
    val spacing = AppTheme.spacing
    val colors = AppTheme.colors
    AppCard(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(spacing.md),
            verticalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = colors.onSurface,
                modifier = Modifier.semantics { heading() }, // §10.2 卡标题 heading 语义
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clearAndSetSemantics { }, // pipe 纯视觉装饰；文本替代由下方 summary Text 承担（避免重复播报）
                horizontalArrangement = Arrangement.spacedBy(spacing.xs),
            ) {
                days.forEach { active ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(spacing.sm)
                            .background(
                                color = if (active) colors.primary else Color.Transparent,
                                shape = RoundedCornerShape(AppTheme.radius.extraSmall),
                            )
                            .then(
                                // 双编码（WCAG 1.4.1，05 spec）：active=实心填充，inactive=空心描边——不只靠颜色
                                if (active) Modifier else Modifier.border(1.dp, colors.border, RoundedCornerShape(AppTheme.radius.extraSmall)),
                            ),
                    )
                }
            }
            Text(
                text = summary,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.onMuted,
            )
            // 统计行：等宽居中（左右填充中心对称——用户实测裁定）
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
            ) {
                stats.forEach { stat ->
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(spacing.xxs),
                    ) {
                        Text(
                            text = stat.value,
                            style = MaterialTheme.typography.titleLarge,
                            color = colors.onSurface,
                        )
                        Text(
                            text = stat.label,
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.onMuted,
                        )
                    }
                }
            }
        }
    }
}

@PreviewLightDark
@PreviewFontScale
@Composable
private fun LearningOverviewCardPreview() {
    AppTheme(ThemeSettings()) {
        LearningOverviewCard(
            title = "Continuous learning: 4 days",
            summary = "Practiced on 4 of the last 7 days.",
            days = listOf(true, true, false, true, false, true, true),
            stats = listOf(
                OverviewStat(value = "7", label = "Due today"),
                OverviewStat(value = "8", label = "New words"),
            ),
        )
    }
}
