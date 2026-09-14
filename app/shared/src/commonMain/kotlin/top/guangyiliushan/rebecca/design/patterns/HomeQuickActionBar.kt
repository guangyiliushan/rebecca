package top.guangyiliushan.rebecca.design.patterns

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import top.guangyiliushan.rebecca.design.components.AppButton
import top.guangyiliushan.rebecca.design.components.AppButtonSize
import top.guangyiliushan.rebecca.design.components.AppButtonVariant
import top.guangyiliushan.rebecca.design.components.AppCard
import top.guangyiliushan.rebecca.design.theme.AppTheme
import top.guangyiliushan.rebecca.design.theme.ThemeSettings

/** 快捷动作项（展示值；enabled=false = flow 未落地，诚实禁用不给死点击）。 */
data class QuickAction(
    val label: String,
    val onClick: () -> Unit,
    val enabled: Boolean = true,
)

/**
 * 快捷动作条带（ui-design-survey 05 §HomeQuickActionBar）：一张卡内横向等宽的多按钮组件
 * （Store / Challenges / AI chat / Books，标签必填；图标是辅助、本阶段不实现——用户裁定）。
 * 动作组不是 tabs：无选中态；对应 flow 未落地前 enabled=false + caption 说明（05 spec：
 * "route only when their flows exist"）。
 * 布局与可读性（review 🟠2）：行划分读 `LocalLayoutDensity`（AppScaffold 下发，不破 F-L4）——
 * Compact=2×2、Regular=1×4；按钮 maxLines=2 允许换行增高，200% 字体不截断（WCAG 1.4.4）。
 * 图标在 icons-core 缺乏匹配（Book/Chat 等）且用户确认可暂不实现，0.1.3/0.1.4 接 flow 时补。
 */
@Composable
fun HomeQuickActionBar(
    actions: List<QuickAction>,
    caption: String?,
    modifier: Modifier = Modifier,
) {
    val spacing = AppTheme.spacing
    val colors = AppTheme.colors
    val rows = quickActionRows(actions, LocalLayoutDensity.current)

    AppCard(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(spacing.md),
            verticalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            rows.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.xs),
                ) {
                    row.forEach { action ->
                        AppButton(
                            text = action.label,
                            onClick = action.onClick,
                            modifier = Modifier.weight(1f),
                            variant = AppButtonVariant.Outline,
                            size = AppButtonSize.Md,
                            enabled = action.enabled,
                            maxLines = 2, // 换行增高不裁切（200% 字体；WCAG 1.4.4）
                        )
                    }
                    // 末行不足时补等宽空位：保持左右填充中心对称（不挤成半宽按钮）
                    repeat(rows.first().size - row.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
            if (caption != null) {
                Text(
                    text = caption,
                    style = MaterialTheme.typography.labelMedium,
                    color = colors.onMuted,
                )
            }
        }
    }
}

/**
 * 行划分（纯函数，UI 与测试共用同一判定）：
 * Regular → 单行；Compact → 每行 2 个（2×2 优先，末行可短）。
 */
internal fun quickActionRows(
    actions: List<QuickAction>,
    density: LayoutDensity,
): List<List<QuickAction>> = when (density) {
    LayoutDensity.Compact -> actions.chunked(2)
    LayoutDensity.Regular -> if (actions.isEmpty()) emptyList() else listOf(actions)
}

@PreviewLightDark
@PreviewFontScale
@Composable
private fun HomeQuickActionBarPreview() {
    AppTheme(ThemeSettings()) {
        HomeQuickActionBar(
            actions = listOf(
                QuickAction("Store", {}, enabled = false),
                QuickAction("Challenges", {}, enabled = false),
                QuickAction("AI chat", {}, enabled = false),
                QuickAction("Books", {}, enabled = false),
            ),
            caption = "These tools arrive in later versions.",
        )
    }
}
