package top.guangyiliushan.rebecca.feature.lists

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import top.guangyiliushan.rebecca.core.RebeccaData
import top.guangyiliushan.rebecca.core.logic.MasteryGrade
import top.guangyiliushan.rebecca.core.logic.isDue
import top.guangyiliushan.rebecca.core.logic.masteryGrade
import top.guangyiliushan.rebecca.core.model.Preference
import top.guangyiliushan.rebecca.core.model.SenseId

/** 词筛选（09 spec §Word filters）。 */
enum class WordFilter { All, New, Learning, Mastered, Due }

/** 词行展示值（headword 来自 Lemma；档位/到期来自 core 纯函数）。 */
data class WordRowUi(
    val senseId: SenseId,
    val headword: String,
    val ipa: String?,
    val pos: String?,
    val grade: MasteryGrade,
    val due: Boolean,
)

data class WordListDetailUiState(
    val listId: String = "",
    val title: String = "",
    val description: String? = null,
    val sourceLabel: String = "",
    val wordCount: Int = 0,
    val learned: Int = 0,
    val mastered: Int = 0,
    val masteredFraction: Float = 0f,
    val isActive: Boolean = false,
    val filter: WordFilter = WordFilter.All,
    val words: List<WordRowUi> = emptyList(),
    val allWords: List<WordRowUi> = emptyList(),
)

class WordListDetailViewModel(listId: String) : ViewModel() {
    private val _uiState = MutableStateFlow(load(listId))
    val uiState: StateFlow<WordListDetailUiState> = _uiState

    /** 词单内容被外部改动（如管词 sheet 加词）后重载（R-D2：详情计数/词行刷新通道）。 */
    fun refresh() {
        _uiState.value = load(_uiState.value.listId)
    }

    fun onFilterSelect(filter: WordFilter) {
        val current = _uiState.value
        _uiState.value = current.copy(
            filter = filter,
            words = when (filter) {
                WordFilter.All -> current.allWords
                WordFilter.New -> current.allWords.filter { it.grade == MasteryGrade.New }
                WordFilter.Learning -> current.allWords.filter { it.grade == MasteryGrade.Learning }
                WordFilter.Mastered -> current.allWords.filter { it.grade == MasteryGrade.Mastered }
                WordFilter.Due -> current.allWords.filter { it.due }
            },
        )
    }

    fun onSetActive() {
        val current = _uiState.value
        RebeccaData.preferences.upsert(
            Preference(
                accountId = RebeccaData.demoAccount,
                key = "study.activeList",
                valueJson = current.listId,
                updatedAt = RebeccaData.demoNow,
            ),
        )
        _uiState.value = load(current.listId)
    }

    fun removeFromList(senseId: SenseId) {
        val current = _uiState.value
        RebeccaData.wordLists.removeEntry(RebeccaData.demoAccount, current.listId, senseId, RebeccaData.demoNow)
        _uiState.value = load(current.listId)
    }

    companion object {
        fun load(listId: String): WordListDetailUiState {
            val account = RebeccaData.demoAccount
            val now = RebeccaData.demoNow
            val list = RebeccaData.wordLists.lists(account).firstOrNull { it.id == listId }
                ?: return WordListDetailUiState(listId = listId)
            val masteryBySense = RebeccaData.mastery.allFor(account).associateBy { it.senseId }
            val entries = RebeccaData.wordLists.entries(account, listId)
            val activeListId = RebeccaData.preferences.get(account, "study.activeList")
                ?.valueJson?.takeIf { it.isNotEmpty() }

            var learned = 0
            var mastered = 0
            val words = entries.map { entry ->
                val m = masteryBySense[entry.senseId]
                val grade = masteryGrade(m)
                if (grade != MasteryGrade.New) learned++
                if (grade == MasteryGrade.Mastered) mastered++
                val sense = RebeccaData.contentDictionary.sense(entry.senseId)
                val lemma = sense?.let { RebeccaData.contentDictionary.lemma(it.lemmaId) }
                WordRowUi(
                    senseId = entry.senseId,
                    headword = lemma?.form ?: "",
                    ipa = sense?.ipa,
                    pos = sense?.partOfSpeech,
                    grade = grade,
                    due = isDue(m, now),
                )
            }
            return WordListDetailUiState(
                listId = listId,
                title = list.title,
                description = list.description,
                sourceLabel = when (list.sourceType) {
                    top.guangyiliushan.rebecca.core.model.WordListSource.USER -> "Personal"
                    top.guangyiliushan.rebecca.core.model.WordListSource.PACK -> "Curated"
                    top.guangyiliushan.rebecca.core.model.WordListSource.AI_EXTRACT -> "AI"
                    top.guangyiliushan.rebecca.core.model.WordListSource.IMPORT -> "Imported"
                },
                wordCount = entries.size,
                learned = learned,
                mastered = mastered,
                masteredFraction = if (entries.isEmpty()) 0f else mastered.toFloat() / entries.size,
                isActive = activeListId == listId,
                words = words,
                allWords = words,
            )
        }
    }
}
