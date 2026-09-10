package top.guangyiliushan.rebecca.feature.lists

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import rebecca.app.shared.generated.resources.Res
import rebecca.app.shared.generated.resources.screen_lists
import top.guangyiliushan.rebecca.design.components.AppPlaceholderScreen

@Composable
fun ListsScreen(modifier: Modifier = Modifier) {
    AppPlaceholderScreen(titleRes = Res.string.screen_lists, modifier = modifier)
}
