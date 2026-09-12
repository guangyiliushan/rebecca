package top.guangyiliushan.rebecca.feature.study

import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import kotlin.test.Test
import kotlin.test.assertTrue
import top.guangyiliushan.rebecca.core.model.QuizMode
import top.guangyiliushan.rebecca.design.theme.AppTheme
import top.guangyiliushan.rebecca.design.theme.ThemeSettings

/**
 * 会话屏语义冒烟（0.1.1，grill Q11 ③）：选项按钮角色 / 答错反馈（Polite）/ 结算态可达。
 * 文案断言用 values/（en）实际值；测试文件豁免 F6（f_gates 白名单）。
 */
@OptIn(ExperimentalTestApi::class)
class QuizScreenSemanticsTest {
    private fun hasRole(role: Role) = SemanticsMatcher("role=$role") {
        it.config.getOrElseNullable(SemanticsProperties.Role) { null } == role
    }

    private fun hasLiveRegion(mode: LiveRegionMode) = SemanticsMatcher("liveRegion=$mode") {
        it.config.getOrElseNullable(SemanticsProperties.LiveRegion) { null } == mode
    }

    @Test
    fun optionsExposeButtonRole() = runComposeUiTest {
        val vm = QuizViewModel(QuizMode.REVIEW)
        setContent {
            AppTheme(ThemeSettings()) {
                QuizScreen(mode = QuizMode.REVIEW, onBackToHub = {}, viewModel = vm)
            }
        }
        val forms = vm.uiState.value.optionForms
        assertTrue(forms.size == 4)
        onNodeWithText(forms[0]).assert(hasRole(Role.Button))
    }

    @Test
    fun wrongChoice_showsPoliteFeedback() = runComposeUiTest {
        val vm = QuizViewModel(QuizMode.REVIEW)
        setContent {
            AppTheme(ThemeSettings()) {
                QuizScreen(mode = QuizMode.REVIEW, onBackToHub = {}, viewModel = vm)
            }
        }
        val q = vm.uiState.value.question!!
        val wrong = (0..3).first { it != q.correctIndex }
        onNodeWithText(vm.uiState.value.optionForms[wrong]).performClick()
        onNodeWithText("Not quite — try again").assertExists()
        onNodeWithText("Not quite — try again").assert(hasLiveRegion(LiveRegionMode.Polite))
    }

    @Test
    fun completedSession_showsResultState() = runComposeUiTest {
        val vm = QuizViewModel(QuizMode.REVIEW)
        var guard = 0
        while (!vm.uiState.value.finished && guard++ < 50) {
            vm.onEvent(QuizUiEvent.OptionChosen(vm.uiState.value.question!!.correctIndex))
            vm.onEvent(QuizUiEvent.Next)
        }
        assertTrue(vm.uiState.value.finished)
        setContent {
            AppTheme(ThemeSettings()) {
                QuizResultScreen(state = vm.uiState.value, onRetry = {}, onBackToHub = {})
            }
        }
        onNodeWithText("Session complete").assertExists()
    }
}
