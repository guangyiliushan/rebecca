package top.guangyiliushan.rebecca.feature.showcase

import androidx.compose.runtime.Composable
import top.guangyiliushan.rebecca.design.showcase.TokensShowcase
import top.guangyiliushan.rebecca.design.theme.ThemeMode
import top.guangyiliushan.rebecca.design.theme.ThemeSettings

/** Showcase debug route 的入口封装；0.1.0 在此升级为完整组件陈列屏。 */
@Composable
fun ShowcaseScreen(settings: ThemeSettings, onModeChange: (ThemeMode) -> Unit) {
    TokensShowcase(settings = settings, onModeChange = onModeChange)
}
