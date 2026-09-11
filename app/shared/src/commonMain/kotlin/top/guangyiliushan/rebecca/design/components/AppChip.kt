package top.guangyiliushan.rebecca.design.components

import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import org.jetbrains.compose.resources.stringResource
import rebecca.app.shared.generated.resources.Res
import rebecca.app.shared.generated.resources.state_not_selected
import rebecca.app.shared.generated.resources.state_selected
import top.guangyiliushan.rebecca.design.theme.AppTheme

/** AppChip 变体（v0.3 §6.5：按官方三分法命名；已知语义差异 = M3 Checkbox 语义，见 R8/§10.2）。 */
enum class AppChipVariant { Filter, Choice, Assist }

/**
 * 可选中 chip（frontend-design-system v0.3 §6.5；规格页 03-appchip）。
 * 底座 M3 FilterChip：role=Checkbox（M3 内建语义，R8 不覆盖）+ stateDescription 来自 catalog（F15）。
 * 触控热区 ≥48dp（minimumInteractiveComponentSize，F8）。
 */
@Composable
fun AppChip(
    label: @Composable () -> Unit,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: AppChipVariant = AppChipVariant.Filter,
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null,
) {
    val colors = AppTheme.colors
    val desc = stringResource(
        if (selected) Res.string.state_selected else Res.string.state_not_selected,
    )

    // Filter/Choice/Assist 视觉同源（选中=accent 底）；语义差异（Checkbox vs button+pressed）已按 R8 登记 COMPONENTS.md
    val chipColors = FilterChipDefaults.filterChipColors(
        selectedContainerColor = colors.accent,
        selectedLabelColor = colors.onAccent,
    )

    FilterChip(
        selected = selected,
        onClick = onClick,
        label = label,
        // 顺序：minimumInteractiveComponentSize 在外（min 48dp 热区）→ 视觉由 M3 FilterChip 自带
        modifier = modifier.minimumInteractiveComponentSize().semantics { stateDescription = desc },
        enabled = enabled,
        leadingIcon = leadingIcon,
        colors = chipColors,
    )
}

@PreviewLightDark
@PreviewFontScale
@Composable
private fun AppChipPreview() {
    AppTheme {
        androidx.compose.foundation.layout.Row {
            AppChip(label = { androidx.compose.material3.Text("Verb") }, selected = true, onClick = {})
            AppChip(label = { androidx.compose.material3.Text("Noun") }, selected = false, onClick = {})
        }
    }
}
