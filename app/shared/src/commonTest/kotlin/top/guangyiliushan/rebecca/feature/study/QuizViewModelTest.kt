package top.guangyiliushan.rebecca.feature.study

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import top.guangyiliushan.rebecca.core.model.QuizMode

class QuizViewModelTest {
    private fun vm(mode: QuizMode) = QuizViewModel(mode)

    @Test
    fun wrongChoice_allowsRetryAndMarksWrong() {
        val vm = vm(QuizMode.REVIEW)
        val q = vm.uiState.value.question!!
        val correct = q.correctIndex
        val wrong = (0..3).first { it != correct }
        vm.onEvent(QuizUiEvent.OptionChosen(wrong))
        assertTrue(wrong in vm.uiState.value.wrongIndices)
        assertFalse(vm.uiState.value.answeredCorrect)
        vm.onEvent(QuizUiEvent.OptionChosen(correct))
        assertTrue(vm.uiState.value.answeredCorrect)
        assertEquals(1, vm.uiState.value.correctCount)
    }

    @Test
    fun correctChoice_showsDetailCardState() {
        val vm = vm(QuizMode.LEARN)
        vm.onEvent(QuizUiEvent.OptionChosen(vm.uiState.value.question!!.correctIndex))
        assertTrue(vm.uiState.value.answeredCorrect)
        assertEquals(4, vm.uiState.value.optionForms.size)
    }

    @Test
    fun nextUntilEnd_finishesWithAccuracyAndTime() {
        val vm = vm(QuizMode.MIXED)
        var guard = 0
        while (!vm.uiState.value.finished && guard++ < 50) {
            vm.onEvent(QuizUiEvent.OptionChosen(vm.uiState.value.question!!.correctIndex))
            vm.onEvent(QuizUiEvent.Next)
        }
        assertTrue(vm.uiState.value.finished)
        assertEquals(
            vm.uiState.value.correctCount.toDouble() / vm.uiState.value.total,
            1.0,
            1e-9,
        )
    }

    @Test
    fun retry_reassemblesSession() {
        val vm = vm(QuizMode.REVIEW)
        val first = vm.uiState.value.question
        vm.onEvent(QuizUiEvent.OptionChosen(first!!.correctIndex))
        vm.onEvent(QuizUiEvent.Next)
        vm.onEvent(QuizUiEvent.Retry)
        assertEquals(0, vm.uiState.value.index)
        assertEquals(0, vm.uiState.value.correctCount)
    }
}
