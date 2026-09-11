package top.guangyiliushan.rebecca.design.scaffold

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.ExperimentalMaterial3AdaptiveNavigationSuiteApi
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItem
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldState
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.material3.adaptive.navigationsuite.rememberNavigationSuiteScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.window.core.layout.WindowSizeClass
import org.jetbrains.compose.resources.stringResource
import top.guangyiliushan.rebecca.design.theme.AppTheme
import top.guangyiliushan.rebecca.navigation.AppNavItem
import top.guangyiliushan.rebecca.navigation.Route

/**
 * 断点唯一入口（F-L4/F9）：官方 `NavigationSuiteScaffold`。
 * v0.3 D5 改造：**显式传 `navigationSuiteType`**（默认参数走已过期的 calculateFromAdaptiveInfo 旧映射）——
 * compact→`ShortNavigationBarCompact`、medium→`ShortNavigationBarMedium`、expanded→**`WideNavigationRailExpanded`**
 * （§7.1 显式偏离：学习类应用主导航文字标签常驻，不用官方默认的 Collapsed）。
 * 迁到官方推荐 `navigationItems` 新形态（`NavigationSuiteItem`）。
 * 内容 inset（安全区）由 NavigationSuiteScaffold 内建 consumeWindowInsets 白拿；收起态 inset 归零。
 * `detail` 非空整页呈现；expanded 主从双栏 0.1.2 落地（接口已在）。
 */
@OptIn(ExperimentalMaterial3AdaptiveNavigationSuiteApi::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun AppScaffold(
    navItems: List<AppNavItem>,
    currentRoute: Route?,
    onNavigate: (Route) -> Unit,
    modifier: Modifier = Modifier,
    topBar: (@Composable () -> Unit)? = null,
    detail: (@Composable () -> Unit)? = null,
    navVisibility: NavigationSuiteScaffoldState? = null,
    content: @Composable () -> Unit,
) {
    val scaffoldState: NavigationSuiteScaffoldState =
        navVisibility ?: rememberNavigationSuiteScaffoldState()
    val adaptiveInfo = currentWindowAdaptiveInfo()

    // D5：显式映射。官方推荐 navigationSuiteType() = compact→ShortNavigationBarCompact、
    // tabletop/矮窗口→ShortNavigationBarMedium、否则→WideNavigationRailCollapsed；
    // 我们的一处偏离：expanded（≥840dp 宽窗）用 WideNavigationRailExpanded（文字标签常驻，§7.1）。
    val suiteType: NavigationSuiteType = if (
        adaptiveInfo.windowSizeClass.minWidthDp >= WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND
    ) {
        NavigationSuiteType.WideNavigationRailExpanded
    } else {
        NavigationSuiteScaffoldDefaults.navigationSuiteType(adaptiveInfo)
    }

    NavigationSuiteScaffold(
        modifier = modifier,
        navigationItems = {
            navItems.forEach { navItem ->
                NavigationSuiteItem(
                    selected = navItem.route == currentRoute,
                    onClick = { onNavigate(navItem.route) },
                    icon = { Icon(navItem.icon, contentDescription = null) },
                    label = { Text(stringResource(navItem.labelRes)) },
                )
            }
        },
        navigationSuiteType = suiteType,
        state = scaffoldState,
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            topBar?.invoke()
            Box(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.TopCenter,
            ) {
                // 内容宽度上限（桌面/Web 不拉面条）
                Box(modifier = Modifier.widthIn(max = AppTheme.contentWidth.default).fillMaxHeight()) {
                    (detail ?: content)()   // detail 非空整页呈现；主从双栏 0.1.2 落地
                }
            }
        }
    }
}
