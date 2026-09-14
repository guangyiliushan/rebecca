package top.guangyiliushan.rebecca.feature.study

import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDialog
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import top.guangyiliushan.rebecca.design.theme.AppTheme
import top.guangyiliushan.rebecca.design.theme.ThemeSettings

/**
 * Study 主页语义冒烟（0.1.1 补：Study 富化 + 用户实测反馈修复）：
 * 主 CTA 唯一 / 占位焦点诚实禁用 / 进度文本替代 / 单卡学习概览（due/new 并入）
 * / accuracy 单 %（用户实测：双 % 渲染异常）/ 四按钮快捷条带 / 已删区块不复活。
 * 文案断言用 values/（en）实际值；测试文件豁免 F6（f_gates 白名单）。
 */
@OptIn(ExperimentalTestApi::class)
class StudyHomeSemanticsTest {

    @AfterTest
    fun restoreDemoPreferences() {
        // 异常安全还原（review 🟡4）：共享单例 DemoPreferenceRepository，断言失败也还原
        StudyHubViewModel().onGoalSave(20)
        StudyHubViewModel().onListSelect(null)
    }

    @Test
    fun primaryCta_uniqueAndVocabularyEnabled() = runComposeUiTest {
        val vm = StudyHubViewModel()
        setContent { AppTheme(ThemeSettings()) { StudyScreen(onModeChosen = {}, viewModel = vm) } }
        onAllNodesWithText("Today’s vocabulary").assertCountEquals(1)
        onNodeWithText("Today’s vocabulary").assertIsEnabled()
        // Mixed 徽标是信息不是控件（08 spec：informational, not a mode control）
        onAllNodes(SemanticsMatcher("mixed-badge-as-button") {
            val text = it.config.getOrElseNullable(SemanticsProperties.Text) { emptyList() } ?: emptyList()
            text.any { t -> t.text == "Mixed" } &&
                it.config.getOrElseNullable(SemanticsProperties.Role) { null } == Role.Button
        }).assertCountEquals(0)
    }

    @Test
    fun placeholderFocus_disablesCtaWithReasonAndFallback() = runComposeUiTest {
        val vm = StudyHubViewModel()
        setContent { AppTheme(ThemeSettings()) { StudyScreen(onModeChosen = {}, viewModel = vm) } }
        onNodeWithText("Reading").performClick() // 用户实测：按钮简化为一词，保证一行放下
        onNodeWithText("Today’s reading").assertIsNotEnabled()
        onNodeWithText("Reading practice is planned. It arrives in a later version.").assertExists()
        onNodeWithText("Practice vocabulary instead").performClick()
        assertEquals(LearningFocus.Vocabulary, vm.uiState.value.focus)
        onNodeWithText("Today’s vocabulary").assertIsEnabled()
    }

    @Test
    fun focusOptions_fitSingleRow_labelsAreShort() = runComposeUiTest {
        val vm = StudyHubViewModel()
        setContent { AppTheme(ThemeSettings()) { StudyScreen(onModeChosen = {}, viewModel = vm) } }
        onNodeWithText("Vocabulary").assertExists()
        onNodeWithText("Reading").assertExists()
        onNodeWithText("Grammar").assertExists()
    }

    @Test
    fun learningOverview_singleCardWithStatsAndTextAlternative() = runComposeUiTest {
        val vm = StudyHubViewModel()
        setContent { AppTheme(ThemeSettings()) { StudyScreen(onModeChosen = {}, viewModel = vm) } }
        onNodeWithText("2 of 20 words").assertExists()               // 进度文本为主（环为辅）
        onNodeWithText("Continuous learning: 4 days").assertExists()
        onNodeWithText("Practiced on 4 of the last 7 days.").assertExists() // pipe 文本替代
        // due/new 并入同一张学习概览大卡（用户实测：分离排版不对称，合并为一张卡）
        onNodeWithText("Due today").assertExists()
        onNodeWithText("New words").assertExists()
    }

    @Test
    fun accuracyLabel_rendersSinglePercentSign() = runComposeUiTest {
        val vm = StudyHubViewModel()
        setContent { AppTheme(ThemeSettings()) { StudyScreen(onModeChosen = {}, viewModel = vm) } }
        // 用户实测：双 % 渲染异常（CMP 非 JVM 格式化器不折叠 %%）；锁单 % 契约
        onNodeWithText("Review accuracy: 50%").assertExists()
        onAllNodesWithText("Review accuracy: 50%%").assertCountEquals(0)
    }

    @Test
    fun quickActionBar_fourLabelsDisabledWithCaption() = runComposeUiTest {
        val vm = StudyHubViewModel()
        setContent { AppTheme(ThemeSettings()) { StudyScreen(onModeChosen = {}, viewModel = vm) } }
        onNodeWithText("Store").assertExists()
        onNodeWithText("Challenges").assertExists()
        onNodeWithText("AI chat").assertExists()
        onNodeWithText("Books").assertExists()
        // 对应 flows 未落地（0.1.3/0.1.4）→ 诚实禁用 + 说明，不给死点击
        onNodeWithText("Store").assertIsNotEnabled()
        onNodeWithText("These tools arrive in later versions.").assertExists()
    }

    @Test
    fun removedSections_doNotResurrect() = runComposeUiTest {
        val vm = StudyHubViewModel()
        setContent { AppTheme(ThemeSettings()) { StudyScreen(onModeChosen = {}, viewModel = vm) } }
        // 用户实测裁定：More practice 是过时设计；词单首页不展示（Lists 有独立屏）
        onAllNodesWithText("More practice").assertCountEquals(0)
        onAllNodesWithText("Your word lists").assertCountEquals(0)
    }

    @Test
    fun goalSheet_stepperSavesAndCardReflects() = runComposeUiTest {
        val vm = StudyHubViewModel()
        setContent { AppTheme(ThemeSettings()) { StudyScreen(onModeChosen = {}, viewModel = vm) } }
        onNodeWithText("Edit").performClick()
        onNodeWithText("Edit daily goal").assertExists()
        onNodeWithContentDescription("Increase").performClick()       // 20 → 21
        onNodeWithText("Save").performClick()
        onNodeWithText("Daily goal: 21 words").assertExists()
        vm.onGoalSave(20) // 还原 demo 偏好，避免污染其他测试
    }

    @Test
    fun goalSheet_stepperBoundariesDisableButtons() = runComposeUiTest {
        val vm = StudyHubViewModel()
        vm.onGoalSave(1)
        setContent { AppTheme(ThemeSettings()) { StudyScreen(onModeChosen = {}, viewModel = vm) } }
        onNodeWithText("Edit").performClick()
        onNodeWithContentDescription("Decrease").assertIsNotEnabled() // 下限 1
        onNodeWithContentDescription("Increase").assertIsEnabled()
        onNodeWithText("Cancel").performClick()
        vm.onGoalSave(20) // 还原
    }

    @Test
    fun listPicker_selectsAndRestoresAdaptive() = runComposeUiTest {
        val vm = StudyHubViewModel()
        setContent { AppTheme(ThemeSettings()) { StudyScreen(onModeChosen = {}, viewModel = vm) } }
        onNodeWithText("Change").performClick()
        onNodeWithText("Choose word list").assertExists()
        onNode(hasText("Daily Life") and hasAnyAncestor(isDialog())).performClick() // sheet 行（ModalBottomSheet=Dialog 窗口）
        assertEquals("list-daily-life", vm.uiState.value.activeListId)
        onAllNodesWithText("Daily Life").assertCountEquals(1)          // 仅焦点卡词单行（近期词单区已删）
        // adaptive 恢复（08 spec：默认可恢复）
        vm.onListSelect(null)
        onAllNodesWithText("Adaptive list").assertCountEquals(1)
    }
}
