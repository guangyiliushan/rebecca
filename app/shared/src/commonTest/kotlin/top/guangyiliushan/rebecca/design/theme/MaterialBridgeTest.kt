package top.guangyiliushan.rebecca.design.theme

import androidx.compose.ui.graphics.Color
import kotlin.math.pow
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import top.guangyiliushan.rebecca.design.theme.palettes.TealPalette

/** WCAG 2.x 相对亮度对比度。 */
private fun contrastRatio(a: Color, b: Color): Double {
    fun lin(c: Float): Double {
        val v = c.toDouble()
        return if (v <= 0.03928) v / 12.92 else ((v + 0.055) / 1.055).pow(2.4)
    }
    fun lum(c: Color): Double = 0.2126 * lin(c.red) + 0.7152 * lin(c.green) + 0.0722 * lin(c.blue)
    val la = lum(a); val lb = lum(b)
    val hi = maxOf(la, lb); val lo = minOf(la, lb)
    return (hi + 0.05) / (lo + 0.05)
}

class MaterialBridgeTest {
    private val scheme = TealPalette.light.toMaterialColorScheme()

    @Test
    fun coreSlotsMapOneToOne() {
        assertEquals(TealPalette.light.primary, scheme.primary)
        assertEquals(TealPalette.light.onPrimary, scheme.onPrimary)
        assertEquals(TealPalette.light.background, scheme.background)
        assertEquals(TealPalette.light.onBackground, scheme.onBackground)
        assertEquals(TealPalette.light.surface, scheme.surface)
        assertEquals(TealPalette.light.onSurface, scheme.onSurface)
    }

    @Test
    fun semanticSlotsBridgeOntoMaterialRoles() {
        assertEquals(TealPalette.light.primary, scheme.secondary)
        assertEquals(TealPalette.light.onPrimary, scheme.onSecondary)
        assertEquals(TealPalette.light.accent, scheme.tertiary)
        assertEquals(TealPalette.light.destructive, scheme.error)
        assertEquals(TealPalette.light.border, scheme.outline)
        assertEquals(TealPalette.light.border, scheme.outlineVariant)
        assertEquals(TealPalette.light.muted, scheme.surfaceVariant)
    }

    @Test
    fun containersAreDerivedNotDesigned() {
        assertEquals(TealPalette.light.accent, scheme.primaryContainer)
        assertEquals(TealPalette.light.onAccent, scheme.onPrimaryContainer)
        assertEquals(TealPalette.light.accent, scheme.secondaryContainer)
        assertEquals(TealPalette.light.onAccent, scheme.onSecondaryContainer)
        assertEquals(TealPalette.light.destructive.copy(alpha = 0.12f), scheme.errorContainer)
    }

    @Test
    fun selectedNavItemContrastIsReadable() {
        // M3 1.12 选中态：文字 = secondary，指示器 = secondaryContainer（NavigationRailColorTokens）
        // 修复：secondary=primary、secondaryContainer=accent，避免 muted-on-muted 的 1:1 对比度
        val light = TealPalette.light
        assertTrue(contrastRatio(light.primary, light.accent) >= 4.5)
        // 图标(onAccent)对指示器(accent) ≥3:1（WCAG 1.4.11 非文本）
        assertTrue(contrastRatio(light.onAccent, light.accent) >= 3.0)
        val dark = TealPalette.dark
        assertTrue(contrastRatio(dark.primary, dark.accent) >= 4.5)
        assertTrue(contrastRatio(dark.onAccent, dark.accent) >= 3.0)
    }

    @Test
    fun darkUsesDarkBase() {
        val dark = TealPalette.dark.toMaterialColorScheme()
        assertEquals(TealPalette.dark.primary, dark.primary)
        assertEquals(TealPalette.dark.background, dark.background)
    }
}
