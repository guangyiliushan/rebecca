package top.guangyiliushan.rebecca.design.theme

enum class ThemeMode { System, Light, Dark }

data class ThemeSettings(
    val mode: ThemeMode = ThemeMode.System,
    val family: ThemeFamily = ThemeFamily.Teal,
)

/** 主题模式解析：System 跟随系统，显式模式覆盖。纯函数。 */
fun resolveDark(mode: ThemeMode, systemDark: Boolean): Boolean = when (mode) {
    ThemeMode.System -> systemDark
    ThemeMode.Light -> false
    ThemeMode.Dark -> true
}
