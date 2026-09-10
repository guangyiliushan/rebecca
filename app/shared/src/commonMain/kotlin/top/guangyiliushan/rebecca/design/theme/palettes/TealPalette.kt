package top.guangyiliushan.rebecca.design.theme.palettes

import androidx.compose.ui.graphics.Color
import top.guangyiliushan.rebecca.design.tokens.AppColors

/**
 * 青色系主题家族（frontend-design-system §3.1 修正后实测值）。
 * 全仓库唯一允许 Color(0x…) 字面量的文件；改值必须过对比度门禁（0.1.0 起 CI）。
 */
object TealPalette {
    val light = AppColors(
        background = Color(0xFFFFFFFF), onBackground = Color(0xFF101816),
        surface = Color(0xFFFFFFFF), onSurface = Color(0xFF101816),
        muted = Color(0xFFF1F5F4), onMuted = Color(0xFF5B6B67),
        primary = Color(0xFF0F766E), onPrimary = Color(0xFFFFFFFF),
        accent = Color(0xFFF0FDFA), onAccent = Color(0xFF0F766E),
        destructive = Color(0xFFDC2626), onDestructive = Color(0xFFFFFFFF),
        success = Color(0xFF15803D), onSuccess = Color(0xFFFFFFFF),
        warning = Color(0xFFB45309), onWarning = Color(0xFFFFFFFF),
        border = Color(0xFFDCE5E2),
        input = Color(0xFF7C918B),
        ring = Color(0xFF0D9488),
    )

    val dark = AppColors(
        background = Color(0xFF0B1110), onBackground = Color(0xFFECF5F2),
        surface = Color(0xFF121A18), onSurface = Color(0xFFECF5F2),
        muted = Color(0xFF1A2422), onMuted = Color(0xFF8FA39E),
        primary = Color(0xFF2DD4BF), onPrimary = Color(0xFF06231F),
        accent = Color(0xFF123330), onAccent = Color(0xFF5EEAD4),
        destructive = Color(0xFFF87171), onDestructive = Color(0xFF2A0A0A),
        success = Color(0xFF4ADE80), onSuccess = Color(0xFF06240F),
        warning = Color(0xFFFBBF24), onWarning = Color(0xFF2A1A02),
        border = Color(0xFF22302D),
        input = Color(0xFF527068),
        ring = Color(0xFF2DD4BF),
    )
}
