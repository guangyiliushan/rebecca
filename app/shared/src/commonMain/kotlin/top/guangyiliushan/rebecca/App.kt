package top.guangyiliushan.rebecca

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import org.jetbrains.compose.resources.stringResource
import rebecca.app.shared.generated.resources.Res
import rebecca.app.shared.generated.resources.identity_language_pair_semantics
import rebecca.app.shared.generated.resources.identity_language_pair_short
import rebecca.app.shared.generated.resources.identity_local_learner
import top.guangyiliushan.rebecca.design.patterns.AppIdentityBar
import top.guangyiliushan.rebecca.design.scaffold.AppScaffold
import top.guangyiliushan.rebecca.design.scaffold.LocalDetailPaneVisible
import top.guangyiliushan.rebecca.design.theme.AppTheme
import top.guangyiliushan.rebecca.design.theme.ThemeSettings
import top.guangyiliushan.rebecca.feature.dictionary.DictionaryScreen
import top.guangyiliushan.rebecca.feature.explore.ExploreScreen
import top.guangyiliushan.rebecca.feature.lists.ListsScreen
import top.guangyiliushan.rebecca.feature.lists.WordListDetailScreen
import top.guangyiliushan.rebecca.feature.settings.SettingsScreen
import top.guangyiliushan.rebecca.feature.showcase.ShowcaseScreen
import top.guangyiliushan.rebecca.feature.study.CardSessionScreen
import top.guangyiliushan.rebecca.feature.study.QuizScreen
import top.guangyiliushan.rebecca.feature.study.StudyScreen
import top.guangyiliushan.rebecca.navigation.Route
import top.guangyiliushan.rebecca.navigation.rememberAppBackStack
import top.guangyiliushan.rebecca.navigation.topLevelNavItems

/**
 * AppIdentityBar 可见路由（**产品决策 2026-09-14：仅首页 Study**）。
 * 依据：身份栏（头像/母语/学习语言）只在首页有配合交互；其余界面（Lists/Dictionary/Explore/
 * Quiz/Showcase/Settings）对它没有可用的交互，保留即噪音 → 全部不装。
 * 这是对 05 spec §Route-level visibility 原表（四条顶层学习路由全装）的**产品偏离**，已登记于法典与 09 spec。
 */
private val identityBarRoutes = setOf(Route.Study)

/** 路由级显隐判定（抽出供单测锁定可见性表）。 */
internal fun shouldShowIdentityBar(route: Route?): Boolean =
    route != null && route in identityBarRoutes

@Composable
fun App() {
    var settings by remember { mutableStateOf(ThemeSettings()) }
    AppTheme(theme = settings) {
        val backStack = rememberAppBackStack()
        val route: Route? = backStack.lastOrNull() // JS 目标类型推断严格，显式标注
        // 主从双栏（0.1.2 Q4）：expanded 时 Lists 屏 detail 槽同屏渲染详情；
        // selectedListId 由当前路由推导（单数据源，compact 压栈与 expanded 同屏共用）。
        val selectedListId: String? = (route as? Route.WordListDetail)?.listId
        AppScaffold(
            navItems = topLevelNavItems,
            currentRoute = route,
            onNavigate = { target ->
                backStack.clear()
                backStack.add(target)
            },
            detail = selectedListId?.let { listId ->
                {
                    WordListDetailScreen(
                        listId = listId,
                        // expanded 同屏时 back = 关详情（回纯列表）
                        onBack = { backStack.removeLastOrNull() },
                        onPractice = { backStack.add(Route.CardSession(listId)) },
                    )
                }
            },
            // 身份条按路由显隐（产品决策：**仅首页 Study**；详见 identityBarRoutes）
            topBar = {
                if (shouldShowIdentityBar(route)) {
                    AppIdentityBar(
                        name = stringResource(Res.string.identity_local_learner),
                        languagePairLabel = stringResource(Res.string.identity_language_pair_short),
                        languagePairSemantics = stringResource(Res.string.identity_language_pair_semantics),
                    )
                }
            },
        ) {
            NavDisplay(
                backStack = backStack,
                onBack = { backStack.removeLastOrNull() },
                entryProvider = { route: Route ->
                    when (route) {
                        Route.Study -> NavEntry(route) {
                            StudyScreen(onModeChosen = { mode -> backStack.add(Route.Quiz(mode)) })
                        }
                        Route.Lists -> NavEntry(route) {
                            ListsScreen(
                                onOpenList = { backStack.add(Route.WordListDetail(it)) },
                                onPractice = { mode -> backStack.add(Route.Quiz(mode)) },
                            )
                        }
                        Route.Dictionary -> NavEntry(route) { DictionaryScreen() }
                        Route.Explore -> NavEntry(route) { ExploreScreen() }
                        Route.Settings -> NavEntry(route) { SettingsScreen() }
                        Route.Showcase -> NavEntry(route) {
                            ShowcaseScreen(
                                settings = settings,
                                onModeChange = { mode -> settings = settings.copy(mode = mode) },
                            )
                        }
                        is Route.WordListDetail -> NavEntry(route) {
                            // R-D1：双栏生效时左栏渲染库屏（详情走 detail 槽，避免双重渲染/左栏错屏）；
                            // 单栏时路由栈自行渲染详情屏幕。
                            if (LocalDetailPaneVisible.current) {
                                ListsScreen(
                                    // 双栏：点行 = 替换栈顶选中（左栏保持库屏）
                                    onOpenList = { listId ->
                                        if (backStack.isNotEmpty()) {
                                            backStack[backStack.size - 1] = Route.WordListDetail(listId)
                                        }
                                    },
                                    onPractice = { backStack.add(Route.CardSession(route.listId)) },
                                )
                            } else {
                                WordListDetailScreen(
                                    listId = route.listId,
                                    onBack = { backStack.removeLastOrNull() },
                                    onPractice = { mode -> backStack.add(Route.CardSession(route.listId)) },
                                )
                            }
                        }
                        is Route.CardSession -> NavEntry(route) {
                            CardSessionScreen(
                                listId = route.listId,
                                onBack = { backStack.removeLastOrNull() },
                            )
                        }
                        is Route.Quiz -> NavEntry(route) {
                            QuizScreen(
                                mode = route.mode,
                                onBackToHub = { backStack.removeLastOrNull() },
                            )
                        }
                    }
                },
            )
        }
    }
}
