package top.guangyiliushan.rebecca.design.components

import androidx.compose.runtime.staticCompositionLocalOf

/**
 * 表单域上下文（frontend-design-system v0.3 §6.5 AppField，对应官方 field.tsx 的
 * `data-[invalid=true]` 父→子级联的 Compose 等价物）。
 * AppInput 读此决定描边色与 error 语义（errors 首条进 error()，文案来自调用方 catalog，F6）。
 * 屏幕不得直接读（组合用 AppField）。
 */
data class AppFieldState(
    val invalid: Boolean = false,
    val enabled: Boolean = true,
    val errors: List<String> = emptyList(),
)

val LocalAppFieldState = staticCompositionLocalOf { AppFieldState() }
