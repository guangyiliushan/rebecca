package top.guangyiliushan.rebecca.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import top.guangyiliushan.rebecca.core.model.QuizMode

@Serializable
sealed interface Route : NavKey {
    @Serializable @SerialName("study")      data object Study : Route
    @Serializable @SerialName("lists")      data object Lists : Route
    @Serializable @SerialName("dictionary") data object Dictionary : Route
    @Serializable @SerialName("explore")    data object Explore : Route
    @Serializable @SerialName("settings")   data object Settings : Route
    @Serializable @SerialName("showcase")   data object Showcase : Route   // debug-only，不进主导航
    @Serializable @SerialName("quiz")
    data class Quiz(val mode: QuizMode) : Route

    /** 词单详情（0.1.2）；fragment: lists/{listId}，无第二段 = Lists 库屏。 */
    @Serializable @SerialName("wordListDetail")
    data class WordListDetail(val listId: String) : Route

    /** 卡片浏览会话（0.1.2）；fragment: cards/{listId}。 */
    @Serializable @SerialName("cardSession")
    data class CardSession(val listId: String) : Route
}

/** URL 片段名（与 @SerialName 一致；browser 适配器 saveKey 用）。 */
val Route.fragmentName: String
    get() = when (this) {
        Route.Study -> "study"
        Route.Lists -> "lists"
        Route.Dictionary -> "dictionary"
        Route.Explore -> "explore"
        Route.Settings -> "settings"
        Route.Showcase -> "showcase"
        is Route.Quiz -> "quiz/${mode.name.lowercase()}"
        is Route.WordListDetail -> "lists/${listId}"
        is Route.CardSession -> "cards/${listId}"
    }

/** URL 片段名反解析（browser 适配器 restoreKey 用）；未知片段回退 Study。 */
fun routeFromFragmentName(name: String): Route {
    val seg = name.split("/")
    return when (seg[0]) {
        "study" -> Route.Study
        "lists" -> if (seg.size > 1 && seg[1].isNotBlank()) {
            Route.WordListDetail(seg[1])
        } else {
            Route.Lists
        }
        "dictionary" -> Route.Dictionary
        "explore" -> Route.Explore
        "settings" -> Route.Settings
        "showcase" -> Route.Showcase
        "quiz" -> Route.Quiz(
            QuizMode.entries.firstOrNull { it.name.equals(seg.getOrNull(1), ignoreCase = true) }
                ?: QuizMode.LEARN,
        )
        "cards" -> Route.CardSession(seg.getOrElse(1) { "" })
        else -> Route.Study
    }
}
