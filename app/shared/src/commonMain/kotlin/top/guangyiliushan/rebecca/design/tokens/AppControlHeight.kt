package top.guangyiliushan.rebecca.design.tokens

import androidx.compose.ui.unit.dp

/**
 * 控件高度令牌（frontend-design-system v0.3 §3.2 / F8「视觉尺寸」依据）。
 * 出处：shadcn h-6/h-8/h-9/h-10/h-12；主轴 Md=36（h-9，全库最高频）。
 * 注意：这是**视觉**尺寸；触控热区 ≥48dp 由 `Modifier.minimumInteractiveComponentSize()` 保证（F8）。
 */
object AppControlHeight {
    val Xs = 24.dp
    val Sm = 32.dp
    val Md = 36.dp
    val Lg = 40.dp
    val Xl = 48.dp
}
