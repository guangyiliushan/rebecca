package top.guangyiliushan.rebecca.design.tokens

import androidx.compose.ui.graphics.Color

/**
 * 弹层 scrim 令牌（frontend-design-system v0.3 §3.1「非色槽」）：
 * 统一 Black @ 60%（skiko Dialog 官方默认 DefaultScrimColor，§10.6 弹层动效基线），
 * 不随主题切换。位于 tokens 白名单内（F1）。
 */
object AppScrim {
    val color: Color = Color.Black.copy(alpha = 0.6f)
}
