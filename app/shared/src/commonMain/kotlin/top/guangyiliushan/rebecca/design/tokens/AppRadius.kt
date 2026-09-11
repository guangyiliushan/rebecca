package top.guangyiliushan.rebecca.design.tokens

import androidx.compose.ui.unit.dp

/**
 * 圆角令牌（frontend-design-system v0.3 §3.2：重定为 M3 官方 Shapes 五档 + full）。
 * shadcn 映射：rounded-xs 2 / rounded-sm 4 → extraSmall 4；rounded-md 6 / rounded-lg 8 → small 8（全库最高频）；
 * rounded-xl → medium 12；rounded-2xl → large 16；rounded-3xl → extraLarge 24（M3 默认 28，取 Reply 示例 24）。
 * 理由：AppTheme 桥接 M3 MaterialTheme(shapes)，取 M3 官方档零翻译；±1-2px 映射差异在 COMPONENTS.md 显式记录。
 */
object AppRadius {
    val extraSmall = 4.dp
    val small = 8.dp
    val medium = 12.dp
    val large = 16.dp
    val extraLarge = 24.dp

    /** 全圆角，百分比语义：消费方 `RoundedCornerShape(percent = AppRadius.full)`。
     *  CMP 1.11 已移除 `Percentage`/`percent` 扩展，故以 Int 落值。 */
    val full = 50
}
