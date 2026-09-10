package top.guangyiliushan.rebecca

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.font.FontFamily
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.preloadFont

import rebecca.app.webapp.generated.resources.NotoSansSC
import rebecca.app.webapp.generated.resources.Res

/**
 * 字体后台预载，渲染不等待。
 * 原模板用 `if (fontFallbackInitialized) content()` 阻塞整页渲染等待 CJK 字体——字体加载失败/缓慢
 * 会导致白屏（0.0.3 在 wasm 复现）。改为无条件渲染，字体就绪后 `fontFamilyResolver.preload` 触发重组。
 */
@OptIn(ExperimentalResourceApi::class)
@Composable
internal inline fun WithFontResourcesLoaded(
    content: @Composable () -> Unit
) {
    val font by preloadFont(Res.font.NotoSansSC)
    val fontFamilyResolver = LocalFontFamilyResolver.current

    LaunchedEffect(fontFamilyResolver, font) {
        font?.let { font ->
            fontFamilyResolver.preload(FontFamily(font))
        }
    }

    content()
}
