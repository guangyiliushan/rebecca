package top.guangyiliushan.rebecca.feature.study

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.stringResource
import rebecca.app.shared.generated.resources.Res
import rebecca.app.shared.generated.resources.quiz_result_accuracy
import rebecca.app.shared.generated.resources.quiz_result_back
import rebecca.app.shared.generated.resources.quiz_result_retry
import rebecca.app.shared.generated.resources.quiz_result_time
import rebecca.app.shared.generated.resources.quiz_result_title
import top.guangyiliushan.rebecca.design.components.AppButton
import top.guangyiliushan.rebecca.design.components.AppButtonVariant
import top.guangyiliushan.rebecca.design.components.AppCard
import top.guangyiliushan.rebecca.design.patterns.StatCard
import top.guangyiliushan.rebecca.design.theme.AppTheme
import top.guangyiliushan.rebecca.design.tokens.AppContentWidth

/** 会话结算（roadmap 0.1.1）：正确率 / 用时 + 重练 / 返回枢纽。 */
@Composable
fun QuizResultScreen(
    state: QuizUiState,
    onRetry: () -> Unit,
    onBackToHub: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = AppTheme.spacing
    val accuracy = if (state.total > 0) state.correctCount * 100 / state.total else 0

    Column(
        modifier = modifier.fillMaxSize().padding(spacing.md),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacing.md),
    ) {
        Text(
            text = stringResource(Res.string.quiz_result_title),
            style = MaterialTheme.typography.titleLarge,
            color = AppTheme.colors.onSurface,
        )
        AppCard(modifier = Modifier.fillMaxWidth().widthIn(max = AppContentWidth.reading)) {
            Column(
                modifier = Modifier.padding(spacing.sm),
                verticalArrangement = Arrangement.spacedBy(spacing.sm),
            ) {
                StatCard(
                    value = "$accuracy%",
                    label = stringResource(Res.string.quiz_result_accuracy),
                    modifier = Modifier.fillMaxWidth(),
                )
                StatCard(
                    value = "${state.elapsedSeconds}s",
                    label = stringResource(Res.string.quiz_result_time),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
        Column(
            modifier = Modifier.widthIn(max = AppContentWidth.reading),
            verticalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            AppButton(
                text = stringResource(Res.string.quiz_result_retry),
                onClick = onRetry,
                modifier = Modifier.fillMaxWidth(),
            )
            AppButton(
                text = stringResource(Res.string.quiz_result_back),
                onClick = onBackToHub,
                modifier = Modifier.fillMaxWidth(),
                variant = AppButtonVariant.Ghost,
            )
        }
    }
}
