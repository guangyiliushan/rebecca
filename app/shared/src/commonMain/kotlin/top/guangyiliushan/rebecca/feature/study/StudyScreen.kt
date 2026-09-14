package top.guangyiliushan.rebecca.feature.study

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import rebecca.app.shared.generated.resources.Res
import rebecca.app.shared.generated.resources.action_cancel
import rebecca.app.shared.generated.resources.cta_practice_vocab_instead
import rebecca.app.shared.generated.resources.cta_today_grammar
import rebecca.app.shared.generated.resources.cta_today_listening
import rebecca.app.shared.generated.resources.cta_today_vocabulary
import rebecca.app.shared.generated.resources.focus_accuracy
import rebecca.app.shared.generated.resources.focus_badge_mixed
import rebecca.app.shared.generated.resources.focus_cap_image_association
import rebecca.app.shared.generated.resources.focus_cap_multiple_choice
import rebecca.app.shared.generated.resources.focus_cap_spelling
import rebecca.app.shared.generated.resources.focus_daily_goal
import rebecca.app.shared.generated.resources.focus_goal_edit
import rebecca.app.shared.generated.resources.focus_list_change
import rebecca.app.shared.generated.resources.focus_no_reviews
import rebecca.app.shared.generated.resources.focus_option_grammar
import rebecca.app.shared.generated.resources.focus_option_listening
import rebecca.app.shared.generated.resources.focus_option_vocabulary
import rebecca.app.shared.generated.resources.focus_planned_grammar
import rebecca.app.shared.generated.resources.focus_planned_listening
import rebecca.app.shared.generated.resources.focus_progress
import rebecca.app.shared.generated.resources.focus_title_grammar
import rebecca.app.shared.generated.resources.focus_title_listening
import rebecca.app.shared.generated.resources.focus_title_vocabulary
import rebecca.app.shared.generated.resources.focus_word_count
import rebecca.app.shared.generated.resources.focus_word_list
import rebecca.app.shared.generated.resources.focus_word_list_adaptive
import rebecca.app.shared.generated.resources.focus_word_list_adaptive_desc
import rebecca.app.shared.generated.resources.goal_decrease
import rebecca.app.shared.generated.resources.goal_increase
import rebecca.app.shared.generated.resources.goal_save
import rebecca.app.shared.generated.resources.goal_sheet_title
import rebecca.app.shared.generated.resources.hub_due
import rebecca.app.shared.generated.resources.hub_new
import rebecca.app.shared.generated.resources.list_adaptive_desc
import rebecca.app.shared.generated.resources.list_sheet_title
import rebecca.app.shared.generated.resources.qa_books
import rebecca.app.shared.generated.resources.qa_challenges
import rebecca.app.shared.generated.resources.qa_chat
import rebecca.app.shared.generated.resources.qa_planned
import rebecca.app.shared.generated.resources.qa_store
import rebecca.app.shared.generated.resources.streak_practiced_days
import rebecca.app.shared.generated.resources.streak_title
import top.guangyiliushan.rebecca.core.model.QuizMode
import top.guangyiliushan.rebecca.design.components.AppButton
import top.guangyiliushan.rebecca.design.components.AppButtonVariant
import top.guangyiliushan.rebecca.design.components.AppIconButton
import top.guangyiliushan.rebecca.design.components.AppListGroup
import top.guangyiliushan.rebecca.design.components.AppListRow
import top.guangyiliushan.rebecca.design.components.AppListRowSeparator
import top.guangyiliushan.rebecca.design.overlays.AppSheet
import top.guangyiliushan.rebecca.design.overlays.AppSheetFooter
import top.guangyiliushan.rebecca.design.overlays.AppSheetHeader
import top.guangyiliushan.rebecca.design.patterns.FocusOption
import top.guangyiliushan.rebecca.design.patterns.HomeQuickActionBar
import top.guangyiliushan.rebecca.design.patterns.LearningFocusCard
import top.guangyiliushan.rebecca.design.patterns.LearningOverviewCard
import top.guangyiliushan.rebecca.design.patterns.OverviewStat
import top.guangyiliushan.rebecca.design.patterns.QuickAction
import top.guangyiliushan.rebecca.design.theme.AppTheme
import top.guangyiliushan.rebecca.design.tokens.AppContentWidth

/**
 * 学习中枢（roadmap 0.1.1 + 0.1.1 补：Study 富化，ui-design-survey 05/08 规格 + 用户实测裁定）：
 * LearningFocusCard（唯一主 CTA = Mixed 会话）→ LearningOverviewCard（单张大卡：连续学习 pipe
 * + due/new 统计等宽居中）→ HomeQuickActionBar（四按钮横向条带，flows 未落地前诚实禁用）。
 * 用户实测裁定：删除 More practice（过时设计）与近期词单区（Lists 有独立屏）。
 * 断点由 AppScaffold 管（F-L4）；内容宽度走 AppContentWidth（expanded 不拉面条）。
 * 08 §Interaction state machine 的 No words / Session starting 两态在 demo 数据下不可达
 * （demo 恒有词、无异步加载）——0.2.x 接真实数据源时实现，不在本批。
 */
@Composable
fun StudyScreen(
    onModeChosen: (QuizMode) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: StudyHubViewModel = viewModel { StudyHubViewModel() },
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val spacing = AppTheme.spacing
    var goalSheetOpen by rememberSaveable { mutableStateOf(false) }
    var listSheetOpen by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(spacing.md),
        verticalArrangement = Arrangement.spacedBy(spacing.md),
    ) {
        // 学习焦点卡第一（08 §Screen order：首屏内主 CTA 可见）：唯一主 CTA；
        // 占位焦点（Reading/Grammar）诚实解释 + 词汇保底动作
        val activeList = state.quickLists.firstOrNull { it.id == state.activeListId }
        LearningFocusCard(
            modifier = Modifier.fillMaxWidth().widthIn(max = AppContentWidth.default),
            title = focusTitle(state.focus),
            badge = stringResource(Res.string.focus_badge_mixed),
            focusOptions = LearningFocus.entries.map { focus ->
                FocusOption(
                    label = focusLabel(focus),
                    selected = state.focus == focus,
                    onSelect = { viewModel.onFocusSelect(focus) },
                )
            },
            accuracyLabel = state.accuracyPercent?.let {
                // % 随参数传入：CMP 格式化器不折叠 %%（用户实测双 % 渲染异常）
                stringResource(Res.string.focus_accuracy, "$it%")
            } ?: stringResource(Res.string.focus_no_reviews),
            goalLabel = pluralStringResource(
                Res.plurals.focus_daily_goal,
                state.dailyGoal,
                state.dailyGoal,
            ),
            onEditGoal = { goalSheetOpen = true },
            capabilityLabels = if (state.focus == LearningFocus.Vocabulary) {
                listOf(
                    stringResource(Res.string.focus_cap_multiple_choice),
                    stringResource(Res.string.focus_cap_spelling),
                    stringResource(Res.string.focus_cap_image_association),
                )
            } else {
                emptyList()
            },
            placeholderReason = when (state.focus) {
                LearningFocus.Vocabulary -> null
                LearningFocus.ListeningReading -> stringResource(Res.string.focus_planned_listening)
                LearningFocus.Grammar -> stringResource(Res.string.focus_planned_grammar)
            },
            progress = if (state.dailyGoal > 0) {
                (state.completedToday.toFloat() / state.dailyGoal).coerceIn(0f, 1f)
            } else {
                null
            },
            progressLabel = pluralStringResource(
                Res.plurals.focus_progress,
                state.dailyGoal,
                state.completedToday,
                state.dailyGoal,
            ),
            wordListLabel = stringResource(Res.string.focus_word_list),
            wordListValue = activeList?.title
                ?: stringResource(Res.string.focus_word_list_adaptive),
            wordListCount = activeList?.let {
                pluralStringResource(Res.plurals.focus_word_count, it.wordCount, it.wordCount)
            } ?: stringResource(Res.string.focus_word_list_adaptive_desc),
            onChangeList = { listSheetOpen = true },
            ctaLabel = ctaFor(state.focus),
            onCtaClick = { onModeChosen(QuizMode.MIXED) },
            editActionLabel = stringResource(Res.string.focus_goal_edit),
            changeActionLabel = stringResource(Res.string.focus_list_change),
            practiceInsteadLabel = stringResource(Res.string.cta_practice_vocab_instead),
            onPracticeInsteadClick = { viewModel.onFocusSelect(LearningFocus.Vocabulary) },
        )

        // 学习概览大卡：连续学习 + 今日进度合并（用户实测：分离排版不对称 → 单卡等宽居中）
        LearningOverviewCard(
            modifier = Modifier.fillMaxWidth().widthIn(max = AppContentWidth.default),
            title = pluralStringResource(Res.plurals.streak_title, state.streak, state.streak),
            summary = stringResource(Res.string.streak_practiced_days, state.practicedDays, 7),
            days = state.activity,
            stats = listOf(
                OverviewStat(value = state.dueCount.toString(), label = stringResource(Res.string.hub_due)),
                OverviewStat(value = state.newCount.toString(), label = stringResource(Res.string.hub_new)),
            ),
        )

        // 四按钮快捷条带（flows 0.1.3/0.1.4 落地前诚实禁用 + 说明）
        HomeQuickActionBar(
            modifier = Modifier.fillMaxWidth().widthIn(max = AppContentWidth.default),
            actions = listOf(
                QuickAction(label = stringResource(Res.string.qa_store), onClick = {}, enabled = false),
                QuickAction(label = stringResource(Res.string.qa_challenges), onClick = {}, enabled = false),
                QuickAction(label = stringResource(Res.string.qa_chat), onClick = {}, enabled = false),
                QuickAction(label = stringResource(Res.string.qa_books), onClick = {}, enabled = false),
            ),
            caption = stringResource(Res.string.qa_planned),
        )
    }

    // 常驻组合（review 🟡8：保留进出场动画；stepper 值在 open 变化时重置）
    GoalEditorSheet(
        open = goalSheetOpen,
        current = state.dailyGoal,
        onDismiss = { goalSheetOpen = false },
        onSave = {
            viewModel.onGoalSave(it)
            goalSheetOpen = false
        },
    )
    ListPickerSheet(
        open = listSheetOpen,
        activeListId = state.activeListId,
        lists = state.quickLists,
        onDismiss = { listSheetOpen = false },
        onSelect = {
            viewModel.onListSelect(it)
            listSheetOpen = false
        },
    )
}

@Composable
private fun focusTitle(focus: LearningFocus): String = stringResource(
    when (focus) {
        LearningFocus.Vocabulary -> Res.string.focus_title_vocabulary
        LearningFocus.ListeningReading -> Res.string.focus_title_listening
        LearningFocus.Grammar -> Res.string.focus_title_grammar
    },
)

@Composable
private fun focusLabel(focus: LearningFocus): String = stringResource(
    when (focus) {
        LearningFocus.Vocabulary -> Res.string.focus_option_vocabulary
        LearningFocus.ListeningReading -> Res.string.focus_option_listening
        LearningFocus.Grammar -> Res.string.focus_option_grammar
    },
)

@Composable
private fun ctaFor(focus: LearningFocus): String = stringResource(
    when (focus) {
        LearningFocus.Vocabulary -> Res.string.cta_today_vocabulary
        LearningFocus.ListeningReading -> Res.string.cta_today_listening
        LearningFocus.Grammar -> Res.string.cta_today_grammar
    },
)

/** 每日目标编辑 sheet（08 spec §Goal editor）：stepper 1..99，边界禁用，保存经偏好持久化。 */
@Composable
private fun GoalEditorSheet(
    open: Boolean,
    current: Int,
    onDismiss: () -> Unit,
    onSave: (Int) -> Unit,
) {
    var value by remember(open) { mutableIntStateOf(current) } // open 变化时重置（对齐卡面现值）
    val spacing = AppTheme.spacing
    AppSheet(open = open, onDismiss = onDismiss) {
        AppSheetHeader(title = { Text(stringResource(Res.string.goal_sheet_title)) })
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = spacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            AppIconButton(
                onClick = { if (value > 1) value-- },
                icon = { Icon(Icons.Filled.KeyboardArrowDown, contentDescription = null) },
                contentDescription = stringResource(Res.string.goal_decrease),
                enabled = value > 1,
            )
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.titleLarge,
                color = AppTheme.colors.onSurface,
                modifier = Modifier.padding(horizontal = spacing.md),
            )
            AppIconButton(
                onClick = { if (value < 99) value++ },
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                contentDescription = stringResource(Res.string.goal_increase),
                enabled = value < 99,
            )
        }
        Text(
            text = pluralStringResource(Res.plurals.focus_word_count, value, value),
            style = MaterialTheme.typography.bodyMedium,
            color = AppTheme.colors.onMuted,
            modifier = Modifier.fillMaxWidth().padding(horizontal = spacing.md),
        )
        AppSheetFooter {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
            ) {
                AppButton(
                    text = stringResource(Res.string.action_cancel),
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    variant = AppButtonVariant.Ghost,
                )
                AppButton(
                    text = stringResource(Res.string.goal_save),
                    onClick = { onSave(value) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

/** 词单选择 sheet（08 spec §Word list selector）：Adaptive 默认且可恢复；选中标记 stateDescription。 */
@Composable
private fun ListPickerSheet(
    open: Boolean,
    activeListId: String?,
    lists: List<QuickList>,
    onDismiss: () -> Unit,
    onSelect: (String?) -> Unit,
) {
    val spacing = AppTheme.spacing
    AppSheet(open = open, onDismiss = onDismiss) {
        AppSheetHeader(title = { Text(stringResource(Res.string.list_sheet_title)) })
        AppListGroup {
            AppListRow(
                title = { Text(stringResource(Res.string.focus_word_list_adaptive)) },
                description = { Text(stringResource(Res.string.list_adaptive_desc)) },
                selected = activeListId == null,
                onClick = { onSelect(null) },
            )
            AppListRowSeparator()
            lists.forEachIndexed { index, list ->
                AppListRow(
                    title = { Text(list.title) },
                    description = {
                        Text(
                            pluralStringResource(
                                Res.plurals.focus_word_count,
                                list.wordCount,
                                list.wordCount,
                            ),
                        )
                    },
                    selected = activeListId == list.id,
                    onClick = { onSelect(list.id) },
                )
                if (index < lists.lastIndex) AppListRowSeparator()
            }
        }
        AppSheetFooter {
            AppButton(
                text = stringResource(Res.string.action_cancel),
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                variant = AppButtonVariant.Ghost,
            )
        }
    }
}
