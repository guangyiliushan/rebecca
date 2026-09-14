package top.guangyiliushan.rebecca.feature.study

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jetbrains.compose.resources.stringResource
import rebecca.app.shared.generated.resources.Res
import rebecca.app.shared.generated.resources.action_back
import rebecca.app.shared.generated.resources.card_decide_known
import rebecca.app.shared.generated.resources.card_decide_unknown
import rebecca.app.shared.generated.resources.card_face_back
import rebecca.app.shared.generated.resources.card_face_front
import rebecca.app.shared.generated.resources.card_flip
import rebecca.app.shared.generated.resources.card_progress
import rebecca.app.shared.generated.resources.card_session_done
import rebecca.app.shared.generated.resources.card_session_summary
import rebecca.app.shared.generated.resources.card_session_title
import rebecca.app.shared.generated.resources.quiz_result_accuracy
import rebecca.app.shared.generated.resources.quiz_result_back
import rebecca.app.shared.generated.resources.quiz_result_retry
import rebecca.app.shared.generated.resources.quiz_result_time
import rebecca.app.shared.generated.resources.quiz_time_seconds
import top.guangyiliushan.rebecca.design.components.AppButton
import top.guangyiliushan.rebecca.design.components.AppButtonVariant
import top.guangyiliushan.rebecca.design.components.AppCard
import top.guangyiliushan.rebecca.design.components.AppIconButton
import top.guangyiliushan.rebecca.design.components.AppProgress
import top.guangyiliushan.rebecca.design.patterns.CardSwipeStack
import top.guangyiliushan.rebecca.design.patterns.SessionSummaryCard
import top.guangyiliushan.rebecca.design.patterns.SummaryMetric
import top.guangyiliushan.rebecca.design.scaffold.AppTopBar
import top.guangyiliushan.rebecca.design.theme.AppTheme

/**
 * 卡片浏览会话（roadmap 0.1.2；Q11 裁决）：
 * 手势（CardSwipeStack）+ **常驻等效按钮**（§10.5：判定永不只靠手势）+ 键盘左右键（桌面/Web）。
 * 判定真实写掌握度与事件；结算复用 quiz 结算形态（本批直接呈现 summary 卡，不建第二套结算屏）。
 */
@Composable
fun CardSessionScreen(
    listId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CardSessionViewModel =
        viewModel(key = "cards-$listId") { CardSessionViewModel(listId) },
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val spacing = AppTheme.spacing
    val faceFrontDescription = stringResource(Res.string.card_face_front)
    val faceBackDescription = stringResource(Res.string.card_face_back)
    // R-D5：键盘左右键 = 不认识/认识（桌面/Web；与常驻按钮同语义，§10.5 手势替代的键盘面）
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .focusRequester(focusRequester)
            .focusable()
            .onPreviewKeyEvent { event ->
                if (event.type != KeyEventType.KeyDown || state.finished) return@onPreviewKeyEvent false
                when (event.key) {
                    Key.DirectionRight -> {
                        viewModel.onEvent(CardUiEvent.Decide(known = true))
                        true
                    }
                    Key.DirectionLeft -> {
                        viewModel.onEvent(CardUiEvent.Decide(known = false))
                        true
                    }
                    Key.DirectionUp, Key.DirectionDown, Key.Spacebar -> {
                        viewModel.onEvent(CardUiEvent.Flip)
                        true
                    }
                    else -> false
                }
            },
    ) {
        AppTopBar(
            title = { Text(state.listTitle.ifBlank { stringResource(Res.string.card_session_title) }) },
            navigationIcon = {
                AppIconButton(
                    onClick = onBack,
                    icon = { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null) },
                    contentDescription = stringResource(Res.string.action_back),
                )
            },
        )
        if (state.finished) {
            // 结算（复用口径：known/unknown/accuracy 来自 core cardSessionSummary；R-D6 共用 SessionSummaryCard）
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(spacing.md),
                verticalArrangement = Arrangement.spacedBy(spacing.md),
            ) {
                val summary = state.summary
                SessionSummaryCard(
                    title = stringResource(Res.string.card_session_done),
                    metrics = listOf(
                        SummaryMetric(
                            value = "${summary?.accuracyPercent ?: 0}%",
                            label = stringResource(Res.string.quiz_result_accuracy),
                        ),
                        SummaryMetric(
                            value = stringResource(Res.string.quiz_time_seconds, state.elapsedSeconds.toInt()),
                            label = stringResource(Res.string.quiz_result_time),
                        ),
                    ),
                    primaryLabel = stringResource(Res.string.quiz_result_retry),
                    onPrimary = onBack,
                    secondaryLabel = stringResource(Res.string.quiz_result_back),
                    onSecondary = onBack,
                )
                Text(
                    text = stringResource(
                        Res.string.card_session_summary,
                        summary?.known ?: 0,
                        summary?.unknown ?: 0,
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppTheme.colors.onMuted,
                )
            }
            return
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(spacing.md),
            verticalArrangement = Arrangement.spacedBy(spacing.md),
        ) {
            // 进度（i/n + 线性）
            Text(
                text = stringResource(Res.string.card_progress, state.index + 1, state.total),
                style = MaterialTheme.typography.labelMedium,
                color = AppTheme.colors.onMuted,
            )
            AppProgress(
                progress = (state.index + 1).toFloat() / state.total,
                modifier = Modifier.fillMaxWidth(),
            )

            // 卡片（手势层；点击翻面）
            CardSwipeStack(
                onDecision = { known -> viewModel.onEvent(CardUiEvent.Decide(known)) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                AppCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { viewModel.onEvent(CardUiEvent.Flip) },
                ) {
                    Column(
                        modifier = Modifier.padding(spacing.lg).fillMaxWidth(),
                        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(spacing.sm),
                    ) {
                        Text(
                            text = state.headword,
                            style = MaterialTheme.typography.headlineSmall,
                            color = AppTheme.colors.onSurface,
                            // R-D14：翻面状态经 stateDescription 可读（读屏可辨正面/背面）
                            modifier = Modifier.semantics {
                                stateDescription = if (state.face == CardFace.Front) {
                                    faceFrontDescription
                                } else {
                                    faceBackDescription
                                }
                            },
                        )
                        if (state.face == CardFace.Front) {
                            Text(
                                text = state.ipa ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                color = AppTheme.colors.onMuted,
                            )
                            Text(
                                text = stringResource(Res.string.card_flip),
                                style = MaterialTheme.typography.labelMedium,
                                color = AppTheme.colors.primary,
                            )
                        } else {
                            Text(
                                text = state.definition,
                                style = MaterialTheme.typography.bodyLarge,
                                color = AppTheme.colors.onSurface,
                            )
                            state.pos?.let {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = AppTheme.colors.onMuted,
                                )
                            }
                        }
                    }
                }
            }

            // 常驻等效按钮（§10.5：手势必有效果替代）
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
            ) {
                AppButton(
                    text = stringResource(Res.string.card_decide_unknown),
                    onClick = { viewModel.onEvent(CardUiEvent.Decide(known = false)) },
                    modifier = Modifier.weight(1f),
                    variant = AppButtonVariant.Outline,
                )
                AppButton(
                    text = stringResource(Res.string.card_decide_known),
                    onClick = { viewModel.onEvent(CardUiEvent.Decide(known = true)) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}
