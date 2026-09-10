package top.guangyiliushan.rebecca.design.theme

import kotlin.test.Test
import kotlin.test.assertEquals
import top.guangyiliushan.rebecca.design.theme.palettes.TealPalette

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
        assertEquals(TealPalette.light.muted, scheme.secondary)
        assertEquals(TealPalette.light.onMuted, scheme.onSecondary)
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
        assertEquals(TealPalette.light.destructive.copy(alpha = 0.12f), scheme.errorContainer)
    }

    @Test
    fun darkUsesDarkBase() {
        val dark = TealPalette.dark.toMaterialColorScheme()
        assertEquals(TealPalette.dark.primary, dark.primary)
        assertEquals(TealPalette.dark.background, dark.background)
    }
}
