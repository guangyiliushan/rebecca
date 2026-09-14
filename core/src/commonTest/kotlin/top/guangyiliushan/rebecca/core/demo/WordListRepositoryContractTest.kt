package top.guangyiliushan.rebecca.core.demo

import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import top.guangyiliushan.rebecca.core.model.SenseId
import top.guangyiliushan.rebecca.core.model.WordList
import top.guangyiliushan.rebecca.core.model.WordListEntry
import top.guangyiliushan.rebecca.core.model.WordListSource

/**
 * 0.1.2 仓库契约（先红后绿）：建/删/重排/移除/反查。
 * 全部操作在一次性测试词单上进行，@AfterTest 软删清理——不污染共享 demo 单例。
 */
class WordListRepositoryContractTest {
    private val testListId = "test-contract-list"
    private val testSenses = listOf(SenseId("sense-a"), SenseId("sense-b"), SenseId("sense-c"))

    @AfterTest
    fun cleanup() {
        DemoWordListRepository.deleteList(DEMO_ACCOUNT, testListId, DEMO_NOW)
    }

    private fun seedTestList() {
        DemoWordListRepository.upsertList(
            WordList(DEMO_ACCOUNT, testListId, "Contract Test", WordListSource.USER, DEMO_NOW, description = "t"),
        )
        testSenses.forEachIndexed { i, senseId ->
            DemoWordListRepository.upsertEntry(
                WordListEntry(DEMO_ACCOUNT, testListId, senseId, i, DEMO_NOW),
            )
        }
    }

    @Test
    fun createList_thenQueryBack() {
        seedTestList()
        val found = DemoWordListRepository.lists(DEMO_ACCOUNT).firstOrNull { it.id == testListId }
        assertEquals("Contract Test", found?.title)
        assertEquals("t", found?.description)
    }

    @Test
    fun deleteList_removesFromListsAndSoftDeletesEntries() {
        seedTestList()
        DemoWordListRepository.deleteList(DEMO_ACCOUNT, testListId, DEMO_NOW)
        assertFalse(DemoWordListRepository.lists(DEMO_ACCOUNT).any { it.id == testListId })
        assertTrue(DemoWordListRepository.entries(DEMO_ACCOUNT, testListId).isEmpty())
    }

    @Test
    fun reorder_changesOrdToGivenOrder() {
        seedTestList()
        val reversed = testSenses.reversed()
        DemoWordListRepository.reorder(DEMO_ACCOUNT, testListId, reversed, DEMO_NOW)
        assertEquals(reversed, DemoWordListRepository.entries(DEMO_ACCOUNT, testListId).map { it.senseId })
    }

    @Test
    fun removeEntry_thenListsContainingExcludesList() {
        seedTestList()
        val probe = testSenses[0]
        assertTrue(DemoWordListRepository.listsContaining(DEMO_ACCOUNT, probe).any { it.id == testListId })
        DemoWordListRepository.removeEntry(DEMO_ACCOUNT, testListId, probe, DEMO_NOW)
        assertFalse(DemoWordListRepository.entries(DEMO_ACCOUNT, testListId).any { it.senseId == probe })
        assertFalse(DemoWordListRepository.listsContaining(DEMO_ACCOUNT, probe).any { it.id == testListId })
    }

    @Test
    fun listsContaining_matchesNonDeletedEntriesOnly() {
        seedTestList()
        assertEquals(3, DemoWordListRepository.entries(DEMO_ACCOUNT, testListId).size)
        assertTrue(DemoWordListRepository.listsContaining(DEMO_ACCOUNT, SenseId("sense-b")).any { it.id == testListId })
    }
}
