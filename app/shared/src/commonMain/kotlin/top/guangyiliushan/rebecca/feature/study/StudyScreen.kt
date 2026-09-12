package top.guangyiliushan.rebecca.feature.study

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jetbrains.compose.resources.stringResource
import rebecca.app.shared.generated.resources.Res
import rebecca.app.shared.generated.resources.hub_chart_placeholder_desc
import rebecca.app.shared.generated.resources.hub_chart_placeholder_title
import rebecca.app.shared.generated.resources.hub_due
import rebecca.app.shared.generated.resources.hub_new
import rebecca.app.shared.generated.resources.hub_pick_mode
import rebecca.app.shared.generated.resources.hub_streak
import rebecca.app.shared.generated.resources.mode_learn
import rebecca.app.shared.generated.resources.mode_mixed
import rebecca.app.shared.generated.resources.mode_review
import top.guangyiliushan.rebecca.core.model.QuizMode
import top.guangyiliushan.rebecca.design.components.AppButton
import top.guangyiliushan.rebecca.design.components.AppButtonVariant
import top.guangyiliushan.rebecca.design.components.AppCard
import top.guangyiliushan.rebecca.design.patterns.EmptyState
import top.guangyiliushan.rebecca.design.patterns.StatCard
import top.guangyiliushan.rebecca.design.theme.AppTheme
import top.guangyiliushan.rebecca.design.tokens.AppContentWidth

/**
 * 学习中枢（roadmap 0.1.1）：今日进度（due/新词/连续天数，demo 真实计算）
 * + 三模式入口（全部真实可用）+ 图表区 EmptyState 占位（0.2.3 接）。
 * 断点由 AppScaffold 管（F-L4）；内容宽度走 AppContentWidth（expanded 不拉面条）。
 */
@Composable
fun StudyScreen(
    onModeChosen: (QuizMode) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: StudyHubViewModel = viewModel { StudyHubViewModel() },
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val spacing = AppTheme.spacing

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(spacing.md),
        verticalArrangement = Arrangement.spacedBy(spacing.md),
    ) {
        // 今日进度三卡
        FlowRow(
            modifier = Modifier.fillMaxWidth().widthIn(max = AppContentWidth.reading),
            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
            verticalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            StatCard(value = state.dueCount.toString(), label = stringResource(Res.string.hub_due))
            StatCard(value = state.newCount.toString(), label = stringResource(Res.string.hub_new))
            StatCard(value = state.streak.toString(), label = stringResource(Res.string.hub_streak))
        }

        // 三模式入口（Q10 图标约束：core 集无 Shuffle，混合用 List）
        Column(verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
            Text(
                text = stringResource(Res.string.hub_pick_mode),
                style = MaterialTheme.typography.titleMedium,
                color = AppTheme.colors.onSurface,
            )
            Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                AppButton(
                    text = stringResource(Res.string.mode_learn),
                    onClick = { onModeChosen(QuizMode.LEARN) },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Filled.PlayArrow, contentDescription = null) },
                )
                AppButton(
                    text = stringResource(Res.string.mode_review),
                    onClick = { onModeChosen(QuizMode.REVIEW) },
                    modifier = Modifier.fillMaxWidth(),
                    variant = AppButtonVariant.Secondary,
                    leadingIcon = { Icon(Icons.Filled.Refresh, contentDescription = null) },
                )
                AppButton(
                    text = stringResource(Res.string.mode_mixed),
                    onClick = { onModeChosen(QuizMode.MIXED) },
                    modifier = Modifier.fillMaxWidth(),
                    variant = AppButtonVariant.Outline,
                    leadingIcon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) },
                )
            }
        }

        // 图表区占位（0.2.3 事件流聚合版替换）
        AppCard(modifier = Modifier.fillMaxWidth().widthIn(max = AppContentWidth.reading)) {
            EmptyState(
                icon = Icons.Filled.Star,
                title = stringResource(Res.string.hub_chart_placeholder_title),
                description = stringResource(Res.string.hub_chart_placeholder_desc),
            )
        }
    }
}
