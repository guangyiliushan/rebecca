package top.guangyiliushan.rebecca.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import top.guangyiliushan.rebecca.design.theme.AppTheme
import top.guangyiliushan.rebecca.design.tokens.AppControlHeight

/**
 * 输入原子（frontend-design-system v0.3 §6.5；规格页 04-appinput，官方三层之原子层）。
 * 无 label/description 概念——由 AppField 包装；invalid/enabled/errors 读 LocalAppFieldState（可被参数覆盖）。
 * placeholder 用 onMuted 槽；invalid 描边 destructive + error() 语义（文案来自调用方 errors 首条，F6）。
 * 焦点环用 ring 槽（§10.4，键盘导航可见）。多行模式 heightIn(min=36)，不硬压单行高度。
 */
@Composable
fun AppInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    enabled: Boolean = LocalAppFieldState.current.enabled,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    invalid: Boolean = LocalAppFieldState.current.invalid,
    errorDescription: String? = LocalAppFieldState.current.errors.firstOrNull(),
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    inputHeight: Dp = AppControlHeight.Md, // 单行视觉高度档（F8：槽位内控件需 48dp 热区时调用方传 Xl，避免高度跳变）
) {
    val colors = AppTheme.colors
    val body = AppTheme.type.bodyLarge
    val textStyle = TextStyle(fontSize = body.size, lineHeight = body.lineHeight, color = colors.onBackground)
    val shape = RoundedCornerShape(AppTheme.radius.extraSmall)
    var focused by remember { mutableStateOf(false) }

    val borderColor = when {
        invalid -> colors.destructive
        focused -> colors.ring
        else -> colors.input
    }

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (singleLine) Modifier.height(inputHeight)
                else Modifier.heightIn(min = AppControlHeight.Md),
            )
            .background(colors.background, shape)
            .border(width = 1.dp, color = borderColor, shape = shape)
            .onFocusChanged { focused = it.isFocused }
            .semantics {
                if (invalid && errorDescription != null) error(errorDescription)
            },
        enabled = enabled,
        singleLine = singleLine,
        maxLines = maxLines,
        keyboardOptions = keyboardOptions,
        textStyle = textStyle,
        cursorBrush = SolidColor(colors.primary),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier.padding(horizontal = AppTheme.spacing.controlPadding),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (leading != null) {
                    leading()
                    Spacer(Modifier.width(AppTheme.spacing.sm))
                }
                Box(Modifier.weight(1f)) {
                    if (value.isEmpty() && placeholder != null) {
                        androidx.compose.material3.Text(
                            text = placeholder,
                            color = colors.onMuted,
                            style = textStyle,
                        )
                    }
                    innerTextField()
                }
                if (trailing != null) {
                    Spacer(Modifier.width(AppTheme.spacing.sm))
                    trailing()
                }
            }
        },
    )
}

@PreviewLightDark
@PreviewFontScale
@Composable
private fun AppInputPreview() {
    AppTheme {
        AppInput(value = "hello", onValueChange = {})
    }
}
