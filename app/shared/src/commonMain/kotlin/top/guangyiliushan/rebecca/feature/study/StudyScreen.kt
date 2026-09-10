package top.guangyiliushan.rebecca.feature.study

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import rebecca.app.shared.generated.resources.Res
import rebecca.app.shared.generated.resources.screen_study
import top.guangyiliushan.rebecca.design.components.AppPlaceholderScreen

@Composable
fun StudyScreen(modifier: Modifier = Modifier) {
    AppPlaceholderScreen(titleRes = Res.string.screen_study, modifier = modifier)
}
