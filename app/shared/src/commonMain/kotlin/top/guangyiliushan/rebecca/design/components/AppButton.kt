package top.guangyiliushan.rebecca.design.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import rebecca.app.shared.generated.resources.Res
import rebecca.app.shared.generated.resources.state_loading
import top.guangyiliushan.rebecca.design.theme.AppTheme
import top.guangyiliushan.rebecca.design.tokens.AppControlHeight
import top.guangyiliushan.rebecca.design.tokens.AppSpacing

/** AppButton 变体（R2：枚举参数化；对齐 shadcn button.tsx 6 variant）。 */
enum class AppButtonVariant { Primary, Secondary, Ghost, Outline, Destructive, Link }

/** AppButton 尺寸（视觉高度 = AppControlHeight 档；触控热区由 minimumInteractiveComponentSize 保证 ≥48dp，F8）。 */
enum class AppButtonSize(val height: Dp, val horizontalPadding: Dp) {
    Xs(AppControlHeight.Xs, AppSpacing.sm),
    Sm(AppControlHeight.Sm, AppSpacing.controlPadding),
    Md(AppControlHeight.Md, AppSpacing.md),
    Lg(AppControlHeight.Lg, AppSpacing.lg),
}

/** 控件文本样式：bodyMedium 档（对齐官方 text-sm），Link 变体加下划线。 */
@Composable
private fun buttonTextStyle(underlined: Boolean): TextStyle {
    val t = AppTheme.type.bodyMedium
    val style = TextStyle(fontSize = t.size, lineHeight = t.lineHeight)
    return if (underlined) style.copy(textDecoration = TextDecoration.Underline) else style
}

/**
 * 主按钮（frontend-design-system v0.3 §6.5；规格页 03-atom-mapping/01）。
 * loading=true → 禁交互 + spinner（role=status）+ stateDescription 来自 catalog（F15）。
 */
@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: AppButtonVariant = AppButtonVariant.Primary,
    size: AppButtonSize = AppButtonSize.Md,
    enabled: Boolean = true,
    loading: Boolean = false,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
) {
    val colors = AppTheme.colors
    val effectiveEnabled = enabled && !loading

    val container: Color = when (variant) {
        AppButtonVariant.Primary -> colors.primary
        AppButtonVariant.Secondary -> colors.accent
        AppButtonVariant.Destructive -> colors.destructive
        AppButtonVariant.Ghost, AppButtonVariant.Outline, AppButtonVariant.Link -> Color.Transparent
    }
    val content: Color = when (variant) {
        AppButtonVariant.Primary -> colors.onPrimary
        AppButtonVariant.Secondary -> colors.onAccent
        AppButtonVariant.Destructive -> colors.onDestructive
        AppButtonVariant.Ghost, AppButtonVariant.Outline, AppButtonVariant.Link -> colors.primary
    }
    val border: BorderStroke? = if (variant == AppButtonVariant.Outline) BorderStroke(1.dp, colors.border) else null

    Button(
        onClick = onClick,
        // 顺序：minimumInteractiveComponentSize 在外（min 48dp 约束整体热区）→ requiredHeight 在内（视觉高度）
        modifier = modifier.minimumInteractiveComponentSize().requiredHeight(size.height),
        enabled = effectiveEnabled,
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
            focusedElevation = 0.dp,
            hoveredElevation = 0.dp,
            disabledElevation = 0.dp,
        ),
        border = border,
        colors = ButtonDefaults.buttonColors(containerColor = container, contentColor = content),
        contentPadding = PaddingValues(horizontal = size.horizontalPadding, vertical = 0.dp),
    ) {
        if (loading) {
            AppSpinner(size = AppSpinnerSize.Sm, contentDescription = stringResource(Res.string.state_loading))
        } else {
            leadingIcon?.invoke()
        }
        Text(text, style = buttonTextStyle(underlined = variant == AppButtonVariant.Link), maxLines = 1)
        if (!loading) trailingIcon?.invoke()
    }
}

@PreviewLightDark
@PreviewFontScale
@Composable
private fun AppButtonPreview() {
    AppTheme {
        androidx.compose.foundation.layout.Column {
            AppButton(text = "Primary", onClick = {})
            AppButton(text = "Outline", variant = AppButtonVariant.Outline, onClick = {})
            AppButton(text = "Loading", onClick = {}, loading = true)
        }
    }
}
