package top.guangyiliushan.rebecca.design.tokens

import androidx.compose.ui.graphics.Color

/** 语义色槽（frontend-design-system §3.1）。屏幕只允许读槽，禁止读 Material 心智色。 */
data class AppColors(
    val background: Color, val onBackground: Color,
    val surface: Color, val onSurface: Color,
    val muted: Color, val onMuted: Color,
    val primary: Color, val onPrimary: Color,
    val accent: Color, val onAccent: Color,
    val destructive: Color, val onDestructive: Color,
    val success: Color, val onSuccess: Color,
    val warning: Color, val onWarning: Color,
    val border: Color,
    val input: Color,
    val ring: Color,
)
