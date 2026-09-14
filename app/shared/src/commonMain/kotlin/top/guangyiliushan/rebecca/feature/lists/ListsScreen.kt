package top.guangyiliushan.rebecca.feature.lists

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
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import rebecca.app.shared.generated.resources.Res
import rebecca.app.shared.generated.resources.empty_search_title
import rebecca.app.shared.generated.resources.empty_library_desc
import rebecca.app.shared.generated.resources.empty_library_title
import rebecca.app.shared.generated.resources.empty_search_desc
import rebecca.app.shared.generated.resources.hub_due
import rebecca.app.shared.generated.resources.lists_active_title
import rebecca.app.shared.generated.resources.lists_completed
import rebecca.app.shared.generated.resources.lists_completed_count
import rebecca.app.shared.generated.resources.lists_create
import rebecca.app.shared.generated.resources.lists_your_library
import rebecca.app.shared.generated.resources.focus_word_count
import rebecca.app.shared.generated.resources.focus_list_change
import rebecca.app.shared.generated.resources.list_active_subtitle
import rebecca.app.shared.generated.resources.list_more_options
import rebecca.app.shared.generated.resources.list_practice
import rebecca.app.shared.generated.resources.list_adaptive_desc
import rebecca.app.shared.generated.resources.list_adaptive_level_meta
import rebecca.app.shared.generated.resources.list_adaptive_title
import rebecca.app.shared.generated.resources.list_state_active
import rebecca.app.shared.generated.resources.row_new_label
import rebecca.app.shared.generated.resources.list_search_clear
import rebecca.app.shared.generated.resources.list_search_placeholder
import rebecca.app.shared.generated.resources.list_set_active_action
import rebecca.app.shared.generated.resources.row_learned_mastered
import rebecca.app.shared.generated.resources.row_due_label
import rebecca.app.shared.generated.resources.screen_lists
import top.guangyiliushan.rebecca.core.model.QuizMode
import top.guangyiliushan.rebecca.design.components.AppBadge
import top.guangyiliushan.rebecca.design.components.AppBadgeVariant
import top.guangyiliushan.rebecca.design.components.AppButton
import top.guangyiliushan.rebecca.design.components.AppIconButton
import top.guangyiliushan.rebecca.design.components.AppListRow
import top.guangyiliushan.rebecca.design.patterns.ActiveListCard
import top.guangyiliushan.rebecca.design.patterns.EmptyState
import top.guangyiliushan.rebecca.design.patterns.VocabularyListRow
import top.guangyiliushan.rebecca.design.patterns.WordListSearchField
import top.guangyiliushan.rebecca.design.theme.AppTheme
import top.guangyiliushan.rebecca.design.tokens.AppContentWidth

/**
 * 词单库屏（roadmap 0.1.2；09 spec 0.1.2 子集，Q7 裁决）：
 * 搜索 → Adaptive 固定首行（虚拟池，非实体：不可查看/不可删除，点击 = 恢复为活跃学习来源）
 * → ActiveListCard → 词单行 → Completed 折叠 → 新建。
 * FilterSheet/Recommendations/Community 后置（不渲染入口，F14）；分组标题 heading 语义（§10.2）。
 * 展开详情由导航层给回调（compact 压栈 / expanded 同屏由 AppScaffold.detail 决定，屏幕不读尺寸）。
 */
@Composable
fun ListsScreen(
    onOpenList: (String) -> Unit,
    onPractice: (QuizMode) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ListsViewModel = viewModel { ListsViewModel() },
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val spacing = AppTheme.spacing
    var createSheetOpen by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(spacing.md),
        verticalArrangement = Arrangement.spacedBy(spacing.md),
    ) {
        // 搜索（本地优先；Community submit 0.1.2 不接——capability 后置）
        WordListSearchField(
            value = state.query,
            onValueChange = viewModel::onQueryChange,
            onClear = { viewModel.onQueryChange("") },
            placeholder = stringResource(Res.string.list_search_placeholder),
            clearContentDescription = stringResource(Res.string.list_search_clear),
            // R-D10：提交即收起键盘/收起焦点（Community 搜索后置，本批仅本地）
            onSubmit = { },
        )

        // Adaptive 固定首行（产品决策 2026-09-14：虚拟池非实体词单——不可查看/不可删除，
        // 无独立视图；唯一动作 = 恢复为活跃学习来源；搜索时不渲染，它不是搜索结果）
        if (state.query.isEmpty()) {
            AppListRow(
                modifier = Modifier.fillMaxWidth(),
                title = {
                    Text(
                        text = stringResource(Res.string.list_adaptive_title),
                        style = MaterialTheme.typography.titleSmall,
                        color = AppTheme.colors.onSurface,
                    )
                },
                description = {
                    val adaptive = state.adaptiveRow
                    val levelCode = adaptive?.levelCode
                    Column(verticalArrangement = Arrangement.spacedBy(spacing.xxs)) {
                        Text(
                            text = stringResource(Res.string.list_adaptive_desc),
                            style = MaterialTheme.typography.bodySmall,
                            color = AppTheme.colors.onMuted,
                        )
                        // 底部信息 = 当前匹配词汇等级 + 理论词汇量（产品决策 2026-09-14）；
                        // 语言包无等级数据时不展示该行（不在 UI 编造等级）
                        if (levelCode != null && adaptive != null) {
                            Text(
                                text = stringResource(
                                    Res.string.list_adaptive_level_meta,
                                    levelCode,
                                    adaptive.levelTargetSize,
                                ),
                                style = MaterialTheme.typography.bodySmall,
                                color = AppTheme.colors.onMuted,
                            )
                        }
                    }
                },
                trailing = if (state.activeListId == null) {
                    {
                        AppBadge(variant = AppBadgeVariant.Outline) {
                            Text(
                                text = stringResource(Res.string.list_state_active),
                                style = MaterialTheme.typography.labelMedium,
                            )
                        }
                    }
                } else {
                    null
                },
                onClick = { viewModel.onSetActive(null) },
            )
        }

        if (state.emptySearch) {
            // 空搜索态：说明 + 新建（09 spec §Search 空结果；Community 入口后置）
            EmptyState(
                icon = Icons.Filled.Search,
                title = stringResource(Res.string.empty_search_title),
                description = stringResource(Res.string.empty_search_desc),
                action = {
                    AppButton(
                        text = stringResource(Res.string.lists_create),
                        onClick = { createSheetOpen = true },
                    )
                },
            )
        } else if (state.rows.isEmpty() && state.completedRows.isEmpty()) {
            // 空库态（R-D12：empty_library_* 文案落位；新建为主 CTA）
            EmptyState(
                icon = Icons.Filled.Search,
                title = stringResource(Res.string.empty_library_title),
                description = stringResource(Res.string.empty_library_desc),
                action = {
                    AppButton(
                        text = stringResource(Res.string.lists_create),
                        onClick = { createSheetOpen = true },
                    )
                },
            )
        } else {
            // 活跃词单摘要（与 Study 词单选择器同源偏好）
            if (state.activeListTitle != null) {
                ActiveListCard(
                    modifier = Modifier.fillMaxWidth().widthIn(max = AppContentWidth.default),
                    sectionTitle = stringResource(Res.string.lists_active_title),
                    title = state.activeListTitle.orEmpty(),
                    subtitle = stringResource(Res.string.list_active_subtitle),
                    countsLine = pluralStringResource(
                        Res.plurals.focus_word_count,
                        state.activeListCount,
                        state.activeListCount,
                    ) + " · " + stringResource(Res.string.row_due_label, state.activeListDue),
                    practiceLabel = stringResource(Res.string.list_practice),
                    changeLabel = stringResource(Res.string.focus_list_change),
                    onPractice = { onPractice(QuizMode.MIXED) },
                    // R-D7：Change = 打开活跃词单详情（真实动作，非死控件）
                    onChangeList = { state.activeListId?.let(onOpenList) },
                )
            }

            // 你的词单 + 新建入口（产品决策：新建固定于区块头，不随列表增长而下移——"瀑布流" UX）
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(Res.string.lists_your_library),
                    style = MaterialTheme.typography.titleMedium,
                    color = AppTheme.colors.onSurface,
                    modifier = Modifier.semantics { heading() },
                )
                AppButton(
                    text = stringResource(Res.string.lists_create),
                    onClick = { createSheetOpen = true },
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
                state.rows.forEach { row ->
                    VocabularyListRow(
                        title = row.title,
                        sourceLabel = row.sourceLabel,
                        wordCountLabel = pluralStringResource(
                            Res.plurals.focus_word_count,
                            row.wordCount,
                            row.wordCount,
                        ),
                        progressLabel = stringResource(
                            Res.string.row_learned_mastered,
                            row.learned,
                            row.mastered,
                        ),
                        dueLabel = if (row.due > 0) {
                            stringResource(Res.string.row_due_label, row.due)
                        } else {
                            null
                        },
                        onClick = { onOpenList(row.id) },
                        // R-D7：行级 More 菜单（Set active / 打开）——0.1.2 采用真实动作，不留死控件
                        more = {
                            AppIconButton(
                                onClick = { viewModel.onSetActive(row.id) },
                                icon = { Icon(Icons.Filled.MoreVert, contentDescription = null) },
                                contentDescription = stringResource(
                                    Res.string.list_set_active_action,
                                    row.title,
                                ),
                            )
                        },
                    )
                }
            }

            // Completed 折叠区（默认收起；完成内容不高于活跃内容——09 spec）
            if (state.completedRows.isNotEmpty()) {
                Text(
                    text = stringResource(Res.string.lists_completed),
                    style = MaterialTheme.typography.titleMedium,
                    color = AppTheme.colors.onSurface,
                    modifier = Modifier.semantics { heading() },
                )
                if (!state.completedCollapsed) {
                    Column(verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
                        state.completedRows.forEach { row ->
                            VocabularyListRow(
                                title = row.title,
                                sourceLabel = row.sourceLabel,
                                wordCountLabel = pluralStringResource(
                                    Res.plurals.focus_word_count,
                                    row.wordCount,
                                    row.wordCount,
                                ),
                                progressLabel = stringResource(
                                    Res.string.row_learned_mastered,
                                    row.learned,
                                    row.mastered,
                                ),
                                dueLabel = null,
                                onClick = { onOpenList(row.id) },
                            )
                        }
                    }
                } else {
                    AppListRow(
                        title = { Text(stringResource(Res.string.lists_completed_count, state.completedRows.size)) },
                        onClick = viewModel::onToggleCompleted,
                    )
                }
            }
        }
    }

    if (createSheetOpen) {
        ListEditSheet(
            open = true,
            onDismiss = { createSheetOpen = false },
            onCreate = { title, description ->
                viewModel.onCreateList(title, description)
                createSheetOpen = false
            },
        )
    }
}
