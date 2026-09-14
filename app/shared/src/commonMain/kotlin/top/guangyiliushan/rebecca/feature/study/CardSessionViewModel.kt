package top.guangyiliushan.rebecca.feature.study

import androidx.lifecycle.ViewModel
import kotlin.time.Clock
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import top.guangyiliushan.rebecca.core.RebeccaData
import top.guangyiliushan.rebecca.core.logic.applyCardDecision
import top.guangyiliushan.rebecca.core.logic.cardEvent
import top.guangyiliushan.rebecca.core.logic.cardSessionSummary
import top.guangyiliushan.rebecca.core.model.SenseId
import top.guangyiliushan.rebecca.core.repository.MasteryRepository

/** 卡片面（正面=headword+ipa，背面=definition+pos）。 */
enum class CardFace { Front, Back }

/** 卡片会话状态（0.1.2；判定真实写 mastery/events——Q3(b) 裁决）。 */
data class CardSessionUiState(
    val listId: String = "",
    val listTitle: String = "",
    val index: Int = 0,
    val total: Int = 0,
    val face: CardFace = CardFace.Front,
    val senseId: SenseId? = null,
    val headword: String = "",
    val ipa: String? = null,
    val pos: String? = null,
    val definition: String = "",
    val knownCount: Int = 0,
    val unknownCount: Int = 0,
    val finished: Boolean = false,
    val summary: top.guangyiliushan.rebecca.core.logic.CardSessionSummary? = null,
    val elapsedSeconds: Long = 0,
)

sealed interface CardUiEvent {
    data object Flip : CardUiEvent
    data class Decide(val known: Boolean) : CardUiEvent
}

class CardSessionViewModel(
    listId: String,
    private val masteryRepo: MasteryRepository = RebeccaData.mastery, // 注入缝：测试用 fake 隔离共享 demo 单例
    private val clockSeconds: (() -> Long)? = null,
) : ViewModel() {
    private var startMark = Clock.System.now().epochSeconds
    private val decisions = mutableListOf<Boolean>()
    private val senseIds: List<SenseId>
    private val _uiState: MutableStateFlow<CardSessionUiState>
    val uiState: StateFlow<CardSessionUiState>

    init {
        val account = RebeccaData.demoAccount
        senseIds = RebeccaData.wordLists.entries(account, listId).map { it.senseId }
        val list = RebeccaData.wordLists.lists(account).firstOrNull { it.id == listId }
        // R-D3：空词单不得进会话（详情屏 Practice 已禁用；此处兜底防 IndexOutOfBounds）
        _uiState = MutableStateFlow(
            if (senseIds.isEmpty()) {
                CardSessionUiState(
                    listId = listId,
                    listTitle = list?.title.orEmpty(),
                    total = 0,
                    finished = true,
                    summary = cardSessionSummary(emptyList()),
                )
            } else {
                cardAt(listId, list?.title.orEmpty(), 0, decisions)
            },
        )
        uiState = _uiState
    }

    fun onEvent(event: CardUiEvent) {
        when (event) {
            CardUiEvent.Flip -> _uiState.value = _uiState.value.copy(
                face = if (_uiState.value.face == CardFace.Front) CardFace.Back else CardFace.Front,
            )

            is CardUiEvent.Decide -> {
                val current = _uiState.value
                if (current.finished) return
                val senseId = current.senseId ?: return
                val now = Clock.System.now()
                val account = RebeccaData.demoAccount
                val prev = masteryRepo.mastery(account, senseId)
                // 掌握度真实写入（Q3(b)：卡片判定 → SenseMastery + MasteryEvent）
                masteryRepo.upsert(applyCardDecision(prev, account, senseId, event.known, now))
                masteryRepo.record(
                    cardEvent(
                        accountId = account,
                        senseId = senseId,
                        known = event.known,
                        occurredAt = now,
                        sequence = decisions.size,
                    ),
                )
                decisions += event.known
                val nextIndex = current.index + 1
                _uiState.value = if (nextIndex >= current.total) {
                    current.copy(
                        index = nextIndex,
                        knownCount = decisions.count { it },
                        unknownCount = decisions.count { !it },
                        finished = true,
                        summary = cardSessionSummary(decisions),
                        elapsedSeconds = elapsedSeconds(),
                    )
                } else {
                    cardAt(current.listId, current.listTitle, nextIndex, decisions)
                }
            }
        }
    }

    private fun elapsedSeconds(): Long =
        clockSeconds?.invoke() ?: (Clock.System.now().epochSeconds - startMark).coerceAtLeast(0)

    private fun cardAt(listId: String, listTitle: String, index: Int, decisions: List<Boolean>): CardSessionUiState {
        val account = RebeccaData.demoAccount
        val senseId = senseIds[index]
        val sense = RebeccaData.contentDictionary.sense(senseId)
        val lemma = sense?.let { RebeccaData.contentDictionary.lemma(it.lemmaId) }
        return CardSessionUiState(
            listId = listId,
            listTitle = listTitle,
            index = index,
            total = senseIds.size,
            face = CardFace.Front,
            senseId = senseId,
            headword = lemma?.form.orEmpty(),
            ipa = sense?.ipa,
            pos = sense?.partOfSpeech,
            definition = sense?.definition.orEmpty(),
            knownCount = decisions.count { it },
            unknownCount = decisions.count { !it },
        )
    }
}
