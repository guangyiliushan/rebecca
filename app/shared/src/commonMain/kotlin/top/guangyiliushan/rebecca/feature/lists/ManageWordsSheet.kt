package top.guangyiliushan.rebecca.feature.lists

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import org.jetbrains.compose.resources.stringResource
import rebecca.app.shared.generated.resources.Res
import rebecca.app.shared.generated.resources.manage_already_in_list
import rebecca.app.shared.generated.resources.manage_add
import rebecca.app.shared.generated.resources.manage_search_placeholder
import rebecca.app.shared.generated.resources.manage_title
import rebecca.app.shared.generated.resources.list_search_clear
import top.guangyiliushan.rebecca.core.RebeccaData
import top.guangyiliushan.rebecca.core.model.WordListEntry
import top.guangyiliushan.rebecca.design.components.AppButton
import top.guangyiliushan.rebecca.design.components.AppListRow
import top.guangyiliushan.rebecca.design.components.AppListGroup
import top.guangyiliushan.rebecca.design.overlays.AppSheet
import top.guangyiliushan.rebecca.design.overlays.AppSheetHeader
import top.guangyiliushan.rebecca.design.theme.AppTheme
import top.guangyiliushan.rebecca.design.theme.ThemeSettings

/**
 * 管词 sheet（09 spec §Manage words 的 0.1.2 最小面，T3.4）：
 * 词典搜索（ContentDictionaryRepository.search）→ 结果行 + Add（重复添加幂等，"already in list"）。
 * 粘贴解析/跨单选择/移除词确认 后置（词库增强批）；本 sheet 由详情屏 More 挂入口。
 */
@Composable
internal fun ManageWordsSheet(
    open: Boolean,
    listId: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    onWordsChanged: () -> Unit = {},
) {
    var query by remember(open) { mutableStateOf("") }
    var addedCount by remember(open) { mutableIntStateOf(0) } // 触发 inList 重算（R-D2）
    val spacing = AppTheme.spacing
    val account = RebeccaData.demoAccount
    val inList = remember(open, addedCount) {
        RebeccaData.wordLists.entries(account, listId).map { it.senseId }.toSet()
    }
    // 搜索即时执行（本地内容平面，无网络）；空 query 不显示结果
    val results = if (query.isBlank()) {
        emptyList()
    } else {
        RebeccaData.contentDictionary.search(query).take(8)
    }

    AppSheet(open = open, onDismiss = onDismiss, modifier = modifier) {
        AppSheetHeader(title = { Text(stringResource(Res.string.manage_title)) })
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = spacing.md),
            verticalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            top.guangyiliushan.rebecca.design.patterns.WordListSearchField(
                value = query,
                onValueChange = { query = it },
                onClear = { query = "" },
                placeholder = stringResource(Res.string.manage_search_placeholder),
                clearContentDescription = stringResource(Res.string.list_search_clear),
            )
            AppListGroup {
                results.forEach { sense ->
                    val lemma = RebeccaData.contentDictionary.lemma(sense.lemmaId)
                    val already = sense.id in inList
                    AppListRow(
                        title = { Text(lemma?.form ?: sense.id.value) },
                        description = { Text(sense.definition) },
                        trailing = if (already) {
                            {
                                Text(
                                    text = stringResource(Res.string.manage_already_in_list),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = AppTheme.colors.onMuted,
                                )
                            }
                        } else {
                            {
                                AppButton(
                                    text = stringResource(Res.string.manage_add),
                                    onClick = {
                                        // 幂等：upsertEntry 同 (list, sense) 先删后插；ord 追加末尾
                                        val nextOrd = RebeccaData.wordLists.entries(account, listId).size
                                        RebeccaData.wordLists.upsertEntry(
                                            WordListEntry(account, listId, sense.id, nextOrd, RebeccaData.demoNow),
                                        )
                                        addedCount++ // 立即反映 "Already in list"（R-D2）
                                        onWordsChanged()
                                    },
                                    size = top.guangyiliushan.rebecca.design.components.AppButtonSize.Sm,
                                )
                            }
                        },
                    )
                }
            }
        }
    }
}

@PreviewLightDark
@PreviewFontScale
@Composable
private fun ManageWordsSheetPreview() {
    AppTheme(ThemeSettings()) {
        // 预览仅排版参考（open=false 不渲染内容）
    }
}
