package top.guangyiliushan.rebecca.design.tokens

import androidx.compose.ui.unit.dp

/**
 * 浮层宽度阶梯（frontend-design-system v0.3 §3.2）。
 * 出处：alert-dialog sm=max-w-xs(320)；sheet sm:max-w-sm(384)；dialog sm:max-w-lg(512)；drawer 640。
 */
object AppOverlayWidth {
    val Xs = 320.dp
    val Sm = 384.dp
    val Md = 512.dp
    val Lg = 640.dp
}

/** 浮层高度约束。出处：drawer max-h-[80vh](0.8)。 */
object AppOverlaySize {
    val MaxHeightRatio = 0.8f
}
