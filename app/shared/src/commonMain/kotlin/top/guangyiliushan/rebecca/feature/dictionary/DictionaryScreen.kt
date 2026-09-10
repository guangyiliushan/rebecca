package top.guangyiliushan.rebecca.feature.dictionary

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import rebecca.app.shared.generated.resources.Res
import rebecca.app.shared.generated.resources.screen_dictionary
import top.guangyiliushan.rebecca.design.components.AppPlaceholderScreen

@Composable
fun DictionaryScreen(modifier: Modifier = Modifier) {
    AppPlaceholderScreen(titleRes = Res.string.screen_dictionary, modifier = modifier)
}
