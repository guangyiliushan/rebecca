package top.guangyiliushan.rebecca.design.tokens

import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

/** 字阶项：字号 + 行高（M3 Typography 的 size/lineHeight 对）。 */
data class AppTextStyle(val size: TextUnit, val lineHeight: TextUnit)

/**
 * 字阶令牌（frontend-design-system v0.3 §3.2：不再维护自定义档，沿用 M3 官方 15 档，零翻译）。
 * shadcn 映射：text-xs→bodySmall(12)；text-sm→bodyMedium(14)；text-base→bodyLarge(16)；
 *   text-lg(18) 放弃 → titleMedium(16)（18 在 M3 无档位，22 对弹层过大）；
 *   跨断点字号（md:text-sm）v1 不做，输入框固定 bodyLarge(16)。
 */
data class AppType(
    val displayLarge: AppTextStyle = AppTextStyle(57.sp, 64.sp),
    val displayMedium: AppTextStyle = AppTextStyle(45.sp, 52.sp),
    val displaySmall: AppTextStyle = AppTextStyle(36.sp, 44.sp),
    val headlineLarge: AppTextStyle = AppTextStyle(32.sp, 40.sp),
    val headlineMedium: AppTextStyle = AppTextStyle(28.sp, 36.sp),
    val headlineSmall: AppTextStyle = AppTextStyle(24.sp, 32.sp),
    val titleLarge: AppTextStyle = AppTextStyle(22.sp, 28.sp),
    val titleMedium: AppTextStyle = AppTextStyle(16.sp, 24.sp),
    val titleSmall: AppTextStyle = AppTextStyle(14.sp, 20.sp),
    val bodyLarge: AppTextStyle = AppTextStyle(16.sp, 24.sp),
    val bodyMedium: AppTextStyle = AppTextStyle(14.sp, 20.sp),
    val bodySmall: AppTextStyle = AppTextStyle(12.sp, 16.sp),
    val labelLarge: AppTextStyle = AppTextStyle(14.sp, 20.sp),
    val labelMedium: AppTextStyle = AppTextStyle(12.sp, 16.sp),
    val labelSmall: AppTextStyle = AppTextStyle(11.sp, 16.sp),
)
