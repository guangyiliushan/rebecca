package top.guangyiliushan.rebecca.design.tokens

import androidx.compose.ui.unit.dp

/**
 * 动效令牌（frontend-design-system v0.3 §10.6 弹层动效基线：取自平台官方值）。
 * 进场 0.2s / 出场 0.1s、缩放起点 0.95（= shadcn zoom-in-95）、scrim 黑 60%（Dialog.skiko.kt 常量）。
 * §13-5 动画语言令牌化时以此为首批值。
 * v0.5 补：手势判定位移入档（F2 禁止组件内魔法 dp；CardSwipeStack 的 AnchoredDraggable 锚点距离）。
 */
object AppMotion {
    const val enterMs = 200
    const val exitMs = 100
    const val dialogScale = 0.95f
    val swipeDecisionThreshold = 96.dp
}
