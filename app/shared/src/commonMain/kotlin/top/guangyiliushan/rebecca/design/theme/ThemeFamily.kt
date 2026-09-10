package top.guangyiliushan.rebecca.design.theme

import top.guangyiliushan.rebecca.design.theme.palettes.TealPalette
import top.guangyiliushan.rebecca.design.tokens.AppColors

/** 主题家族。新增家族 = 新增 palette 文件 + 枚举值 + colors() 一臂（frontend-design-system §4.1）。 */
enum class ThemeFamily {
    Teal;

    fun colors(dark: Boolean): AppColors = when (this) {
        Teal -> if (dark) TealPalette.dark else TealPalette.light
    }
}
