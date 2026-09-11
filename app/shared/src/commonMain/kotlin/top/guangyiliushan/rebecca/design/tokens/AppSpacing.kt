package top.guangyiliushan.rebecca.design.tokens

import androidx.compose.ui.unit.dp

/** 间距令牌（frontend-design-system v0.3 §3.2：对齐 shadcn 用到的全部步长）。 */
object AppSpacing {
    val xxs = 2.dp               // shadcn gap-0.5
    val xs = 4.dp
    val sm = 8.dp
    val md = 16.dp
    val lg = 24.dp
    val xl = 32.dp
    val xxl = 48.dp
    val tight = 6.dp             // shadcn gap-1.5（field/sheet/item 高频）
    val compact = 10.dp          // shadcn gap-2.5（item size=sm）
    val controlPadding = 12.dp   // shadcn px-3（控件内边距）
}
