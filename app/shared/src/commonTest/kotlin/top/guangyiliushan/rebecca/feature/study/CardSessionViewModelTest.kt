package top.guangyiliushan.rebecca.feature.study

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import top.guangyiliushan.rebecca.core.RebeccaData
import top.guangyiliushan.rebecca.core.model.AccountId
import top.guangyiliushan.rebecca.core.model.MasteryEvent
import top.guangyiliushan.rebecca.core.model.SenseId
import top.guangyiliushan.rebecca.core.model.SenseMastery
import top.guangyiliushan.rebecca.core.repository.MasteryRepository

/** 内存 fake（判定真实写入的验证隔离于共享 demo 单例——0.1.2 防污染）。 */
private class FakeMasteryRepo : MasteryRepository {
    val masteries = mutableMapOf<Pair<AccountId, SenseId>, SenseMastery>()
    val events = mutableListOf<MasteryEvent>()

    override fun mastery(accountId: AccountId, senseId: SenseId): SenseMastery? = masteries[accountId to senseId]
    override fun allFor(accountId: AccountId): List<SenseMastery> =
        masteries.values.filter { it.accountId == accountId && it.deletedAt == null }
    override fun upsert(mastery: SenseMastery) {
        masteries[mastery.accountId to mastery.senseId] = mastery
    }
    override fun record(event: MasteryEvent) {
        events += event
    }
    override fun events(accountId: AccountId): List<MasteryEvent> = events.filter { it.accountId == accountId }
}

class CardSessionViewModelTest {
    private val account = RebeccaData.demoAccount

    @Test
    fun startsWithFirstCard_frontFace() {
        val fake = FakeMasteryRepo()
        val vm = CardSessionViewModel("list-completed-basics", masteryRepo = fake, clockSeconds = { 0 })
        val s = vm.uiState.value
        assertEquals(0, s.index)
        assertEquals(4, s.total)
        assertEquals(CardFace.Front, s.face)
        assertEquals(false, s.finished)
        assertTrue(s.headword.isNotBlank())
    }

    @Test
    fun decideKnown_advancesAndWritesMasteryAndEvent() {
        val fake = FakeMasteryRepo()
        val vm = CardSessionViewModel("list-completed-basics", masteryRepo = fake, clockSeconds = { 0 })
        val targetSense = vm.uiState.value.senseId!!

        vm.onEvent(CardUiEvent.Decide(known = true))

        val s = vm.uiState.value
        assertEquals(1, s.index)
        assertEquals(1, s.knownCount)
        // 掌握度真实写入：无 previous（fake 空）→ +0.5 首档
        assertEquals(0.5, fake.mastery(account, targetSense)?.stability)
        // 事件真实写入（kind=card）
        assertEquals(1, fake.events.size)
        assertEquals("card", fake.events.single().kind)
        assertEquals(targetSense, fake.events.single().senseId)
    }

    @Test
    fun decideUnknown_countsUnknownAndWritesLapse() {
        val fake = FakeMasteryRepo()
        val vm = CardSessionViewModel("list-completed-basics", masteryRepo = fake, clockSeconds = { 0 })
        val targetSense = vm.uiState.value.senseId!!
        vm.onEvent(CardUiEvent.Decide(known = false))
        assertEquals(1, vm.uiState.value.unknownCount)
        assertEquals(1, fake.mastery(account, targetSense)?.lapses)
    }

    @Test
    fun emptyList_finishesImmediatelyWithoutCrash() {
        // R-D3：空词单不得进会话（详情屏 Practice 已禁用，VM 兜底）
        val fake = FakeMasteryRepo()
        val vm = CardSessionViewModel("test-empty-list", masteryRepo = fake, clockSeconds = { 0 })
        val s = vm.uiState.value
        assertEquals(0, s.total)
        assertTrue(s.finished)
        assertEquals(0, fake.events.size)
    }

    @Test
    fun flip_togglesFace() {
        val fake = FakeMasteryRepo()
        val vm = CardSessionViewModel("list-completed-basics", masteryRepo = fake, clockSeconds = { 0 })
        vm.onEvent(CardUiEvent.Flip)
        assertEquals(CardFace.Back, vm.uiState.value.face)
        vm.onEvent(CardUiEvent.Flip)
        assertEquals(CardFace.Front, vm.uiState.value.face)
    }

    @Test
    fun lastDecision_finishesSessionWithSummary() {
        val fake = FakeMasteryRepo()
        val vm = CardSessionViewModel("list-completed-basics", masteryRepo = fake, clockSeconds = { 42 })
        repeat(3) { vm.onEvent(CardUiEvent.Decide(known = true)) }
        vm.onEvent(CardUiEvent.Decide(known = false))
        val s = vm.uiState.value
        assertTrue(s.finished)
        assertEquals(3, s.summary!!.known)
        assertEquals(1, s.summary!!.unknown)
        assertEquals(4, s.summary!!.total)
        assertEquals(75, s.summary!!.accuracyPercent) // 截断口径 3*100/4
        assertEquals(42, s.elapsedSeconds)
        assertEquals(4, fake.events.size) // 每次判定一条事件
    }

    @Test
    fun decisions_matchListOrder() {
        val fake = FakeMasteryRepo()
        val vm = CardSessionViewModel("list-completed-basics", masteryRepo = fake, clockSeconds = { 0 })
        val first = vm.uiState.value.headword
        vm.onEvent(CardUiEvent.Decide(known = true))
        val second = vm.uiState.value.headword
        assertTrue(first != second, "判定后应进入下一张（ord 顺序）")
    }

    @Test
    fun decisions_applyCorePolicyToExistingMastery() {
        // fake 里预置 mastery：验证 core 政策（+0.5/−0.3）被 VM 真实执行
        val fake = FakeMasteryRepo()
        val vm = CardSessionViewModel("list-completed-basics", masteryRepo = fake, clockSeconds = { 0 })
        val targetSense = vm.uiState.value.senseId!!
        fake.upsert(
            SenseMastery(account, targetSense, stability = 1.0, dueAt = RebeccaData.demoNow, lapses = 2, updatedAt = RebeccaData.demoNow),
        )
        vm.onEvent(CardUiEvent.Decide(known = false))
        assertEquals(0.7, fake.mastery(account, targetSense)?.stability) // 1.0 − 0.3
        assertEquals(3, fake.mastery(account, targetSense)?.lapses)
    }
}
