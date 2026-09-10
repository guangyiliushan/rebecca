package top.guangyiliushan.rebecca.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import com.github.terrakok.navigation3.browser.ChronologicalBrowserNavigation
import com.github.terrakok.navigation3.browser.buildBrowserHistoryFragment
import com.github.terrakok.navigation3.browser.getBrowserHistoryFragmentName

/** wasmJs 端：chronological（Web 风格）浏览器历史，地址栏可读 URL + 后退/前进。 */
@Composable
actual fun rememberAppBackStack(): MutableList<Route> {
    val backStack = remember { mutableStateListOf<Route>(Route.Study) }
    ChronologicalBrowserNavigation(
        backStack = backStack,
        saveKey = { route -> buildBrowserHistoryFragment(route.fragmentName) },
        restoreKey = { fragment ->
            getBrowserHistoryFragmentName(fragment)?.let(::routeFromFragmentName)
        },
    )
    return backStack
}
