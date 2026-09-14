package top.guangyiliushan.rebecca.design.patterns

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import top.guangyiliushan.rebecca.design.components.AppCard
import top.guangyiliushan.rebecca.design.theme.AppTheme
import top.guangyiliushan.rebecca.design.theme.ThemeSettings

/**
 * 主从双栏（frontend-design-system §7.3；0.1.2 落地）。
 * 读 `LocalLayoutDensity`（AppScaffold 下发）——**屏幕不读窗口尺寸**（F-L4）：
 * Regular 且有 detail → 左右双栏；否则单栏（detail 由调用方经路由压栈呈现）。
 * 宽度上限归 AppScaffold（§7.2 内容宽契约），本组件不自套宽度约束，避免两处分叉。
 */
@Composable
fun MasterDetailPane(
    list: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    detail: (@Composable () -> Unit)? = null,
) {
    val density = LocalLayoutDensity.current
    if (density == LayoutDensity.Regular && detail != null) {
        Row(
            modifier = modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md),
        ) {
            Box(modifier = Modifier.weight(1f)) { list() }
            Box(modifier = Modifier.weight(1.15f)) { detail() }
        }
    } else {
        Box(modifier = modifier.fillMaxSize()) { list() }
    }
}

@PreviewLightDark
@PreviewFontScale
@Composable
private fun MasterDetailPanePreview() {
    AppTheme(ThemeSettings()) {
        MasterDetailPane(
            modifier = Modifier.fillMaxSize(),
            list = {
                AppCard(modifier = Modifier.fillMaxSize()) {
                    androidx.compose.material3.Text("List")
                }
            },
            detail = {
                AppCard(modifier = Modifier.fillMaxSize()) {
                    androidx.compose.material3.Text("Detail")
                }
            },
        )
    }
}
