package top.guangyiliushan.rebecca.core.language

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class LanguageProfileRegistryTest {
    @Test
    fun default_isEnglish() {
        assertEquals("en", LanguageProfileRegistry.default().languageCode)
    }

    @Test
    fun forLanguage_resolvesEnglish() {
        assertNotNull(LanguageProfileRegistry.forLanguage("en"))
    }

    @Test
    fun englishRegistersAllEngines() {
        assertEquals(EngineFeature.entries.toSet(), LanguageProfileRegistry.default().engines)
    }
}
