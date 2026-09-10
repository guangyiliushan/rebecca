package top.guangyiliushan.rebecca

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import top.guangyiliushan.rebecca.design.components.AppScaffold
import top.guangyiliushan.rebecca.design.theme.AppTheme
import top.guangyiliushan.rebecca.design.theme.ThemeSettings
import top.guangyiliushan.rebecca.feature.dictionary.DictionaryScreen
import top.guangyiliushan.rebecca.feature.explore.ExploreScreen
import top.guangyiliushan.rebecca.feature.lists.ListsScreen
import top.guangyiliushan.rebecca.feature.settings.SettingsScreen
import top.guangyiliushan.rebecca.feature.showcase.ShowcaseScreen
import top.guangyiliushan.rebecca.feature.study.StudyScreen
import top.guangyiliushan.rebecca.navigation.Route
import top.guangyiliushan.rebecca.navigation.rememberAppBackStack
import top.guangyiliushan.rebecca.navigation.topLevelNavItems

@Composable
fun App() {
    var settings by remember { mutableStateOf(ThemeSettings()) }
    AppTheme(theme = settings) {
        val backStack = rememberAppBackStack()
        AppScaffold(
            navItems = topLevelNavItems,
            currentRoute = backStack.lastOrNull(),
            onNavigate = { route ->
                backStack.clear()
                backStack.add(route)
            },
        ) {
            NavDisplay(
                backStack = backStack,
                onBack = { backStack.removeLastOrNull() },
                entryProvider = { route: Route ->
                    when (route) {
                        Route.Study -> NavEntry(route) { StudyScreen() }
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
                    }
                },
            )
        }
    }
}
