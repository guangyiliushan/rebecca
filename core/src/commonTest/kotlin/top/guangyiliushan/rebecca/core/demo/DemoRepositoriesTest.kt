package top.guangyiliushan.rebecca.core.demo

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import top.guangyiliushan.rebecca.core.logic.currentStreak

/** 契约测试：每个 repo 接口方法断言可查回种子数据（"demo 驱动屏幕"的客观验证）。 */
class DemoRepositoriesTest {
    @Test
    fun dictionary_searchByFormReturnsAllSensesOfLemma() {
        assertEquals(4, DemoContentDictionaryRepository.search("run").size)
    }

    @Test
    fun dictionary_lemmaReturnsLemma() {
        assertNotNull(DemoContentDictionaryRepository.lemma("lemma-run"))
    }

    @Test
    fun dictionary_sensesForLemma() {
        assertEquals(4, DemoContentDictionaryRepository.sensesForLemma("lemma-run").size)
    }

    @Test
    fun reading_booksHaveParagraphs() {
        assertTrue(DemoContentReadingRepository.books().all { it.paragraphs.isNotEmpty() })
    }

    @Test
    fun explore_hasAllSections() {
        assertNotNull(DemoContentExploreRepository.wordOfTheDay())
        assertTrue(DemoContentExploreRepository.confusables().isNotEmpty())
        assertTrue(DemoContentExploreRepository.rootsAndAffixes().isNotEmpty())
    }

    @Test
    fun readingProgress_seedExists() {
        assertNotNull(DemoReadingProgressRepository.progress(DEMO_ACCOUNT, "book-1"))
    }

    @Test
    fun excerpts_seedExists() {
        assertTrue(DemoExcerptRepository.excerpts(DEMO_ACCOUNT).isNotEmpty())
    }

    @Test
    fun wordLists_entriesSortedByOrd() {
        val entries = DemoWordListRepository.entries(DEMO_ACCOUNT, "list-basic-verbs")
        assertEquals(11, entries.size)
        assertEquals(entries.map { it.ord }.sorted(), entries.map { it.ord })
    }

    @Test
    fun wordLists_threeLists() {
        assertEquals(3, DemoWordListRepository.lists(DEMO_ACCOUNT).size)
    }

    @Test
    fun mastery_coversMasteredSeedsOnly() {
        // 0.1.1：前 20 个有 mastery；后 8 个无 = 学习模式新词池（grill Q6）
        assertEquals(20, DemoMasteryRepository.allFor(DEMO_ACCOUNT).size)
    }

    @Test
    fun mastery_newWordPool_hasEightWithoutMastery() {
        val new = DEMO_SENSES.filter { DemoMasteryRepository.mastery(DEMO_ACCOUNT, it.id) == null }
        assertEquals(8, new.size)
    }

    @Test
    fun mastery_eventsSeed_supportsFourDayStreak() {
        val events = DemoMasteryRepository.events(DEMO_ACCOUNT)
        assertTrue(events.isNotEmpty())
        assertEquals(4, currentStreak(events, DEMO_NOW))
    }

    @Test
    fun preferences_hasDefaults() {
        assertNotNull(DemoPreferenceRepository.get(DEMO_ACCOUNT, "ui.theme"))
        assertNotNull(DemoPreferenceRepository.get(DEMO_ACCOUNT, "ui.language"))
        assertNotNull(DemoPreferenceRepository.get(DEMO_ACCOUNT, "nativeAssist"))
    }

    @Test
    fun study_dueSensesReturnsSenses() {
        assertTrue(DemoStudyRepository.dueSenses(DEMO_ACCOUNT, limit = 5).isNotEmpty())
    }

    @Test
    fun study_dueSensesOnlyDueNow_returnsSeven() {
        // 索引 i%3==0 且 i<20 → 0,3,6,9,12,15,18 = 7 个 due 今天
        assertEquals(7, DemoStudyRepository.dueSenses(DEMO_ACCOUNT, limit = 100).size)
    }
}
