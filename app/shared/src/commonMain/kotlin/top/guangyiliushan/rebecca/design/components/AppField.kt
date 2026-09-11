package top.guangyiliushan.rebecca.design.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import top.guangyiliushan.rebecca.design.theme.AppTheme

/** AppField 布局方向（v1 只提供 Vertical/Horizontal；Responsive 留 0.1.2 + LocalLayoutDensity）。 */
enum class AppFieldOrientation { Vertical, Horizontal }

/**
 * 表单域（frontend-design-system v0.3 §6.5；规格页 04-appinput，官方三层之语义包装层）。
 * 对应官方 field.tsx：label / description / errors（多条去重）+ 父→子 invalid 级联（LocalAppFieldState）。
 * 两种 orientation 都完整渲染 label/description/errors（无静默丢失）。
 * errors 的 liveRegion=Assertive（官方 FieldError role="alert" 的 Compose 等价物，§10.6）。
 */
@Composable
fun AppField(
    modifier: Modifier = Modifier,
    invalid: Boolean = false,
    enabled: Boolean = true,
    label: (@Composable () -> Unit)? = null,
    description: (@Composable () -> Unit)? = null,
    errors: List<String> = emptyList(),
    orientation: AppFieldOrientation = AppFieldOrientation.Vertical,
    content: @Composable () -> Unit,
) {
    val spacing = AppTheme.spacing
    val distinctErrors = errors.distinct()

    CompositionLocalProvider(
        LocalAppFieldState provides AppFieldState(invalid = invalid, enabled = enabled, errors = distinctErrors),
    ) {
        Column(modifier, verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
            if (orientation == AppFieldOrientation.Horizontal) {
                Row(horizontalArrangement = Arrangement.spacedBy(spacing.sm)) {
                    label?.let { l ->
                        Column(Modifier.weight(1f)) { l() }
                    }
                    content()
                }
            } else {
                label?.invoke()
                content()
            }
            description?.invoke()
            if (distinctErrors.isNotEmpty()) {
                AppFieldError(distinctErrors)
            }
        }
    }
}

/** 错误展示：多条去重 + Assertive 朗读（官方 FieldError role="alert" 等价物）。 */
@Composable
fun AppFieldError(
    errors: List<String>,
    modifier: Modifier = Modifier,
) {
    val colors = AppTheme.colors
    val label = AppTheme.type.bodySmall
    val style = TextStyle(fontSize = label.size, lineHeight = label.lineHeight)
    Column(
        modifier = modifier.semantics { liveRegion = LiveRegionMode.Assertive },
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xxs),
    ) {
        errors.distinct().forEach { message ->
            androidx.compose.material3.Text(message, color = colors.destructive, style = style)
        }
    }
}

@PreviewLightDark
@PreviewFontScale
@Composable
private fun AppFieldPreview() {
    AppTheme {
        Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.md)) {
            AppField(
                label = { androidx.compose.material3.Text("Name") },
                errors = listOf("Required", "Required"),
            ) {
                AppInput(value = "", onValueChange = {})
            }
            AppField(
                label = { androidx.compose.material3.Text("Horizontal") },
                description = { androidx.compose.material3.Text("desc") },
                errors = listOf("Too short"),
                orientation = AppFieldOrientation.Horizontal,
            ) {
                AppInput(value = "", onValueChange = {})
            }
        }
    }
}
