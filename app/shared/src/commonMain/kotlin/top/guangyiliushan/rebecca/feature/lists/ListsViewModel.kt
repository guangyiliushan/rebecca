package top.guangyiliushan.rebecca.feature.lists

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import top.guangyiliushan.rebecca.core.RebeccaData
import top.guangyiliushan.rebecca.core.logic.MasteryGrade
import top.guangyiliushan.rebecca.core.language.LanguageProfileRegistry
import top.guangyiliushan.rebecca.core.language.matchVocabularyLevel
import top.guangyiliushan.rebecca.core.logic.isDue
import top.guangyiliushan.rebecca.core.logic.masteryGrade
import top.guangyiliushan.rebecca.core.model.Preference
import top.guangyiliushan.rebecca.core.model.SenseId
import top.guangyiliushan.rebecca.core.model.WordList
import top.guangyiliushan.rebecca.core.model.WordListSource

/** 词单行展示值（计数全来自 core 纯函数 masteryGrade，Q5 政策不在 UI 算）。 */
data class ListRowUi(
    val id: String,
    val title: String,
    val description: String?,
    val sourceLabel: String,
    val wordCount: Int,
    val learned: Int,
    val mastered: Int,
    val due: Int,
    val completed: Boolean,
)

/**
 * Adaptive 行＝**收录全站单词的词表**，按当前词汇等级匹配该阶段学习词汇（产品决策 2026-09-14）。
 * 它不是实体词单：不可查看、不可删除；唯一动作 = 恢复 adaptive 为活跃学习来源。
 * 底部信息 = 当前匹配等级 + 理论词汇量（语言包数据；无等级数据时等级为 null → 不展示等级行，不在 UI 编造）。
 * TODO(0.2.x)：开放问题——展开可见"已答题学过的词 + 系统判断可能已掌握的词"（用户标注保留，暂不实现）。
 */
data class AdaptiveRowUi(
    val levelCode: String?,
    val levelTargetSize: Int,
    val isActive: Boolean,
)

/**
 * Lists 库屏状态（09 spec §UI state shape 的 0.1.2 子集，Adaptive 部分按产品决策偏离）。
 * FilterSheet 入口 / Recommendations / Community 后置（capability 隐藏，不渲染）。
 */
data class ListsUiState(
    val query: String = "",
    val activeListId: String? = null,
    val activeListTitle: String? = null,
    val activeListCount: Int = 0,
    val activeListDue: Int = 0,
    val rows: List<ListRowUi> = emptyList(),
    val completedRows: List<ListRowUi> = emptyList(),
    val completedCollapsed: Boolean = true, // 09 spec：Completed 默认折叠
    val emptySearch: Boolean = false,
    val adaptiveRow: AdaptiveRowUi? = null, // 搜索时为空（它不是搜索结果）
)

private const val ACTIVE_LIST_KEY = "study.activeList"

class ListsViewModel : ViewModel() {
    private var query = ""
    private var completedCollapsed = true
    private val _uiState = MutableStateFlow(load())
    val uiState: StateFlow<ListsUiState> = _uiState

    fun onQueryChange(newQuery: String) {
        query = newQuery.trim()
        _uiState.value = load()
    }

    fun onToggleCompleted() {
        completedCollapsed = !completedCollapsed
        _uiState.value = load()
    }

    fun onSetActive(listId: String?) {
        RebeccaData.preferences.upsert(
            Preference(
                accountId = RebeccaData.demoAccount,
                key = ACTIVE_LIST_KEY,
                valueJson = listId.orEmpty(),
                updatedAt = RebeccaData.demoNow,
            ),
        )
        _uiState.value = load()
    }

    fun onCreateList(title: String, description: String?) {
        val n = RebeccaData.wordLists.lists(RebeccaData.demoAccount).size
        val id = "list-user-${n + 1}-${title.hashCode().toUInt()}"
        RebeccaData.wordLists.upsertList(
            WordList(
                accountId = RebeccaData.demoAccount,
                id = id,
                title = title,
                sourceType = WordListSource.USER,
                updatedAt = RebeccaData.demoNow,
                description = description?.takeIf { it.isNotBlank() },
            ),
        )
        _uiState.value = load()
    }

    fun onDeleteList(listId: String) {
        RebeccaData.wordLists.deleteList(RebeccaData.demoAccount, listId, RebeccaData.demoNow)
        if (_uiState.value.activeListId == listId) onSetActive(null) else _uiState.value = load()
    }

    private fun load(): ListsUiState {
        val account = RebeccaData.demoAccount
        val now = RebeccaData.demoNow
        val masteryBySense = RebeccaData.mastery.allFor(account).associateBy { it.senseId }
        val trimmed = query.trim()
        val activeListId = RebeccaData.preferences.get(account, ACTIVE_LIST_KEY)
            ?.valueJson?.takeIf { it.isNotEmpty() }

        val rows = mutableListOf<ListRowUi>()
        val completed = mutableListOf<ListRowUi>()
        var activeListCount = 0
        var activeListDue = 0
        var activeListTitle: String? = null
        val poolSenseIds = mutableSetOf<SenseId>() // 自适应池 = 全库去重词条（虚拟池，无实体词单）

        RebeccaData.wordLists.lists(account).forEach { list ->
            val entries = RebeccaData.wordLists.entries(account, list.id)
            var learned = 0
            var mastered = 0
            var due = 0
            entries.forEach { entry ->
                if (entry.deletedAt == null) poolSenseIds += entry.senseId
                val m = masteryBySense[entry.senseId]
                when (masteryGrade(m)) {
                    MasteryGrade.New -> Unit
                    MasteryGrade.Learning, MasteryGrade.Mastered -> learned++
                    else -> Unit
                }
                if (masteryGrade(m) == MasteryGrade.Mastered) mastered++
                if (isDue(m, now)) due++
            }
            val row = ListRowUi(
                id = list.id,
                title = list.title,
                description = list.description,
                sourceLabel = sourceLabel(list.sourceType),
                wordCount = entries.size,
                learned = learned,
                mastered = mastered,
                due = due,
                completed = entries.isNotEmpty() && mastered == entries.size,
            )
            val matches = trimmed.isEmpty() ||
                list.title.contains(trimmed, ignoreCase = true) ||
                list.description?.contains(trimmed, ignoreCase = true) == true
            if (!matches) return@forEach
            if (row.completed) completed += row else rows += row
            if (list.id == activeListId) {
                activeListTitle = list.title
                activeListCount = row.wordCount
                activeListDue = row.due
            }
        }

        // Adaptive 行（产品决策 2026-09-14）：等级由已掌握词数推算（0.2.x 改用户声明/分级测试）
        val profile = LanguageProfileRegistry.default()
        val masteredInPool = poolSenseIds.count {
            masteryGrade(masteryBySense[it]) == MasteryGrade.Mastered
        }
        val level = matchVocabularyLevel(masteredInPool, profile.vocabularyLevels)
        val adaptiveRow = if (trimmed.isEmpty()) {
            AdaptiveRowUi(
                levelCode = level?.code,
                levelTargetSize = level?.targetVocabularySize ?: 0,
                isActive = activeListId == null,
            )
        } else {
            null
        }

        return ListsUiState(
            query = trimmed,
            activeListId = activeListId,
            activeListTitle = activeListTitle,
            activeListCount = activeListCount,
            activeListDue = activeListDue,
            rows = rows,
            completedRows = completed,
            completedCollapsed = completedCollapsed,
            emptySearch = query.isNotEmpty() && rows.isEmpty() && completed.isEmpty(),
            adaptiveRow = adaptiveRow,
        )
    }

    private fun sourceLabel(source: WordListSource): String = when (source) {
        WordListSource.USER -> "Personal"
        WordListSource.PACK -> "Curated"
        WordListSource.AI_EXTRACT -> "AI"
        WordListSource.IMPORT -> "Imported"
    }
}
