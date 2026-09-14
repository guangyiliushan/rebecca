package top.guangyiliushan.rebecca.feature.lists

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.stringResource
import rebecca.app.shared.generated.resources.Res
import rebecca.app.shared.generated.resources.action_cancel
import rebecca.app.shared.generated.resources.list_create_title
import rebecca.app.shared.generated.resources.list_desc_label
import rebecca.app.shared.generated.resources.list_desc_optional
import rebecca.app.shared.generated.resources.list_title_label
import rebecca.app.shared.generated.resources.list_title_required
import rebecca.app.shared.generated.resources.goal_save
import top.guangyiliushan.rebecca.design.components.AppButton
import top.guangyiliushan.rebecca.design.components.AppButtonVariant
import top.guangyiliushan.rebecca.design.components.AppField
import top.guangyiliushan.rebecca.design.components.AppInput
import top.guangyiliushan.rebecca.design.overlays.AppSheet
import top.guangyiliushan.rebecca.design.overlays.AppSheetFooter
import top.guangyiliushan.rebecca.design.overlays.AppSheetHeader
import top.guangyiliushan.rebecca.design.theme.AppTheme

private const val TITLE_MAX = 60

/**
 * 建/改词单 sheet（09 spec §Create/edit list sheet 的 0.1.2 子集）：
 * title 必填非空白 + 可见长度计数；description 可选；source/language 只读展示。
 * Save 仅在 title 非法时禁用（理由可见：错误文案走 AppField errors，F15 Assertive 由 AppField 内建）。
 */
@Composable
internal fun ListEditSheet(
    open: Boolean,
    onDismiss: () -> Unit,
    onCreate: (title: String, description: String?) -> Unit,
) {
    var title by remember(open) { mutableStateOf("") }
    var description by remember(open) { mutableStateOf("") }
    var attempted by remember(open) { mutableIntStateOf(0) }
    val spacing = AppTheme.spacing
    val titleInvalid = attempted > 0 && title.isBlank()

    AppSheet(open = open, onDismiss = onDismiss) {
        AppSheetHeader(title = { Text(stringResource(Res.string.list_create_title)) })
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = spacing.md),
            verticalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            AppField(
                invalid = titleInvalid,
                errors = if (titleInvalid) {
                    listOf(stringResource(Res.string.list_title_required))
                } else {
                    emptyList()
                },
                label = { Text(stringResource(Res.string.list_title_label)) },
            ) {
                AppInput(
                    value = title,
                    onValueChange = { if (it.length <= TITLE_MAX) title = it },
                    placeholder = null,
                )
            }
            Text(
                text = "${title.length} / $TITLE_MAX",
                style = MaterialTheme.typography.labelMedium,
                color = AppTheme.colors.onMuted,
                modifier = Modifier.fillMaxWidth(),
            )
            AppField(
                label = { Text(stringResource(Res.string.list_desc_label)) },
                description = { Text(stringResource(Res.string.list_desc_optional)) },
            ) {
                AppInput(
                    value = description,
                    onValueChange = { description = it },
                    singleLine = false,
                    maxLines = 3,
                )
            }
        }
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
                    onClick = {
                        attempted++
                        if (title.isNotBlank()) {
                            onCreate(title.trim(), description.trim().takeIf { it.isNotEmpty() })
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !titleInvalid,
                )
            }
        }
    }
}
