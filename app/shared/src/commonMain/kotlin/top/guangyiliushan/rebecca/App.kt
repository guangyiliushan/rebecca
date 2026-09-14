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
import top.guangyiliushan.rebecca.design.theme.AppTheme
import top.guangyiliushan.rebecca.design.theme.ThemeSettings
import top.guangyiliushan.rebecca.feature.dictionary.DictionaryScreen
import top.guangyiliushan.rebecca.feature.explore.ExploreScreen
import top.guangyiliushan.rebecca.feature.lists.ListsScreen
import top.guangyiliushan.rebecca.feature.settings.SettingsScreen
import top.guangyiliushan.rebecca.feature.showcase.ShowcaseScreen
import top.guangyiliushan.rebecca.feature.study.QuizScreen
import top.guangyiliushan.rebecca.feature.study.StudyScreen
import top.guangyiliushan.rebecca.navigation.Route
import top.guangyiliushan.rebecca.navigation.rememberAppBackStack
import top.guangyiliushan.rebecca.navigation.topLevelNavItems

/** AppIdentityBar 可见路由（05 spec §Route-level visibility；Quiz/Showcase/Settings 不装）。 */
private val identityBarRoutes = setOf(Route.Study, Route.Lists, Route.Dictionary, Route.Explore)

/** 路由级显隐判定（抽出供单测锁定 05 spec 的可见性表）。 */
internal fun shouldShowIdentityBar(route: Route?): Boolean =
    route != null && route in identityBarRoutes

@Composable
fun App() {
    var settings by remember { mutableStateOf(ThemeSettings()) }
    AppTheme(theme = settings) {
        val backStack = rememberAppBackStack()
        val route: Route? = backStack.lastOrNull() // JS 目标类型推断严格，显式标注
        AppScaffold(
            navItems = topLevelNavItems,
            currentRoute = route,
            onNavigate = { target ->
                backStack.clear()
                backStack.add(target)
            },
            // 身份条按路由显隐（05 spec §Route-level visibility）：
            // Study/Lists/Dictionary/Explore 装；Settings/Quiz/Showcase 与子流程不装。
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
                        Route.Lists -> NavEntry(route) { ListsScreen() }
                        Route.Dictionary -> NavEntry(route) { DictionaryScreen() }
                        Route.Explore -> NavEntry(route) { ExploreScreen() }
                        Route.Settings -> NavEntry(route) { SettingsScreen() }
                        Route.Showcase -> NavEntry(route) {
                            ShowcaseScreen(
                                settings = settings,
                                onModeChange = { mode -> settings = settings.copy(mode = mode) },
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
