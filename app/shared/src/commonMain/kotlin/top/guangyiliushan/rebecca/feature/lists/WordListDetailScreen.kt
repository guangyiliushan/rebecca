package top.guangyiliushan.rebecca.feature.lists

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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jetbrains.compose.resources.stringResource
import rebecca.app.shared.generated.resources.Res
import rebecca.app.shared.generated.resources.action_back
import rebecca.app.shared.generated.resources.action_cancel
import rebecca.app.shared.generated.resources.detail_mastered
import rebecca.app.shared.generated.resources.detail_manage_words
import rebecca.app.shared.generated.resources.detail_practice
import rebecca.app.shared.generated.resources.detail_practice_disabled
import rebecca.app.shared.generated.resources.detail_progress_title
import rebecca.app.shared.generated.resources.detail_remove_desc
import rebecca.app.shared.generated.resources.detail_remove_title
import rebecca.app.shared.generated.resources.detail_set_active
import rebecca.app.shared.generated.resources.detail_state_active
import rebecca.app.shared.generated.resources.detail_word_due
import rebecca.app.shared.generated.resources.detail_word_remove
import rebecca.app.shared.generated.resources.filter_all
import rebecca.app.shared.generated.resources.filter_due
import rebecca.app.shared.generated.resources.filter_learning
import rebecca.app.shared.generated.resources.filter_mastered
import rebecca.app.shared.generated.resources.filter_new
import rebecca.app.shared.generated.resources.row_learned_mastered
import rebecca.app.shared.generated.resources.screen_lists
import top.guangyiliushan.rebecca.core.logic.MasteryGrade
import top.guangyiliushan.rebecca.core.model.QuizMode
import top.guangyiliushan.rebecca.design.components.AppButton
import top.guangyiliushan.rebecca.design.components.AppButtonVariant
import top.guangyiliushan.rebecca.design.components.AppChip
import top.guangyiliushan.rebecca.design.components.AppChipGroup
import top.guangyiliushan.rebecca.design.components.AppChipGroupMode
import top.guangyiliushan.rebecca.design.components.AppChipGroupSpacing
import top.guangyiliushan.rebecca.design.components.AppIconButton
import top.guangyiliushan.rebecca.design.components.AppListRow
import top.guangyiliushan.rebecca.design.components.AppProgress
import top.guangyiliushan.rebecca.design.overlays.AppAlertDialog
import top.guangyiliushan.rebecca.design.scaffold.AppTopBar
import top.guangyiliushan.rebecca.design.theme.AppTheme

/**
 * 词单详情屏（roadmap 0.1.2；09 spec §List detail 0.1.2 子集，Q8 裁决）：
 * header → [Practice mixed][Set active|Active] → 进度块（learned/mastered + 线性进度）
 * → 词筛选 chips → 词行（本批不可点——词典详情 0.1.3）。
 * Quick preview 后置（Q2）；More=从词单移除（含确认）。
 * AppIdentityBar 不装（09 spec 路由表）；AppTopBar 自带返回。
 * 文案全部经 catalog（`filter_*`/`detail_word_*`），feature 层零硬编码（F6/R9）。
 */
@Composable
fun WordListDetailScreen(
    listId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    onPractice: (QuizMode) -> Unit = {},
    viewModel: WordListDetailViewModel =
        viewModel(key = "detail-$listId") { WordListDetailViewModel(listId) },
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val spacing = AppTheme.spacing
    var manageSheetOpen by rememberSaveable { mutableStateOf(false) }
    var pendingRemoval by remember { mutableStateOf<WordRowUi?>(null) }

    Column(modifier = modifier.fillMaxSize()) {
        AppTopBar(
            title = { Text(state.title.ifBlank { stringResource(Res.string.screen_lists) }) },
            navigationIcon = {
                AppIconButton(
                    onClick = onBack,
                    icon = { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null) },
                    contentDescription = stringResource(Res.string.action_back),
                )
            },
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(spacing.md),
            verticalArrangement = Arrangement.spacedBy(spacing.md),
        ) {
            // header 元数据
            Text(
                text = "${state.sourceLabel} · ${state.wordCount}",
                style = MaterialTheme.typography.bodyMedium,
                color = AppTheme.colors.onMuted,
            )
            state.description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppTheme.colors.onSurface,
                )
            }

            // 行动区：Practice 主行动（空单禁用 + 理由，R-D3）；Set active / Active；Manage words
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
            ) {
                AppButton(
                    text = stringResource(Res.string.detail_practice),
                    onClick = { onPractice(QuizMode.MIXED) },
                    modifier = Modifier.weight(1f),
                    enabled = state.wordCount > 0,
                )
                AppButton(
                    text = if (state.isActive) {
                        stringResource(Res.string.detail_state_active)
                    } else {
                        stringResource(Res.string.detail_set_active)
                    },
                    onClick = viewModel::onSetActive,
                    enabled = !state.isActive,
                    variant = AppButtonVariant.Ghost,
                )
            }
            if (state.wordCount == 0) {
                Text(
                    text = stringResource(Res.string.detail_practice_disabled),
                    style = MaterialTheme.typography.bodySmall,
                    color = AppTheme.colors.onMuted,
                )
            }
            AppButton(
                text = stringResource(Res.string.detail_manage_words),
                onClick = { manageSheetOpen = true },
                modifier = Modifier.fillMaxWidth(),
                variant = AppButtonVariant.Outline,
            )

            // 进度块（learned/mastered + 线性进度；文本替代齐全）
            Text(
                text = stringResource(Res.string.detail_progress_title),
                style = MaterialTheme.typography.titleMedium,
                color = AppTheme.colors.onSurface,
                modifier = Modifier.semantics { heading() },
            )
            Text(
                text = stringResource(Res.string.row_learned_mastered, state.learned, state.mastered),
                style = MaterialTheme.typography.bodyMedium,
                color = AppTheme.colors.onMuted,
            )
            AppProgress(
                progress = state.masteredFraction,
                modifier = Modifier.fillMaxWidth(),
                label = stringResource(Res.string.detail_mastered, state.mastered),
            )

            // 词筛选 chips（All/New/Learning/Mastered/Due）
            AppChipGroup(
                mode = AppChipGroupMode.Single,
                spacing = AppChipGroupSpacing.Separated,
            ) {
                WordFilter.entries.forEach { filter ->
                    AppChip(
                        label = { Text(filterLabel(filter)) },
                        selected = state.filter == filter,
                        onClick = { viewModel.onFilterSelect(filter) },
                    )
                }
            }

            // 词行（不可点——词典详情 0.1.3；移除 = 真实动作 + 确认，R-D8）
            state.words.forEach { word ->
                AppListRow(
                    title = { Text(word.headword) },
                    description = {
                        Column(verticalArrangement = Arrangement.spacedBy(spacing.xxs)) {
                            Text(
                                text = listOfNotNull(word.ipa, word.pos).joinToString(" · "),
                                style = MaterialTheme.typography.bodySmall,
                                color = AppTheme.colors.onMuted,
                            )
                            Text(
                                text = if (word.due) {
                                    stringResource(Res.string.detail_word_due, gradeLabel(word.grade))
                                } else {
                                    gradeLabel(word.grade)
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = AppTheme.colors.onMuted,
                            )
                        }
                    },
                    trailing = {
                        AppIconButton(
                            onClick = { pendingRemoval = word },
                            icon = { Icon(Icons.Filled.Close, contentDescription = null) },
                            contentDescription = stringResource(Res.string.detail_word_remove),
                        )
                    },
                )
            }
        }
    }

    // 移除词确认（R-D8：状态后果说明 + 掌握度保留）
    pendingRemoval?.let { word ->
        AppAlertDialog(
            open = true,
            onDismiss = { pendingRemoval = null },
            title = { Text(stringResource(Res.string.detail_remove_title)) },
            text = {
                val removalMessage = word.headword + "\n" + stringResource(Res.string.detail_remove_desc)
                Text(removalMessage)
            },
            confirmLabel = stringResource(Res.string.detail_word_remove),
            onConfirm = {
                viewModel.removeFromList(word.senseId)
                pendingRemoval = null
            },
            dismissLabel = stringResource(Res.string.action_cancel),
        )
    }

    if (manageSheetOpen) {
        ManageWordsSheet(
            open = true,
            listId = listId,
            onDismiss = { manageSheetOpen = false },
            onWordsChanged = viewModel::refresh, // R-D2：加词后详情计数/词行刷新
        )
    }
}

@Composable
private fun filterLabel(filter: WordFilter): String = stringResource(
    when (filter) {
        WordFilter.All -> Res.string.filter_all
        WordFilter.New -> Res.string.filter_new
        WordFilter.Learning -> Res.string.filter_learning
        WordFilter.Mastered -> Res.string.filter_mastered
        WordFilter.Due -> Res.string.filter_due
    },
)

@Composable
private fun gradeLabel(grade: MasteryGrade): String = stringResource(
    when (grade) {
        MasteryGrade.New -> Res.string.filter_new
        MasteryGrade.Learning -> Res.string.filter_learning
        MasteryGrade.Mastered -> Res.string.filter_mastered
    },
)
