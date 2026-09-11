package top.guangyiliushan.rebecca.design.scaffold

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.ExperimentalMaterial3AdaptiveNavigationSuiteApi
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.stringResource
import top.guangyiliushan.rebecca.design.theme.AppTheme
import top.guangyiliushan.rebecca.navigation.AppNavItem
import top.guangyiliushan.rebecca.navigation.Route

/**
 * 断点唯一入口（F-L4/F9）：官方 `NavigationSuiteScaffold`（material3-adaptive-navigation-suite）
 * 按 WindowSizeClass 自动切 NavigationBar / NavigationRail / NavigationDrawer，且默认应用内容 inset（安全区）。
 * `detail`/`topBar` 0.0.3 留接口：detail 非空时整页呈现，expanded 主从双栏 0.1.2 落地；topBar 暂直排在内容上方。
 */
@OptIn(ExperimentalMaterial3AdaptiveNavigationSuiteApi::class)
@Composable
fun AppScaffold(
    navItems: List<AppNavItem>,
    currentRoute: Route?,
    onNavigate: (Route) -> Unit,
    modifier: Modifier = Modifier,
    topBar: (@Composable () -> Unit)? = null,
    detail: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    NavigationSuiteScaffold(
        modifier = modifier,
        navigationSuiteItems = {
            navItems.forEach { navItem ->
                item(
                    selected = navItem.route == currentRoute,
                    onClick = { onNavigate(navItem.route) },
                    icon = { Icon(navItem.icon, contentDescription = null) },
                    label = { Text(stringResource(navItem.labelRes)) },
                )
            }
        },
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
