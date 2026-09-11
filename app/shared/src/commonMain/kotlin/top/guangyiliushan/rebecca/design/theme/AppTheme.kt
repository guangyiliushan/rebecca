package top.guangyiliushan.rebecca.design.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.key
import androidx.compose.runtime.staticCompositionLocalOf
import top.guangyiliushan.rebecca.design.theme.palettes.TealPalette
import top.guangyiliushan.rebecca.design.tokens.AppColors
import top.guangyiliushan.rebecca.design.tokens.AppContentWidth
import top.guangyiliushan.rebecca.design.tokens.AppControlHeight
import top.guangyiliushan.rebecca.design.tokens.AppElevation
import top.guangyiliushan.rebecca.design.tokens.AppIconSize
import top.guangyiliushan.rebecca.design.tokens.AppMotion
import top.guangyiliushan.rebecca.design.tokens.AppOverlaySize
import top.guangyiliushan.rebecca.design.tokens.AppOverlayWidth
import top.guangyiliushan.rebecca.design.tokens.AppRadius
import top.guangyiliushan.rebecca.design.tokens.AppSpacing
import top.guangyiliushan.rebecca.design.tokens.AppType

// ---- CompositionLocals（tokens 的提供入口，frontend-design-system §3.3）----
val LocalAppColors = staticCompositionLocalOf { TealPalette.light }
val LocalAppSpacing = staticCompositionLocalOf { AppSpacing }
val LocalAppRadius = staticCompositionLocalOf { AppRadius }
val LocalAppType = staticCompositionLocalOf { AppType() }
val LocalAppElevation = staticCompositionLocalOf { AppElevation }
val LocalAppContentWidth = staticCompositionLocalOf { AppContentWidth }
val LocalAppControlHeight = staticCompositionLocalOf { AppControlHeight }
val LocalAppIconSize = staticCompositionLocalOf { AppIconSize }
val LocalAppOverlayWidth = staticCompositionLocalOf { AppOverlayWidth }
val LocalAppOverlaySize = staticCompositionLocalOf { AppOverlaySize }
val LocalAppMotion = staticCompositionLocalOf { AppMotion }

@Composable
fun AppTheme(
    theme: ThemeSettings = ThemeSettings(),
    content: @Composable () -> Unit,
) {
    val dark = resolveDark(theme.mode, isSystemInDarkTheme())
    val colors = theme.family.colors(dark)
    CompositionLocalProvider(
        LocalAppColors provides colors,
        LocalAppSpacing provides AppSpacing,
        LocalAppRadius provides AppRadius,
        LocalAppType provides AppType(),
        LocalAppElevation provides AppElevation,
        LocalAppContentWidth provides AppContentWidth,
        LocalAppControlHeight provides AppControlHeight,
        LocalAppIconSize provides AppIconSize,
        LocalAppOverlayWidth provides AppOverlayWidth,
        LocalAppOverlaySize provides AppOverlaySize,
        LocalAppMotion provides AppMotion,
    ) {
        key(theme) {   // 主题切换整块重建（frontend-design-system §4.1）
            MaterialTheme(
                colorScheme = colors.toMaterialColorScheme(),
                typography = AppType().toMaterialTypography(),
                shapes = AppShapes,
                content = content,
            )
        }
    }
}

/** 读取入口唯一：AppTheme.colors.xxx / AppTheme.spacing.xxx（frontend-design-system §3.3） */
object AppTheme {
    val colors: AppColors @Composable @ReadOnlyComposable get() = LocalAppColors.current
    val spacing: AppSpacing @Composable @ReadOnlyComposable get() = LocalAppSpacing.current
    val radius: AppRadius @Composable @ReadOnlyComposable get() = LocalAppRadius.current
    val type: AppType @Composable @ReadOnlyComposable get() = LocalAppType.current
    val elevation: AppElevation @Composable @ReadOnlyComposable get() = LocalAppElevation.current
    val contentWidth: AppContentWidth @Composable @ReadOnlyComposable get() = LocalAppContentWidth.current
    val controlHeight: AppControlHeight @Composable @ReadOnlyComposable get() = LocalAppControlHeight.current
    val iconSize: AppIconSize @Composable @ReadOnlyComposable get() = LocalAppIconSize.current
    val overlayWidth: AppOverlayWidth @Composable @ReadOnlyComposable get() = LocalAppOverlayWidth.current
    val overlaySize: AppOverlaySize @Composable @ReadOnlyComposable get() = LocalAppOverlaySize.current
    val motion: AppMotion @Composable @ReadOnlyComposable get() = LocalAppMotion.current
}

/**
 * M3 桥接（frontend-design-system §3.3 注记）：
 * 19 语义槽一对一映射；容器槽 = 就近语义槽或 alpha 派生，不新增语义槽、不落色值文档。
 * 全部 48 个 M3 槽均被覆盖（copy 自 lightColorScheme 基座），故亮暗差异完全由 AppColors 决定，函数无 dark 参数。
 */
fun AppColors.toMaterialColorScheme(): ColorScheme {
    return lightColorScheme().copy(
        // 直接映射
        primary = primary, onPrimary = onPrimary,
        background = background, onBackground = onBackground,
        surface = surface, onSurface = onSurface,
        // 语义槽 → M3 角色
        secondary = primary, onSecondary = onPrimary,
        tertiary = accent, onTertiary = onAccent,
        error = destructive, onError = onDestructive,
        surfaceVariant = muted, onSurfaceVariant = onMuted,
        outline = border, outlineVariant = border,
        surfaceTint = primary,
        // 容器槽：派生（accent 槽即"容器色对"；error 容器 alpha 派生）
        primaryContainer = accent, onPrimaryContainer = onAccent,
        secondaryContainer = accent, onSecondaryContainer = onAccent,
        tertiaryContainer = accent, onTertiaryContainer = onAccent,
        errorContainer = destructive.copy(alpha = 0.12f), onErrorContainer = destructive,
        inverseSurface = onSurface, inverseOnSurface = surface, inversePrimary = primary,
        scrim = onBackground.copy(alpha = 0.32f),
        // 表面层级：v1 扁平设计，容器层次退化平铺（高度表达交给 AppElevation）
        surfaceBright = surface, surfaceDim = surface,
        surfaceContainer = surface, surfaceContainerHigh = surface, surfaceContainerHighest = surface,
        surfaceContainerLow = background, surfaceContainerLowest = background,
        // fixed 系列：镜像容器
        primaryFixed = accent, primaryFixedDim = muted,
        onPrimaryFixed = onAccent, onPrimaryFixedVariant = primary,
        secondaryFixed = muted, secondaryFixedDim = background,
        onSecondaryFixed = onMuted, onSecondaryFixedVariant = onSurface,
        tertiaryFixed = accent, tertiaryFixedDim = muted,
        onTertiaryFixed = onAccent, onTertiaryFixedVariant = onAccent,
    )
}

/** 字阶桥接：AppType M3 官方 15 档 → M3 Typography（零翻译，size/lineHeight 1:1）。 */
fun AppType.toMaterialTypography(): Typography {
    val base = Typography()
    return base.copy(
        displayLarge = base.displayLarge.copy(fontSize = displayLarge.size, lineHeight = displayLarge.lineHeight),
        displayMedium = base.displayMedium.copy(fontSize = displayMedium.size, lineHeight = displayMedium.lineHeight),
        displaySmall = base.displaySmall.copy(fontSize = displaySmall.size, lineHeight = displaySmall.lineHeight),
        headlineLarge = base.headlineLarge.copy(fontSize = headlineLarge.size, lineHeight = headlineLarge.lineHeight),
        headlineMedium = base.headlineMedium.copy(fontSize = headlineMedium.size, lineHeight = headlineMedium.lineHeight),
        headlineSmall = base.headlineSmall.copy(fontSize = headlineSmall.size, lineHeight = headlineSmall.lineHeight),
        titleLarge = base.titleLarge.copy(fontSize = titleLarge.size, lineHeight = titleLarge.lineHeight),
        titleMedium = base.titleMedium.copy(fontSize = titleMedium.size, lineHeight = titleMedium.lineHeight),
        titleSmall = base.titleSmall.copy(fontSize = titleSmall.size, lineHeight = titleSmall.lineHeight),
        bodyLarge = base.bodyLarge.copy(fontSize = bodyLarge.size, lineHeight = bodyLarge.lineHeight),
        bodyMedium = base.bodyMedium.copy(fontSize = bodyMedium.size, lineHeight = bodyMedium.lineHeight),
        bodySmall = base.bodySmall.copy(fontSize = bodySmall.size, lineHeight = bodySmall.lineHeight),
        labelLarge = base.labelLarge.copy(fontSize = labelLarge.size, lineHeight = labelLarge.lineHeight),
        labelMedium = base.labelMedium.copy(fontSize = labelMedium.size, lineHeight = labelMedium.lineHeight),
        labelSmall = base.labelSmall.copy(fontSize = labelSmall.size, lineHeight = labelSmall.lineHeight),
    )
}

/** 圆角桥接：M3 Shapes 五档 1:1。 */
val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(AppRadius.extraSmall),
    small = RoundedCornerShape(AppRadius.small),
    medium = RoundedCornerShape(AppRadius.medium),
    large = RoundedCornerShape(AppRadius.large),
    extraLarge = RoundedCornerShape(AppRadius.extraLarge),
)
