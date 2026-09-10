package top.guangyiliushan.rebecca.design.theme

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ThemeSettingsTest {
    @Test
    fun systemModeFollowsSystemDarkness() {
        assertTrue(resolveDark(ThemeMode.System, systemDark = true))
        assertFalse(resolveDark(ThemeMode.System, systemDark = false))
    }

    @Test
    fun explicitModesOverrideSystem() {
        assertFalse(resolveDark(ThemeMode.Light, systemDark = true))
        assertTrue(resolveDark(ThemeMode.Dark, systemDark = false))
    }

    @Test
    fun defaultsAreSystemAndTeal() {
        val s = ThemeSettings()
        assertTrue(s.mode == ThemeMode.System && s.family == ThemeFamily.Teal)
    }
}
