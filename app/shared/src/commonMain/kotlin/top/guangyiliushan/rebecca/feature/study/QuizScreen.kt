package top.guangyiliushan.rebecca.feature.study

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jetbrains.compose.resources.stringResource
import rebecca.app.shared.generated.resources.Res
import rebecca.app.shared.generated.resources.action_back
import rebecca.app.shared.generated.resources.quiz_correct
import rebecca.app.shared.generated.resources.quiz_detail_examples
import rebecca.app.shared.generated.resources.quiz_detail_pronunciation
import rebecca.app.shared.generated.resources.quiz_detail_word_family
import rebecca.app.shared.generated.resources.quiz_empty_no_words
import rebecca.app.shared.generated.resources.quiz_next
import rebecca.app.shared.generated.resources.quiz_progress
import rebecca.app.shared.generated.resources.quiz_question_hint
import rebecca.app.shared.generated.resources.quiz_screen_title
import rebecca.app.shared.generated.resources.quiz_view_results
import rebecca.app.shared.generated.resources.quiz_wrong_retry
import top.guangyiliushan.rebecca.core.model.QuizMode
import top.guangyiliushan.rebecca.design.components.AppButton
import top.guangyiliushan.rebecca.design.components.AppButtonVariant
import top.guangyiliushan.rebecca.design.components.AppCard
import top.guangyiliushan.rebecca.design.components.AppIconButton
import top.guangyiliushan.rebecca.design.components.AppProgress
import top.guangyiliushan.rebecca.design.patterns.EmptyState
import top.guangyiliushan.rebecca.design.scaffold.AppTopBar
import top.guangyiliushan.rebecca.design.theme.AppTheme
import top.guangyiliushan.rebecca.design.tokens.AppContentWidth

/**
 * 选择题会话（roadmap 0.1.1）：题干=definition，选项=lemma form（四选一）。
 * 答题交互全态：错标红可续选（Q7 仅内存）、对出详解卡、结算转 QuizResultScreen。
 * 顶部 AppTopBar 统一返回（用户实测：桌面/Web 无系统返回手势，答题界面顶部必须有返回按钮；
 * 05 spec：Quiz 沉浸式、不装 AppIdentityBar，但需要自己的上下文头部）。
 * 结算态额外保留卡内 "Back to hub" 主行动（会话结束的自然出口），空态只留顶部返回（review 🟡3）。
 */
@Composable
fun QuizScreen(
    mode: QuizMode,
    onBackToHub: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: QuizViewModel = viewModel { QuizViewModel(mode) },
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize()) {
        AppTopBar(
            title = { Text(stringResource(Res.string.quiz_screen_title)) },
            navigationIcon = {
                AppIconButton(
                    onClick = onBackToHub,
                    icon = { Icon(Icons.Filled.ArrowBack, contentDescription = null) },
                    contentDescription = stringResource(Res.string.action_back),
                )
            },
        )

        when {
            state.finished -> QuizResultScreen(
                state = state,
                onRetry = { viewModel.onEvent(QuizUiEvent.Retry) },
                onBackToHub = onBackToHub,
                modifier = Modifier.fillMaxWidth().weight(1f),
            )

            state.question == null -> EmptyState(
                icon = Icons.Filled.Search,
                title = stringResource(Res.string.quiz_empty_no_words),
                modifier = Modifier.fillMaxWidth().weight(1f),
                // 无 action：顶部 AppTopBar 的返回已是唯一出口（review 🟡3：避免双重返回入口）
            )

            else -> QuizBody(
                state = state,
                onOptionChosen = { viewModel.onEvent(QuizUiEvent.OptionChosen(it)) },
                onNext = { viewModel.onEvent(QuizUiEvent.Next) },
                modifier = Modifier.fillMaxWidth().weight(1f),
            )
        }
    }
}

/** 答题主体（题干卡 + 四选项 + 反馈 + 详解卡）。 */
@Composable
private fun QuizBody(
    state: QuizUiState,
    onOptionChosen: (Int) -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = AppTheme.spacing
    val question = state.question ?: return // 防御：调用方已保证非空

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(spacing.md),
        verticalArrangement = Arrangement.spacedBy(spacing.md),
    ) {
        // 进度（不变量：question != null ⇒ total > 0，除法安全）
        Text(
            text = stringResource(Res.string.quiz_progress, state.index + 1, state.total),
            style = MaterialTheme.typography.labelMedium,
            color = AppTheme.colors.onMuted,
        )
        AppProgress(
            progress = (state.index + 1).toFloat() / state.total,
            modifier = Modifier.fillMaxWidth(),
        )

        // 题干卡：definition + 提示
        AppCard(modifier = Modifier.fillMaxWidth().widthIn(max = AppContentWidth.reading)) {
            Column(
                modifier = Modifier.padding(spacing.md),
                verticalArrangement = Arrangement.spacedBy(spacing.xs),
            ) {
                Text(
                    text = stringResource(Res.string.quiz_question_hint),
                    style = MaterialTheme.typography.labelMedium,
                    color = AppTheme.colors.onMuted,
                )
                Text(
                    text = question.target.definition,
                    style = MaterialTheme.typography.titleMedium,
                    color = AppTheme.colors.onSurface,
                )
            }
        }

        // 四选项：答对后正确项 Primary、其余禁点；错选标 Destructive 且可继续选
        Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
            state.optionForms.forEachIndexed { i, form ->
                val isWrong = i in state.wrongIndices
                val isCorrectChoice = state.answeredCorrect && i == state.selectedIndex
                AppButton(
                    text = form,
                    onClick = { onOptionChosen(i) },
                    modifier = Modifier.fillMaxWidth(),
                    variant = when {
                        isCorrectChoice -> AppButtonVariant.Primary
                        isWrong -> AppButtonVariant.Destructive
                        else -> AppButtonVariant.Outline
                    },
                    enabled = !state.answeredCorrect && !isWrong,
                )
            }
        }

        // 对错反馈（liveRegion=Polite，§10.6）
        if (state.selectedIndex != null) {
            val feedback = if (state.answeredCorrect) {
                stringResource(Res.string.quiz_correct)
            } else {
                stringResource(Res.string.quiz_wrong_retry)
            }
            Text(
                text = feedback,
                style = MaterialTheme.typography.bodyMedium,
                color = if (state.answeredCorrect) AppTheme.colors.success else AppTheme.colors.destructive,
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
            )
        }

        // 详解卡（答对后）：ipa / examples / wordFamily
        if (state.answeredCorrect) {
            AppCard(modifier = Modifier.fillMaxWidth().widthIn(max = AppContentWidth.reading)) {
                Column(
                    modifier = Modifier.padding(spacing.md),
                    verticalArrangement = Arrangement.spacedBy(spacing.xs),
                ) {
                    val sense = question.target
                    Text(
                        text = stringResource(Res.string.quiz_detail_pronunciation),
                        style = MaterialTheme.typography.labelMedium,
                        color = AppTheme.colors.onMuted,
                    )
                    Text(
                        text = sense.ipa ?: "—",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppTheme.colors.onSurface,
                    )
                    if (sense.examples.isNotEmpty()) {
                        Text(
                            text = stringResource(Res.string.quiz_detail_examples),
                            style = MaterialTheme.typography.labelMedium,
                            color = AppTheme.colors.onMuted,
                        )
                        sense.examples.forEach { ex ->
                            Text(
                                text = ex,
                                style = MaterialTheme.typography.bodyMedium,
                                color = AppTheme.colors.onSurface,
                            )
                        }
                    }
                    if (sense.wordFamily.isNotEmpty()) {
                        Text(
                            text = stringResource(Res.string.quiz_detail_word_family),
                            style = MaterialTheme.typography.labelMedium,
                            color = AppTheme.colors.onMuted,
                        )
                        Text(
                            text = sense.wordFamily.joinToString(", "),
                            style = MaterialTheme.typography.bodyMedium,
                            color = AppTheme.colors.onSurface,
                        )
                    }
                    AppButton(
                        text = stringResource(
                            if (state.index + 1 >= state.total) {
                                Res.string.quiz_view_results
                            } else {
                                Res.string.quiz_next
                            },
                        ),
                        onClick = onNext,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}
