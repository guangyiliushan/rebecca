package top.guangyiliushan.rebecca.feature.study

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.random.Random
import kotlin.time.ExperimentalTime
import kotlin.time.TimeMark
import kotlin.time.TimeSource
import top.guangyiliushan.rebecca.core.RebeccaData
import top.guangyiliushan.rebecca.core.logic.QuizQuestion
import top.guangyiliushan.rebecca.core.logic.buildQuestion
import top.guangyiliushan.rebecca.core.logic.isCorrect
import top.guangyiliushan.rebecca.core.logic.selectCandidates
import top.guangyiliushan.rebecca.core.model.QuizMode

data class QuizUiState(
    val mode: QuizMode,
    val question: QuizQuestion? = null,      // null = 无题可练（空态）
    val index: Int = 0,
    val total: Int = 0,
    val selectedIndex: Int? = null,
    val wrongIndices: Set<Int> = emptySet(), // 本题已错选项（标红 + 可继续选；仅内存，Q7）
    val answeredCorrect: Boolean = false,    // 选对 → 详解卡态
    val correctCount: Int = 0,
    val finished: Boolean = false,
    val elapsedSeconds: Long = 0,
    val optionForms: List<String> = emptyList(), // lemmaId → form 的 UI 侧解析结果
)

sealed interface QuizUiEvent {
    data class OptionChosen(val index: Int) : QuizUiEvent
    data object Next : QuizUiEvent
    data object Retry : QuizUiEvent
}

/**
 * 会话编排（R1：只编排，判分/选词在 core；0.2.0 下沉状态机时判分零重写）。
 * Q7 裁定：0.1.1 不写 SenseMastery/不生成 MasteryEvent。
 */
@OptIn(ExperimentalTime::class)
class QuizViewModel(
    private val mode: QuizMode,
    // 测试注入：null 时用真实时钟（TimeSource.Monotonic，四端通用；commonMain 无 System.currentTimeMillis）
    private val clockSeconds: (() -> Long)? = null,
) : ViewModel() {
    private val _uiState: MutableStateFlow<QuizUiState>
    val uiState: StateFlow<QuizUiState>

    private var queue: List<QuizQuestion> = emptyList()
    private var startMark: TimeMark = TimeSource.Monotonic.markNow()

    init {
        _uiState = MutableStateFlow(assemble())
        uiState = _uiState
    }

    private fun elapsedSeconds(): Long =
        (clockSeconds?.invoke() ?: startMark.elapsedNow().inWholeSeconds).coerceAtLeast(0)

    fun onEvent(event: QuizUiEvent) {
        val s = _uiState.value
        when (event) {
            is QuizUiEvent.OptionChosen -> onChosen(s, event.index)
            QuizUiEvent.Next -> onNext(s)
            QuizUiEvent.Retry -> {
                _uiState.value = assemble()
            }
        }
    }

    private fun onChosen(s: QuizUiState, chosen: Int) {
        val q = s.question ?: return
        if (s.answeredCorrect) return
        val correct = isCorrect(q, chosen)
        if (correct) {
            _uiState.value = s.copy(
                selectedIndex = chosen,
                answeredCorrect = true,
                correctCount = s.correctCount + 1,
            )
            // 末题不再立即 finalize：先给详解卡，Next 才结算（review P2-6）
        } else {
            _uiState.value = s.copy(
                selectedIndex = chosen,
                wrongIndices = s.wrongIndices + chosen,
            )
        }
    }

    private fun onNext(s: QuizUiState) {
        if (!s.answeredCorrect) return // 答对才能进下一题
        if (s.index + 1 >= s.total) {
            finalize(s.correctCount)
            return
        }
        val next = queue[s.index + 1]
        _uiState.value = s.copy(
            question = next,
            index = s.index + 1,
            selectedIndex = null,
            wrongIndices = emptySet(),
            answeredCorrect = false,
            optionForms = resolveForms(next),
        )
    }

    private fun finalize(correctCount: Int) {
        _uiState.value = _uiState.value.copy(
            finished = true,
            correctCount = correctCount,
            elapsedSeconds = elapsedSeconds(),
        )
    }

    private fun assemble(): QuizUiState {
        startMark = TimeSource.Monotonic.markNow() // Retry 重开会话时重置计时
        val account = RebeccaData.demoAccount
        val all = RebeccaData.contentDictionary.allSenses()
        val masteredIds = RebeccaData.mastery.allFor(account).map { it.senseId }.toSet()
        val new = all.filter { it.id !in masteredIds }
        val due = RebeccaData.study.dueSenses(account, limit = 1000)
        val candidates = selectCandidates(mode, new, due, limit = 20, rng = Random.Default)
        queue = candidates.mapNotNull { buildQuestion(it, all) }
        val first = queue.firstOrNull()
        return QuizUiState(
            mode = mode,
            question = first,
            total = queue.size,
            optionForms = first?.let { resolveForms(it) } ?: emptyList(),
        )
    }

    /** lemmaId → lemma form（UI 显示用）；解析失败兜底显示 lemmaId，不崩。 */
    private fun resolveForms(q: QuizQuestion): List<String> =
        q.optionLemmaIds.map { id ->
            RebeccaData.contentDictionary.lemma(id)?.form ?: id
        }
}
