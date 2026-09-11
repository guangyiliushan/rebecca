package top.guangyiliushan.rebecca.design.tokens

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.test.Test
import kotlin.test.assertEquals

/** Phase 2 T1 令牌终值断言（frontend-design-system v0.3 §3.2 + §10.6 动效基线）。 */
class TokensTest {
    @Test
    fun spacing_hasOfficialSteps() {
        assertEquals(2.dp, AppSpacing.xxs)
        assertEquals(4.dp, AppSpacing.xs)
        assertEquals(8.dp, AppSpacing.sm)
        assertEquals(16.dp, AppSpacing.md)
        assertEquals(24.dp, AppSpacing.lg)
        assertEquals(32.dp, AppSpacing.xl)
        assertEquals(48.dp, AppSpacing.xxl)
        assertEquals(6.dp, AppSpacing.tight)
        assertEquals(10.dp, AppSpacing.compact)
        assertEquals(12.dp, AppSpacing.controlPadding)
    }

    @Test
    fun radius_alignedToM3Shapes() {
        assertEquals(4.dp, AppRadius.extraSmall)
        assertEquals(8.dp, AppRadius.small)
        assertEquals(12.dp, AppRadius.medium)
        assertEquals(16.dp, AppRadius.large)
        assertEquals(24.dp, AppRadius.extraLarge)
        assertEquals(50, AppRadius.full)
    }

    @Test
    fun type_alignedToM3Official15Tiers() {
        val type = AppType()
        // title 系
        assertEquals(22.sp, type.titleLarge.size)
        assertEquals(28.sp, type.titleLarge.lineHeight)
        assertEquals(16.sp, type.titleMedium.size)
        assertEquals(24.sp, type.titleMedium.lineHeight)
        assertEquals(14.sp, type.titleSmall.size)
        assertEquals(20.sp, type.titleSmall.lineHeight)
        // body 系（shadcn text-sm → bodyMedium 14）
        assertEquals(16.sp, type.bodyLarge.size)
        assertEquals(24.sp, type.bodyLarge.lineHeight)
        assertEquals(14.sp, type.bodyMedium.size)
        assertEquals(20.sp, type.bodyMedium.lineHeight)
        assertEquals(12.sp, type.bodySmall.size)
        assertEquals(16.sp, type.bodySmall.lineHeight)
        // label 系
        assertEquals(14.sp, type.labelLarge.size)
        assertEquals(20.sp, type.labelLarge.lineHeight)
        assertEquals(12.sp, type.labelMedium.size)
        assertEquals(16.sp, type.labelMedium.lineHeight)
    }

    @Test
    fun controlHeight_shadcnScale() {
        assertEquals(24.dp, AppControlHeight.Xs)
        assertEquals(32.dp, AppControlHeight.Sm)
        assertEquals(36.dp, AppControlHeight.Md)
        assertEquals(40.dp, AppControlHeight.Lg)
        assertEquals(48.dp, AppControlHeight.Xl)
    }

    @Test
    fun iconSize_shadcnScale() {
        assertEquals(12.dp, AppIconSize.Xs)
        assertEquals(16.dp, AppIconSize.Sm)
        assertEquals(20.dp, AppIconSize.Md)
        assertEquals(24.dp, AppIconSize.Lg)
        assertEquals(32.dp, AppIconSize.Xl)
    }

    @Test
    fun overlayWidth_plusMaxHeightRatio() {
        assertEquals(320.dp, AppOverlayWidth.Xs)
        assertEquals(384.dp, AppOverlayWidth.Sm)
        assertEquals(512.dp, AppOverlayWidth.Md)
        assertEquals(640.dp, AppOverlayWidth.Lg)
        assertEquals(0.8f, AppOverlaySize.MaxHeightRatio)
    }

    @Test
    fun motion_platformBaseline() {
        assertEquals(200, AppMotion.enterMs)
        assertEquals(100, AppMotion.exitMs)
        assertEquals(0.95f, AppMotion.dialogScale)
    }
}
