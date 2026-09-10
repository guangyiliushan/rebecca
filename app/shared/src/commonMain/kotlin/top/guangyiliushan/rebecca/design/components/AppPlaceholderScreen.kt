package top.guangyiliushan.rebecca.design.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import top.guangyiliushan.rebecca.design.theme.AppTheme

/** 占位屏（0.1.1-0.1.4 迁移时替换为真实屏幕）。视觉单元归 design/（F3）。 */
@Composable
fun AppPlaceholderScreen(titleRes: StringResource, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = stringResource(titleRes),
            style = MaterialTheme.typography.titleLarge,
            color = AppTheme.colors.onBackground,
        )
    }
}
