package top.guangyiliushan.rebecca.design.tokens

/**
 * 动效令牌（frontend-design-system v0.3 §10.6 弹层动效基线：取自平台官方值）。
 * 进场 0.2s / 出场 0.1s、缩放起点 0.95（= shadcn zoom-in-95）、scrim 黑 60%（Dialog.skiko.kt 常量）。
 * §13-5 动画语言令牌化时以此为首批值。
 */
object AppMotion {
    val enterMs = 200
    val exitMs = 100
    val dialogScale = 0.95f
}
