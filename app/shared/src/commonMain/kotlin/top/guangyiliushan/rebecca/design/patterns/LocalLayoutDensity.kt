package top.guangyiliushan.rebecca.design.patterns

import androidx.compose.runtime.staticCompositionLocalOf

/**
 * 容器查询替代机制的骨架（frontend-design-system v0.3 §7.3，决策 D4）：
 * patterns 层测量容器 → 以密度档下发；组件只读此 CompositionLocal，不读窗口尺寸（不破 F9）。
 * 0.1.0 只落空壳；0.1.2 的 MasterDetailPane 与 AppField.Responsive 在此骨架上实现。
 */
enum class LayoutDensity { Compact, Regular }

val LocalLayoutDensity = staticCompositionLocalOf { LayoutDensity.Regular }
