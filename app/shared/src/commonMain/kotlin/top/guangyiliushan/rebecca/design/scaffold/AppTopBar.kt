package top.guangyiliushan.rebecca.design.scaffold

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.AppBarMenuState
import androidx.compose.material3.AppBarRow
import androidx.compose.material3.AppBarRowScope
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import top.guangyiliushan.rebecca.design.components.AppIconButton
import top.guangyiliushan.rebecca.design.theme.AppTheme
import org.jetbrains.compose.resources.stringResource
import rebecca.app.shared.generated.resources.Res
import rebecca.app.shared.generated.resources.action_more_options

/** AppTopBar 高度档（无 shadcn 出处，规格以 M3 为准；由 AppScaffold 传入，屏幕不读尺寸）。 */
enum class AppTopBarSize { Small, Medium, Large }

/**
 * 顶栏（frontend-design-system v0.3 §6.5；规格页 08-apptopbar。**无 shadcn 对应物**，以 M3 为准）。
 * 动作区走 M3 1.12 **AppBarRow DSL（自动溢出菜单）**——放不下的动作自动进 "…" 菜单（官方意图的自适应）。
 * ⚠ F6 平台库文案泄漏规避（§3.3/§9.1 F6 例外）：AppBarOverflowIndicator 默认实现用 M3 渠道文案
 * （跟平台 locale 不跟 uiLanguage）→ 自传 overflowIndicator，label 来自 catalog。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: (@Composable () -> Unit)? = null,
    size: AppTopBarSize = AppTopBarSize.Small,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    actions: (AppBarRowScope.() -> Unit)? = null,
) {
    val colors = AppTheme.colors

    // 自传 overflowIndicator：contentDescription 来自 catalog（规避平台库文案泄漏）
    val overflowIndicator: @Composable (AppBarMenuState) -> Unit = { menuState ->
        AppIconButton(
            onClick = { menuState.show() },
            icon = { androidx.compose.material3.Text("⋯") },
            contentDescription = stringResource(Res.string.action_more_options),
        )
    }

    // insets 归 AppScaffold（唯一入口，F-L4/§7.2）：AppTopBar 不自带 windowInsets，
    // 否则与 scaffold 的 safeDrawing 顶部内边距双算（review 🟠1：Android 答题页顶部白条翻倍）
    val noInsets = WindowInsets(0, 0, 0, 0)

    when (size) {
        AppTopBarSize.Small -> TopAppBar(
            title = title,
            modifier = modifier,
            navigationIcon = navigationIcon ?: {},
            actions = {
                if (actions != null) {
                    AppBarRow(overflowIndicator = overflowIndicator) { actions() }
                }
            },
            scrollBehavior = scrollBehavior,
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = colors.surface,
                titleContentColor = colors.onSurface,
                navigationIconContentColor = colors.onSurface,
                actionIconContentColor = colors.onSurface,
            ),
            windowInsets = noInsets,
        )
        AppTopBarSize.Medium -> MediumTopAppBar(
            title = title,
            modifier = modifier,
            navigationIcon = navigationIcon ?: {},
            actions = {
                if (actions != null) {
                    AppBarRow(overflowIndicator = overflowIndicator) { actions() }
                }
            },
            scrollBehavior = scrollBehavior,
            colors = TopAppBarDefaults.mediumTopAppBarColors(
                containerColor = colors.surface,
                titleContentColor = colors.onSurface,
                navigationIconContentColor = colors.onSurface,
                actionIconContentColor = colors.onSurface,
            ),
            windowInsets = noInsets,
        )
        AppTopBarSize.Large -> LargeTopAppBar(
            title = title,
            modifier = modifier,
            navigationIcon = navigationIcon ?: {},
            actions = {
                if (actions != null) {
                    AppBarRow(overflowIndicator = overflowIndicator) { actions() }
                }
            },
            scrollBehavior = scrollBehavior,
            colors = TopAppBarDefaults.largeTopAppBarColors(
                containerColor = colors.surface,
                titleContentColor = colors.onSurface,
                navigationIconContentColor = colors.onSurface,
                actionIconContentColor = colors.onSurface,
            ),
            windowInsets = noInsets,
        )
    }
}

@PreviewLightDark
@PreviewFontScale
@Composable
private fun AppTopBarPreview() {
    AppTheme {
        AppTopBar(title = { androidx.compose.material3.Text("Title") })
    }
}
