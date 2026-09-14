package top.guangyiliushan.rebecca.design.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import top.guangyiliushan.rebecca.design.theme.AppTheme
import top.guangyiliushan.rebecca.design.theme.ThemeSettings
import top.guangyiliushan.rebecca.design.tokens.AppSpacing

/** 组选择模式（R2；官方 toggle-group 的 `type="single|multiple"`）。 */
enum class AppChipGroupMode { Single, Multiple }

/** 间距（官方 toggle-group 的 `spacing`）。 */
enum class AppChipGroupSpacing { Connected, Separated }

/**
 * Chip 组（frontend-design-system v0.5 §6.5；官方 `toggle-group`）。
 * `Single` → `Modifier.selectableGroup()`（§10.2 选择组语义，读屏按选择组广播）。
 *
 * ⚠ 已知差异（2026-09-14 取证）：
 * 1. roving focus：官方原语 `Modifier.focusGroup()` **CMP 1.12.0 不存在**（发布物 ui-1.12.0-sources.jar
 *    无 FocusGroup.kt，属 AndroidX 独有）→ 逐 chip 可 Tab 聚焦，roving 行为待上游补 API 或 0.2.x 自实现。
 * 2. `Connected` 仅零间距连排；shadcn 的"首/尾圆角收拢、中间直角"未实现（M3 chip 形状按枚自持，
 *    组内无法逐枚覆写形状而不引入索引管线）。
 */
@Composable
fun AppChipGroup(
    modifier: Modifier = Modifier,
    mode: AppChipGroupMode = AppChipGroupMode.Single,
    spacing: AppChipGroupSpacing = AppChipGroupSpacing.Separated,
    content: @Composable RowScope.() -> Unit,
) {
    val gap = when (spacing) {
        AppChipGroupSpacing.Connected -> 0.dp
        AppChipGroupSpacing.Separated -> AppTheme.spacing.sm
    }
    Row(
        modifier = modifier.then(
            if (mode == AppChipGroupMode.Single) Modifier.selectableGroup() else Modifier,
        ),
        horizontalArrangement = Arrangement.spacedBy(gap),
        verticalAlignment = Alignment.CenterVertically,
        content = content,
    )
}

@PreviewLightDark
@PreviewFontScale
@Composable
private fun AppChipGroupPreview() {
    AppTheme(ThemeSettings()) {
        Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
            AppChipGroup(mode = AppChipGroupMode.Single, spacing = AppChipGroupSpacing.Separated) {
                AppChip(label = { Text("Verb") }, selected = true, onClick = {})
                AppChip(label = { Text("Noun") }, selected = false, onClick = {})
            }
            AppChipGroup(mode = AppChipGroupMode.Single, spacing = AppChipGroupSpacing.Connected) {
                AppChip(label = { Text("Prev") }, selected = false, onClick = {})
                AppChip(label = { Text("Next") }, selected = true, onClick = {})
            }
        }
    }
}
