package top.guangyiliushan.rebecca.design.tokens

import androidx.compose.ui.unit.dp

object AppRadius {
    val sm = 6.dp
    val md = 10.dp
    val lg = 16.dp
    val xl = 24.dp

    /** 全圆角，百分比语义：消费方 `RoundedCornerShape(percent = AppRadius.full)`。
     *  CMP 1.11 已移除 `Percentage`/`percent` 扩展，故以 Int 落值。 */
    val full = 50
}
