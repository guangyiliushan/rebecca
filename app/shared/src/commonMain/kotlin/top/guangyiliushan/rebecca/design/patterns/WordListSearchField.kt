package top.guangyiliushan.rebecca.design.patterns

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import top.guangyiliushan.rebecca.design.components.AppField
import top.guangyiliushan.rebecca.design.components.AppIconButton
import top.guangyiliushan.rebecca.design.components.AppIconButtonSize
import top.guangyiliushan.rebecca.design.components.AppInput
import top.guangyiliushan.rebecca.design.tokens.AppControlHeight
import top.guangyiliushan.rebecca.design.theme.AppTheme
import top.guangyiliushan.rebecca.design.theme.ThemeSettings

/**
 * 词单搜索框（09 spec §Priority search）：AppField + AppInput(leading=搜索图标, trailing=清空)。
 * 清空是独立 48dp 动作（AppIconButton 自带热区）；仅在有内容时出现（空输入不给无意义动作）。
 * 文案全由调用方经 catalog 给（R6）。
 */
@Composable
fun WordListSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    onClear: () -> Unit,
    placeholder: String,
    clearContentDescription: String,
    modifier: Modifier = Modifier,
    onSubmit: (() -> Unit)? = null,
) {
    AppField(modifier = modifier.fillMaxWidth()) {
        AppInput(
            value = value,
            onValueChange = onValueChange,
            placeholder = placeholder,
            singleLine = true,
            inputHeight = AppControlHeight.Xl, // 48dp：trailing 清空钮需要完整 48dp 热区（F8），且不随输入跳变
            leading = { Icon(Icons.Filled.Search, contentDescription = null) },
            trailing = {
                AppIconButton(
                    onClick = onClear,
                    icon = { Icon(Icons.Filled.Close, contentDescription = null) },
                    contentDescription = clearContentDescription,
                    size = AppIconButtonSize.Sm,
                    // 常驻但空输入时禁用：热区稳定（无高度跳变）+ 诚实禁用态
                    enabled = value.isNotEmpty(),
                )
            },
            keyboardOptions = if (onSubmit != null) {
                KeyboardOptions(imeAction = ImeAction.Search)
            } else {
                KeyboardOptions.Default
            },
        )
    }
}

@PreviewLightDark
@PreviewFontScale
@Composable
private fun WordListSearchFieldPreview() {
    AppTheme(ThemeSettings()) {
        WordListSearchField(
            value = "macbeth",
            onValueChange = {},
            onClear = {},
            placeholder = "Search word lists",
            clearContentDescription = "Clear",
        )
    }
}
