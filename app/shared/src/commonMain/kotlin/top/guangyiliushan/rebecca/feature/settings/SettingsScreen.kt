package top.guangyiliushan.rebecca.feature.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import rebecca.app.shared.generated.resources.Res
import rebecca.app.shared.generated.resources.screen_settings
import top.guangyiliushan.rebecca.design.components.AppPlaceholderScreen

@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    AppPlaceholderScreen(titleRes = Res.string.screen_settings, modifier = modifier)
}
