package top.guangyiliushan.rebecca

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import top.guangyiliushan.rebecca.design.showcase.TokensShowcase
import top.guangyiliushan.rebecca.design.theme.AppTheme
import top.guangyiliushan.rebecca.design.theme.ThemeSettings

@Composable
fun App() {
    var settings by remember { mutableStateOf(ThemeSettings()) }
    AppTheme(theme = settings) {
        TokensShowcase(settings = settings, onModeChange = { mode ->
            settings = settings.copy(mode = mode)
        })
    }
}
