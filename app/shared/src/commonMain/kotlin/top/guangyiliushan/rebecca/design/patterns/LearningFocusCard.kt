package top.guangyiliushan.rebecca.design.patterns

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import top.guangyiliushan.rebecca.design.components.AppBadge
import top.guangyiliushan.rebecca.design.components.AppBadgeVariant
import top.guangyiliushan.rebecca.design.components.AppButton
import top.guangyiliushan.rebecca.design.components.AppButtonSize
import top.guangyiliushan.rebecca.design.components.AppButtonVariant
import top.guangyiliushan.rebecca.design.components.AppCard
import top.guangyiliushan.rebecca.design.components.AppChip
import top.guangyiliushan.rebecca.design.components.AppProgress
import top.guangyiliushan.rebecca.design.components.AppProgressVariant
import top.guangyiliushan.rebecca.design.theme.AppTheme
import top.guangyiliushan.rebecca.design.theme.ThemeSettings

/** 焦点选项（展示值；领域枚举不进 design 层——feature 边界转换，ui-design-survey 04 §Proposed API）。 */
data class FocusOption(
    val label: String,
    val selected: Boolean,
    val onSelect: () -> Unit,
)

/**
 * 学习焦点卡（ui-design-survey 05 §LearningFocusCard / 08 §LearningFocusCard）：
 * 焦点标题 + Mixed 徽标（信息性非控件）+ 焦点选择 chips + 详情（准确率/目标/能力徽章）
 * + 进度环（AppProgress Circular；文本为主、环为辅）+ 词单行（Change 打开选择 sheet）+ 唯一主 CTA。
 * 占位焦点（Listening/Grammar）：CTA disabled + 原因 + "Practice vocabulary instead" 次级动作。
 * 全部字符串由调用方经 catalog 给（R6）；不内嵌卡片嵌套卡片；无窗口尺寸读取（F-L4）。
 */
@Composable
fun LearningFocusCard(
    title: String,
    badge: String,
    focusOptions: List<FocusOption>,
    accuracyLabel: String,
    goalLabel: String,
    onEditGoal: () -> Unit,
    capabilityLabels: List<String>,
    placeholderReason: String?,
    progress: Float?,
    progressLabel: String,
    wordListLabel: String,
    wordListValue: String,
    wordListCount: String,
    onChangeList: () -> Unit,
    ctaLabel: String,
    onCtaClick: () -> Unit,
    editActionLabel: String,
    changeActionLabel: String,
    modifier: Modifier = Modifier,
    practiceInsteadLabel: String? = null,
    onPracticeInsteadClick: (() -> Unit)? = null,
) {
    val spacing = AppTheme.spacing
    val colors = AppTheme.colors
    val ctaEnabled = placeholderReason == null

    AppCard(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(spacing.md),
            verticalArrangement = Arrangement.spacedBy(spacing.md),
        ) {
            // Header：焦点标题 + Mixed 徽标（徽标是信息，不是模式控件）
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.onSurface,
                    modifier = Modifier.weight(1f).semantics { heading() }, // §10.2 卡标题 heading 语义
                )
                AppBadge(variant = AppBadgeVariant.Secondary) {
                    Text(text = badge, style = MaterialTheme.typography.labelMedium)
                }
            }

            // 焦点选择器：单选 chips（切换只改卡片内容，不启动会话、不建路由）
            // FlowRow：200% 字体/窄屏换行，不溢出（04 §Responsive rules：compact chips 可滚动；换行等价更稳）
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.xs),
                verticalArrangement = Arrangement.spacedBy(spacing.xs),
            ) {
                focusOptions.forEach { option ->
                    AppChip(
                        label = { Text(option.label) },
                        selected = option.selected,
                        onClick = option.onSelect,
                    )
                }
            }

            // 详情：准确率 + 每日目标（Edit 打开 goal sheet）
            Text(
                text = accuracyLabel,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.onMuted,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
            ) {
                Text(
                    text = goalLabel,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onMuted,
                    modifier = Modifier.weight(1f),
                )
                AppButton(
                    text = editActionLabel,
                    onClick = onEditGoal,
                    variant = AppButtonVariant.Link,
                    size = AppButtonSize.Sm,
                )
            }

            // 能力徽章（Vocabulary）或占位原因（Listening/Grammar）
            if (capabilityLabels.isNotEmpty()) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.xs),
                    verticalArrangement = Arrangement.spacedBy(spacing.xs),
                ) {
                    capabilityLabels.forEach { label ->
                        AppBadge(variant = AppBadgeVariant.Outline) {
                            Text(text = label, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            } else if (placeholderReason != null) {
                Text(
                    text = placeholderReason,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onMuted,
                )
            }

            // 进度：环为辅（range 语义由 AppProgress 内部提供），文本 "X of Y words" 为主
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.md),
            ) {
                if (progress != null) {
                    AppProgress(
                        variant = AppProgressVariant.Circular,
                        progress = progress,
                    )
                }
                Text(
                    text = progressLabel,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onSurface,
                    modifier = Modifier.weight(1f),
                )
            }

            // 词单行：Adaptive 默认可恢复；Change 打开选择 sheet
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = wordListLabel,
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.onMuted,
                    )
                    Text(
                        text = wordListValue,
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.onSurface,
                    )
                    Text(
                        text = wordListCount,
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.onMuted,
                    )
                }
                AppButton(
                    text = changeActionLabel,
                    onClick = onChangeList,
                    variant = AppButtonVariant.Link,
                    size = AppButtonSize.Sm,
                )
            }

            // 唯一主 CTA；占位焦点 disabled + 原因可见 + 次级动作保底
            AppButton(
                text = ctaLabel,
                onClick = onCtaClick,
                modifier = Modifier.fillMaxWidth(),
                size = AppButtonSize.Xl, // 08 spec：主 CTA 用 Xl（AppControlHeight.Xl=48）
                enabled = ctaEnabled,
            )
            if (!ctaEnabled && practiceInsteadLabel != null && onPracticeInsteadClick != null) {
                AppButton(
                    text = practiceInsteadLabel,
                    onClick = onPracticeInsteadClick,
                    modifier = Modifier.fillMaxWidth(),
                    variant = AppButtonVariant.Ghost,
                    size = AppButtonSize.Md,
                )
            }
        }
    }
}

// 组件不内置用户可见字符串（R6）：Edit/Change 等动作标签由 feature 经 catalog 传入。
@PreviewLightDark
@PreviewFontScale
@Composable
private fun LearningFocusCardPreview() {
    AppTheme(ThemeSettings()) {
        LearningFocusCard(
            title = "Vocabulary learning",
            badge = "Mixed",
            focusOptions = listOf(
                FocusOption("Vocabulary", true, {}),
                FocusOption("Reading", false, {}),
                FocusOption("Grammar", false, {}),
            ),
            accuracyLabel = "Review accuracy: 50%",
            goalLabel = "Daily goal: 20 words",
            onEditGoal = {},
            capabilityLabels = listOf("Multiple choice", "Spelling", "Image association"),
            placeholderReason = null,
            progress = 0.1f,
            progressLabel = "2 of 20 words",
            wordListLabel = "Word list",
            wordListValue = "Adaptive list",
            wordListCount = "adapts to your level",
            onChangeList = {},
            ctaLabel = "Today's vocabulary",
            onCtaClick = {},
            editActionLabel = "Edit",
            changeActionLabel = "Change",
        )
    }
}
