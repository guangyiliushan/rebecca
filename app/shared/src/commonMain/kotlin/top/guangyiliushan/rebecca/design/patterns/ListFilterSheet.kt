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
import top.guangyiliushan.rebecca.design.components.AppButtonVariant
import top.guangyiliushan.rebecca.design.components.AppChip
import top.guangyiliushan.rebecca.design.components.AppChipGroup
import top.guangyiliushan.rebecca.design.components.AppChipGroupMode
import top.guangyiliushan.rebecca.design.components.AppChipGroupSpacing
import top.guangyiliushan.rebecca.design.overlays.AppSheet
import top.guangyiliushan.rebecca.design.overlays.AppSheetFooter
import top.guangyiliushan.rebecca.design.overlays.AppSheetHeader
import top.guangyiliushan.rebecca.design.theme.AppTheme
import top.guangyiliushan.rebecca.design.theme.ThemeSettings

/** 筛选组（展示值；mode 由数据形状决定——Level/Size/Progress/Sort 单选，Topic/Source/Structure 多选，09 spec §Filter sheet 表）。 */
data class FilterGroupUi(
    val title: String,
    val mode: AppChipGroupMode,
    val options: List<FilterOptionUi>,
)

/** 单个筛选选项（selected + onToggle 由调用方状态提升）。 */
data class FilterOptionUi(
    val label: String,
    val selected: Boolean,
    val onToggle: () -> Unit,
)

/**
 * 词单筛选 sheet（09 spec §Filter sheet）：分组多选 chips + Reset/Apply。
 * 0.1.2 只落组件（屏内入口后置——无 metadata 来源的组由调用方不传入，spec：unsupported 组隐藏）。
 * 文案全由调用方给（R6）；Reset 不清 query（query 状态在屏层，不在本 sheet 管辖）。
 */
@Composable
fun ListFilterSheet(
    open: Boolean,
    title: String,
    groups: List<FilterGroupUi>,
    applyLabel: String,
    resetLabel: String,
    onApply: () -> Unit,
    onReset: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = AppTheme.spacing
    val colors = AppTheme.colors
    AppSheet(open = open, onDismiss = onDismiss, modifier = modifier) {
        AppSheetHeader(title = { Text(title) })
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = spacing.md),
            verticalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            groups.forEach { group ->
                Text(
                    text = group.title,
                    style = MaterialTheme.typography.labelMedium,
                    color = colors.onMuted,
                    modifier = Modifier.semantics { heading() },
                )
                AppChipGroup(
                    modifier = Modifier.fillMaxWidth(),
                    mode = group.mode,
                    spacing = AppChipGroupSpacing.Separated,
                ) {
                    group.options.forEach { option ->
                        AppChip(
                            label = { Text(option.label) },
                            selected = option.selected,
                            onClick = option.onToggle,
                        )
                    }
                }
            }
        }
        AppSheetFooter {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
            ) {
                AppButton(
                    text = resetLabel,
                    onClick = onReset,
                    modifier = Modifier.weight(1f),
                    variant = AppButtonVariant.Ghost,
                )
                AppButton(
                    text = applyLabel,
                    onClick = onApply,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@PreviewLightDark
@PreviewFontScale
@Composable
private fun ListFilterSheetPreview() {
    AppTheme(ThemeSettings()) {
        ListFilterSheet(
            open = true,
            title = "Filters",
            groups = listOf(
                FilterGroupUi(
                    title = "Source",
                    mode = AppChipGroupMode.Multiple,
                    options = listOf(
                        FilterOptionUi("Adaptive", true, {}),
                        FilterOptionUi("Personal", false, {}),
                        FilterOptionUi("Curated", false, {}),
                    ),
                ),
                FilterGroupUi(
                    title = "Progress",
                    mode = AppChipGroupMode.Single,
                    options = listOf(
                        FilterOptionUi("Not started", false, {}),
                        FilterOptionUi("In progress", true, {}),
                    ),
                ),
            ),
            applyLabel = "Apply",
            resetLabel = "Reset",
            onApply = {},
            onReset = {},
            onDismiss = {},
        )
    }
}
